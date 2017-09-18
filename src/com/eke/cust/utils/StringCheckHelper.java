package com.eke.cust.utils;

public class StringCheckHelper {

	public static boolean isEmpty(String str) {
		if (str == null || str.length() == 0 || str.equals("null"))
			return true;
		else
			return false;
	}

}
