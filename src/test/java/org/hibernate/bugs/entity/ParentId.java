package org.hibernate.bugs.entity;

import java.io.Serializable;
import java.util.Objects;

import org.hibernate.bugs.utils.CommonUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Composite id of {@link Parent}: a short, fixed-length "organisation code" plus a numeric
 * sequence number. Generic stand-in for a real-world composite business key made of a
 * char(n) code and a sequence number.
 */
@Embeddable
public class ParentId
    implements Serializable
{
    private static final long serialVersionUID = 1L;

    public static final int ORG_CODE_LENGTH = 5;

    @Column(name = "ORG_CODE", length = ORG_CODE_LENGTH, columnDefinition = "char(" + ORG_CODE_LENGTH + ")")
    private String orgCode;

    @Column(name = "PARENT_NUM")
    private Integer parentNum;

    public ParentId()
    {
    }

    public ParentId(String pOrgCode, Integer pParentNum)
    {
        orgCode = CommonUtils.untrim(pOrgCode);
        parentNum = pParentNum;
    }

    public String getOrgCode()
    {
        return CommonUtils.trim(orgCode);
    }

    public void setOrgCode(String pOrgCode)
    {
        orgCode = CommonUtils.untrim(pOrgCode);
    }

    public Integer getParentNum()
    {
        return parentNum;
    }

    public void setParentNum(Integer pParentNum)
    {
        parentNum = pParentNum;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(CommonUtils.trim(orgCode), parentNum);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof ParentId other))
        {
            return false;
        }
        return Objects.equals(CommonUtils.trim(orgCode), CommonUtils.trim(other.orgCode))
            && Objects.equals(parentNum, other.parentNum);
    }

    @Override
    public String toString()
    {
        return "ParentId [orgCode=" + orgCode + ", parentNum=" + parentNum + "]";
    }
}
