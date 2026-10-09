package com.example.hibernateissue;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Composite id of {@link GrandChild}: the full {@link ChildId} columns plus one extra
 * discriminating code, mirroring a typical "child of a child" business key.
 */
@Embeddable
public class GrandChildId
    implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Column(name = "ORG_CODE", length = ChildId.ORG_CODE_LENGTH, columnDefinition = "char("
        + ChildId.ORG_CODE_LENGTH + ")")
    private String orgCode;

    @Column(name = "PARENT_NUM")
    private Integer parentNum;

    @Column(name = "CHILD_CODE", length = ChildId.CHILD_CODE_LENGTH, columnDefinition = "char("
        + ChildId.CHILD_CODE_LENGTH + ")")
    private String childCode;

    @Column(name = "ORIGIN_FLAG", length = ChildId.ORIGIN_FLAG_LENGTH, columnDefinition = "char("
        + ChildId.ORIGIN_FLAG_LENGTH + ")")
    private String originFlag;

    @Column(name = "GRANDCHILD_CODE", length = 10, columnDefinition = "char(10)")
    private String grandChildCode;

    public GrandChildId()
    {
    }

    public GrandChildId(String pOrgCode, Integer pParentNum, String pChildCode, String pOriginFlag,
        String pGrandChildCode)
    {
        orgCode = CommonUtils.untrim(pOrgCode);
        parentNum = pParentNum;
        childCode = CommonUtils.untrim(pChildCode);
        originFlag = pOriginFlag;
        grandChildCode = CommonUtils.untrim(pGrandChildCode);
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

    public String getGrandChildCode()
    {
        return CommonUtils.trim(grandChildCode);
    }

    public void setGrandChildCode(String pGrandChildCode)
    {
        grandChildCode = CommonUtils.untrim(pGrandChildCode);
    }

    @Override
    public int hashCode()
    {
        int result = 1;
        result = 31 * result + (orgCode == null ? 0 : CommonUtils.trim(orgCode).hashCode());
        result = 31 * result + (parentNum == null ? 0 : parentNum.hashCode());
        result = 31 * result + (childCode == null ? 0 : CommonUtils.trim(childCode).hashCode());
        result = 31 * result + (originFlag == null ? 0 : originFlag.hashCode());
        return 31 * result + (grandChildCode == null ? 0 : CommonUtils.trim(grandChildCode).hashCode());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof GrandChildId other))
        {
            return false;
        }
        return Objects.equals(CommonUtils.trim(orgCode), CommonUtils.trim(other.orgCode)) && Objects.equals(
            parentNum, other.parentNum)
            && Objects.equals(CommonUtils.trim(childCode),
                CommonUtils.trim(other.childCode))
            && Objects.equals(originFlag, other.originFlag) && Objects.equals(
                CommonUtils.trim(grandChildCode), CommonUtils.trim(other.grandChildCode));
    }

    @Override
    public String toString()
    {
        return "GrandChildId [orgCode=" + orgCode + ", parentNum=" + parentNum + ", childCode=" + childCode
            + ", originFlag=" + originFlag + ", grandChildCode=" + grandChildCode + "]";
    }
}
