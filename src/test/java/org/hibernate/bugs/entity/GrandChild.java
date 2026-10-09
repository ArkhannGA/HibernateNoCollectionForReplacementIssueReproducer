package org.hibernate.bugs.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * Generic "grand-child" entity, many-to-one towards its {@link Child}.
 */
@Entity
@Table(name = "GRANDCHILD")
public class GrandChild
    implements Serializable
{
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private GrandChildId id;

    @Version
    @Column(name = "VERSION_TS")
    private Timestamp versionTs;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "ORG_CODE", referencedColumnName = "ORG_CODE", insertable = false, updatable = false),
            @JoinColumn(name = "PARENT_NUM", referencedColumnName = "PARENT_NUM", insertable = false, updatable = false),
            @JoinColumn(name = "CHILD_CODE", referencedColumnName = "CHILD_CODE", insertable = false, updatable = false),
            @JoinColumn(name = "ORIGIN_FLAG", referencedColumnName = "ORIGIN_FLAG", insertable = false, updatable = false)})
    private Child child;

    public GrandChild()
    {
    }

    public GrandChild(GrandChildId pId)
    {
        id = pId;
    }

    public GrandChildId getId()
    {
        return id;
    }

    public void setId(GrandChildId pId)
    {
        id = pId;
    }

    public Child getChild()
    {
        return child;
    }

    public void setChild(Child pChild)
    {
        child = pChild;
    }

    @Override
    public String toString()
    {
        return "GrandChild [id=" + id + "]";
    }
}
