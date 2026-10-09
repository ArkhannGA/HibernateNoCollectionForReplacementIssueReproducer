package org.hibernate.bugs.utils;

/**
 * Generic string helper reproducing the "trim on read / pad with trailing spaces on write"
 * behaviour common in fixed-length char(n) column mappings, since this pattern was suspected
 * to be involved in the original issue.
 */
public final class CommonUtils
{
    private CommonUtils()
    {
    }

    public static String trim(String pValue)
    {
        return pValue == null ? null : pValue.stripTrailing();
    }

    /**
     * Does NOT pad here (padding to column length is done by fillWith)
     */
    public static String untrim(String pValue)
    {
        return pValue;
    }

    public static String fillWith(String pValue, char pFiller, int pLength)
    {
        if (pValue == null)
        {
            return null;
        }
        StringBuilder sb = new StringBuilder(pValue);
        while (sb.length() < pLength)
        {
            sb.append(pFiller);
        }
        return sb.toString();
    }
}
