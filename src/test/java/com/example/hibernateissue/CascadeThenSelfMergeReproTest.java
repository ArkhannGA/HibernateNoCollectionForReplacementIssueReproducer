package com.example.hibernateissue;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Minimal reproduction of the "No collection for replacement found: ...Child.grandChildren"
 * {@code HibernateException} that only appears since Hibernate 7.
 * <p>
 * This mirrors, step for step, a common production code path:
 * <ol>
 * <li>A "create parent" service builds the list of children to attach to the parent, then
 * REASSIGNS {@code parent.setChildren(new HashSet<>(children))} right before
 * {@code entityManager.persist(parent)}. Cascade=ALL persists the parent, its children, and -
 * cascading once more - each child's (here empty) {@code grandChildren} collection.</li>
 * <li>A "create or update children" service then, for each child (same Java instances, already
 * managed by the cascade):
 * <ul>
 * <li>mutates a few scalar fields,</li>
 * <li>calls {@code entityManager.merge(child)} - a self-merge of an entity that is ALREADY
 * managed in the persistence context,</li>
 * <li>then calls {@code parent.getChildren().add(child)} on the parent's own (lazy,
 * uninitialized) {@code Set<Child>}.</li>
 * </ul>
 * </li>
 * </ol>
 * <p>
 * Because {@code children} is a {@code Set} (not a bag/list), Hibernate cannot queue the
 * {@code add()} operation: it must initialize the collection first to respect Set semantics.
 * That forced initialization happens for the FIRST child processed by the loop, while the
 * SECOND child's own insert (and its empty {@code grandChildren} cascade) is still pending in
 * the persistence context. This is exactly the chain of events that, in Hibernate 7, leaves
 * the second child's {@code grandChildren} PersistentCollection unreachable from
 * {@code StatefulPersistenceContext} by the time its own {@code merge()} call tries to replace
 * it - producing "No collection for replacement found".
 */
class CascadeThenSelfMergeReproTest
{
    private EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    @BeforeEach
    void setUp()
    {
        entityManagerFactory = Persistence.createEntityManagerFactory("hibernateCollectionIssue");
        entityManager = entityManagerFactory.createEntityManager();
    }

    @AfterEach
    void tearDown()
    {
        if (entityManager != null && entityManager.isOpen())
        {
            entityManager.close();
        }
        if (entityManagerFactory != null && entityManagerFactory.isOpen())
        {
            entityManagerFactory.close();
        }
    }

    @Test
    void selfMergingCascadedChildrenThenInitializingTheOwningSetThrows()
    {
        entityManager.getTransaction().begin();

        // --- Step 1: build the parent and its children -------------------------------------

        Parent parent = new Parent(new ParentId("ORGNZ", 1));
        parent.setLabel("TEST PARENT");

        Child child1 = new Child(new ChildId("ORGNZ", 1, "CHILD0001", "I"));
        child1.setParent(parent);

        Child child2 = new Child(new ChildId("ORGNZ", 1, "CHILD0002", "I"));
        child2.setParent(parent);

        // Mirrors a "build then replace the collection just before persist" pattern: the
        // children are first collected into a brand new ArrayList, then the parent's
        // collection is REPLACED by a fresh HashSet built from that list, right before persist.
        List<Child> children = new ArrayList<>(List.of(child1, child2));
        parent.setChildren(new ArrayList<>(children));

        // --- Step 2: persist(parent) ---------------------------------------------------------
        // Cascade=ALL persists parent -> child1, child2 -> (empty) grandChildren of each.
        GenericDaoSupport.create(entityManager, parent);

        // --- Step 3: "create or update children" business logic ------------------------------
        // A "read without JPQL so a managed entity doesn't hit the table" style find() returns
        // the SAME managed instance, with the SAME children PersistentSet installed by the
        // cascade above.
        Parent managedParent = entityManager.find(Parent.class, parent.getId());

        PersistenceException thrown = assertThrows(PersistenceException.class, () -> {
            for (Child child : children)
            {
                // Real business logic typically reads OTHER entities via JPQL/criteria queries
                // BEFORE merging the current child. With FlushMode.AUTO (the default), issuing
                // ANY JPQL/criteria query auto-flushes the WHOLE persistence context first -
                // including the still "fresh from cascade" INSERT and (empty) grandChildren of
                // the NEXT child(ren), who haven't been through this loop's business logic yet.
                entityManager.createQuery("select p from Parent p", Parent.class).getResultList();

                // A couple of scalar mutations.
                child.setLabel("TEST CHILD");
                child.setFlag2("0");

                // Self-merge of an already-managed entity.
                Child merged = GenericDaoSupport.merge(entityManager, child);

                // parent.getChildren().add(child) : touches the parent's Set<Child>.
                managedParent.getChildren().add(merged);
            }
        });

        System.out.println("Reproduced exception: " + thrown);
        Throwable cause = thrown;
        while (cause.getCause() != null)
        {
            cause = cause.getCause();
            System.out.println("  caused by: " + cause);
        }
    }
}
