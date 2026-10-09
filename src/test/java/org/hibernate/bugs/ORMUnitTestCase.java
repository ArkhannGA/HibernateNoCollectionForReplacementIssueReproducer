package org.hibernate.bugs;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.bugs.entity.Child;
import org.hibernate.bugs.entity.ChildId;
import org.hibernate.bugs.entity.GrandChild;
import org.hibernate.bugs.entity.Parent;
import org.hibernate.bugs.entity.ParentId;
import org.hibernate.bugs.utils.GenericDaoSupport;

import org.hibernate.cfg.AvailableSettings;

import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.ServiceRegistry;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.hibernate.testing.orm.junit.Setting;
import org.junit.jupiter.api.Test;

import jakarta.persistence.PersistenceException;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using its built-in unit test framework.
 * Although ORMStandaloneTestCase is perfectly acceptable as a reproducer, usage of this class is much preferred.
 * Since we nearly always include a regression test with bug fixes, providing your reproducer using this method
 * simplifies the process.
 * <p>
 * What's even better? Fork hibernate-orm itself, add your test case directly to a module's unit tests, then
 * submit it as a PR!
 */
@DomainModel(annotatedClasses = {
        // Add your entities here.
        Parent.class,
        Child.class,
        GrandChild.class
},
    // If you use *.hbm.xml mappings, instead of annotations, add the mappings here.
    xmlMappings = {
    // "org/hibernate/test/Foo.hbm.xml",
    // "org/hibernate/test/Bar.hbm.xml"
    })
@ServiceRegistry(
    // Add in any settings that are specific to your test. See resources/hibernate.properties for the defaults.
    settings = {
            // For your own convenience to see generated queries:
            @Setting(name = AvailableSettings.SHOW_SQL, value = "true"),
            @Setting(name = AvailableSettings.FORMAT_SQL, value = "true"),
    // @Setting( name = AvailableSettings.GENERATE_STATISTICS, value = "true" ),

    // Add your own settings that are a part of your quarkus configuration:
    // @Setting( name = AvailableSettings.SOME_CONFIGURATION_PROPERTY, value = "SOME_VALUE" ),
    })
@SessionFactory
class ORMUnitTestCase
{

    // Add your tests, using standard JUnit 5.
    @Test
    void hhh123Test(SessionFactoryScope scope)
    {
        scope.inTransaction(session -> {
            // build the parent and its children

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

            // Cascade=ALL persists parent -> child1, child2 -> (empty) grandChildren of each.
            GenericDaoSupport.create(session, parent);

            PersistenceException thrown = assertThrows(PersistenceException.class, () -> {
                for (Child child : children)
                {
                    // Real business logic typically reads OTHER entities via JPQL/criteria queries
                    // BEFORE merging the current child. With FlushMode.AUTO (the default), issuing
                    // ANY JPQL/criteria query auto-flushes the WHOLE persistence context first -
                    // including the still "fresh from cascade" INSERT and (empty) grandChildren of
                    // the NEXT child(ren), who haven't been through this loop's business logic yet.
                    session.createQuery("select p from Parent p", Parent.class).getResultList();

                    // Self-merge of an already-managed entity.
                    GenericDaoSupport.merge(session, child);
                }
            });

            System.out.println("Reproduced exception: " + thrown);
            Throwable cause = thrown;
            while (cause.getCause() != null)
            {
                cause = cause.getCause();
                System.out.println("  caused by: " + cause);
            }
        });
    }
}
