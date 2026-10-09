package com.example.hibernateissue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;

/**
 * Generic DAO helper: before every create()/merge(), the {@code @EmbeddedId}'s String fields
 * are right-padded with spaces to their {@code @Column} length (fillId) - a common pattern
 * with fixed-length char(n) business keys.
 */
public final class GenericDaoSupport
{
    private GenericDaoSupport()
    {
    }

    public static <E> void create(EntityManager pEntityManager, E pEntity)
    {
        fillId(pEntity);
        pEntityManager.persist(pEntity);
    }

    @SuppressWarnings("unchecked")
    public static <E> E merge(EntityManager pEntityManager, E pEntity)
    {
        fillId(pEntity);
        return (E) pEntityManager.merge(pEntity);
    }

    private static void fillId(Object pEntity)
    {
        try
        {
            for (Field field : pEntity.getClass().getDeclaredFields())
            {
                if (field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(EmbeddedId.class))
                {
                    field.setAccessible(true);
                    Object id = field.get(pEntity);
                    if (id != null)
                    {
                        padEmbeddedId(id);
                    }
                }
            }
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void padEmbeddedId(Object pId) throws IllegalAccessException
    {
        Class<?> idClass = pId.getClass();
        if (!idClass.isAnnotationPresent(Embeddable.class))
        {
            return;
        }
        for (Field field : idClass.getDeclaredFields())
        {
            Column columnAnnotation = field.getAnnotation(Column.class);
            if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())
                && field.getType().equals(String.class) && columnAnnotation != null)
            {
                field.setAccessible(true);
                String value = (String) field.get(pId);
                int length = columnAnnotation.length();
                if (value != null && value.length() != length)
                {
                    field.set(pId, CommonUtils.fillWith(value, ' ', length));
                }
            }
        }
    }
}
