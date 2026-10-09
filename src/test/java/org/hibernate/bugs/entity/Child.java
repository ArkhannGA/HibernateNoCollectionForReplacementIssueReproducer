package org.hibernate.bugs.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.bugs.utils.CommonUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * Generic "child" entity, in a many-to-one relationship with its {@link Parent} and a
 * one-to-many relationship with {@link GrandChild}. Notably:
 * <ul>
 * <li>{@link #parent} is a {@code @ManyToOne} with {@code CascadeType.ALL} whose join columns
 * are shared with - and marked {@code insertable=false}/{@code updatable=false} because of -
 * the {@code @EmbeddedId}. This is exactly what produces the "HHH000502 ... property was
 * modified but won't be updated because the property is immutable" warning.</li>
 * <li>{@link #grandChildren} is the collection that Hibernate 7 complains about with
 * "No collection for replacement found" when this entity is self-merged.</li>
 * </ul>
 */
@Entity
@Table(name = "CHILD")
public class Child
    implements Serializable
{
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ChildId id;

    @Version
    @Column(name = "VERSION_TS")
    private Timestamp versionTs;

    @Column(name = "LABEL", length = 40)
    private String label;

    @Column(name = "FLAG1", length = 3)
    private String flag1;

    @Column(name = "FLAG2", length = 1)
    private String flag2;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "ORG_CODE", referencedColumnName = "ORG_CODE", insertable = false, updatable = false),
            @JoinColumn(name = "PARENT_NUM", referencedColumnName = "PARENT_NUM", insertable = false, updatable = false)})
    private Parent parent;

    @OneToMany(mappedBy = "child", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<GrandChild> grandChildren = new HashSet<>();

    public Child()
    {
    }

    public Child(ChildId pId)
    {
        id = pId;
    }

    public ChildId getId()
    {
        return id;
    }

    public void setId(ChildId pId)
    {
        id = pId;
    }

    public String getLabel()
    {
        return CommonUtils.trim(label);
    }

    public void setLabel(String pLabel)
    {
        label = CommonUtils.untrim(pLabel);
    }

    public String getFlag1()
    {
        return CommonUtils.trim(flag1);
    }

    public void setFlag1(String pFlag1)
    {
        flag1 = CommonUtils.untrim(pFlag1);
    }

    public String getFlag2()
    {
        return CommonUtils.trim(flag2);
    }

    public void setFlag2(String pFlag2)
    {
        flag2 = CommonUtils.untrim(pFlag2);
    }

    public Parent getParent()
    {
        return parent;
    }

    public void setParent(Parent pParent)
    {
        parent = pParent;
    }

    public Set<GrandChild> getGrandChildren()
    {
        return grandChildren;
    }

    public void setGrandChildren(Set<GrandChild> pGrandChildren)
    {
        grandChildren = pGrandChildren;
    }

    @Override
    public String toString()
    {
        return "Child [id=" + id + "]";
    }
}
