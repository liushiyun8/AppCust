package foundation.util;

import java.util.ArrayList;

public class CompareUtils {

	static public boolean compareString(String val1, String val2) {
		if (StringUtil.isEmpty(val1) && StringUtil.isEmpty(val2)) {
			return true;
		} else if (!StringUtil.isEmpty(val1) && !StringUtil.isEmpty(val2)) {
			return val1.equals(val2);
		} else {
			return false;
		}
	}

	static public boolean compareInt(int val1, int val2) {
		if (val1 == val2) {
			return true;
		} else {
			return val1 + "".compareTo(val2 + "") == 0;
		}
	}

	static public boolean compareFloat(Float val1, Float val2) {
		if (val1 == val2) {
			return true;
		} else if (val1 == null || val2 == null) {
			return false;
		} else {
			return val1.compareTo(val2) == 0;
		}
	}

	static public boolean compareLong(Long val1, Long val2) {
		if (val1 == val2) {
			return true;
		} else if (val1 == null || val2 == null) {
			return false;
		} else {
			return val1.compareTo(val2) == 0;
		}
	}

	public static <T> boolean compareArray(ArrayList<T> array1, ArrayList<T> array2) {

		if (array1 == array2)
			return true;

		if (array1 == null && array2.size() == 0)
			return true;

		if (array2 == null && array1.size() == 0)
			return true;

		if (array1 == null || array2 == null)
			return false;

		if (array1.size() != array2.size())
			return false;

		for (int i = 0; i < array1.size(); i++) {
			T o1 = array1.get(i);
			T o2 = array2.get(i);

			if (!o1.equals(o2))
				return false;
		}

		return true;
	}
}
