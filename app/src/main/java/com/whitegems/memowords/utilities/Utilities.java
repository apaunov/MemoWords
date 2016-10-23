package com.whitegems.memowords.utilities;

/**
 * Created by apaunov on 2015-12-03.
 */
public class Utilities
{
    public static int convertBooleanToInt(boolean boolToConvert)
    {
        return (boolToConvert) ? 1 : 0;
    }

    public static boolean convertIntToBoolean(int intToConvert)
    {
        return (intToConvert > 0);
    }
}
