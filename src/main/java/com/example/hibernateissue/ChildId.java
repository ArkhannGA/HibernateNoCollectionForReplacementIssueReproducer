package com.example.hibernateissue;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Composite id of {@link Child}. {@code equals()}/{@code hashCode()} trim the String parts
 * before comparing, which means an id padded with trailing spaces (as a "fill to column
 * length before persist/merge" helper would do - see {@link GenericDaoSupport}) is still
 * considered equal/identical to the untrimmed one from the persistence context's point of
 * view.
 */
@Embeddable
public class ChildId
    implements Serializable
{
    private static final long serialVersionUID = 1L;

    public static final int ORG_CODE_LENGTH = ParentId.ORG_CODE_LENGTH;

    public static final int CHILD_CODE_LENGTH = 10;

    public static final int ORIGIN_FLAG_LENGTH = 1;

    @Column(name = "ORG_CODE", length = ORG_CODE_LENGTH, columnDefinition = "char(" + ORG_CODE_LENGTH + ")")
    private String orgCode;

    @Column(name = "PARENT_NUM")
    private Integer parentNum;

    @Column(name = "CHILD_CODE", length = CHILD_CODE_LENGTH, columnDefinition = "char(" + CHILD_CODE_LENGTH + ")")
    private String childCode;

    @Column(name = "ORIGIN_FLAG", length = ORIGIN_FLAG_LENGTH, columnDefinition = "char(" + ORIGIN_FLAG_LENGTH + ")")
    private String originFlag;

    public ChildId()
    {
    }

    public ChildId(String pOrgCode, Integer pParentNum, String pChildCode, String pOriginFlag)
    {
        orgCode = CommonUtils.untrim(pOrgCode);
        parentNum = pParentNum;
        childCode = CommonUtils.untrim(pChildCode);
        originFlag = pOriginFlag;
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

    public String getChildCode()
    {
        return CommonUtils.trim(childCode);
    }

    public void setChildCode(String pChildCode)
    {
        childCode = CommonUtils.untrim(pChildCode);
    }

    public String getOriginFlag()
    {
        return originFlag;
    }

    public void setOriginFlag(String pOriginFlag)
    {
        originFlag = pOriginFlag;
    }

    @Override
    public int hashCode()
    {
        int result = 1;
        result = 31 * result + (orgCode == null ? 0 : CommonUtils.trim(orgCode).hashCode());
        result = 31 * result + (parentNum == null ? 0 : parentNum.hashCode());
        result = 31 * result + (childCode == null ? 0 : CommonUtils.trim(childCode).hashCode());
        return 31 * result + (originFlag == null ? 0 : originFlag.hashCode());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof ChildId other))
        {
            return false;
        }
        return Objects.equals(CommonUtils.trim(orgCode), CommonUtils.trim(other.orgCode)) && Objects.equals(
            parentNum, other.parentNum)
            && Objects.equals(CommonUtils.trim(childCode),
                CommonUtils.trim(other.childCode))
            && Objects.equals(originFlag, other.originFlag);
    }

    @Override
    public String toString()
    {
        return "ChildId [orgCode=" + orgCode + ", parentNum=" + parentNum + ", childCode=" + childCode
            + ", originFlag=" + originFlag + "]";
    }
}
