package org.hibernate.bugs.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.bugs.utils.CommonUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * Generic "parent" entity owning a {@code Set<Child>} with {@code CascadeType.ALL} on the
 * inverse (mappedBy) side - the collection that Hibernate's merge code tries to touch when a
 * child is added to it after being self-merged.
 */
@Entity
@Table(name = "PARENT")
public class Parent
    implements Serializable
{
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ParentId id;

    @Version
    @Column(name = "VERSION_TS")
    private Timestamp versionTs;

    @Column(name = "LABEL", length = 40)
    private String label;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Child> children = new ArrayList<>();

    public Parent()
    {
    }

    public Parent(ParentId pId)
    {
        id = pId;
    }

    public ParentId getId()
    {
        return id;
    }

    public void setId(ParentId pId)
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

    public List<Child> getChildren()
    {
        return children;
    }

    public void setChildren(List<Child> pChildren)
    {
        children = pChildren;
    }

    @Override
    public String toString()
    {
        return "Parent [id=" + id + "]";
    }
}
