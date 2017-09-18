package foundation.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortUtils {

	@SuppressWarnings("hiding")
	static public <T> ArrayList<T> sort(ArrayList<T> array, Comparator<T> comparator) {
		ArrayList<T> result = new ArrayList<T>();
		result.addAll(array);
		Collections.sort(result, comparator);
		return result;
	}

	static public int compare(int a, int b) {
		if (a > b)
			return 1;
		else if (a < b)
			return -1;
		return 0;
	}
	
	static public int compare(long a, long b) {
		if (a > b)
			return 1;
		else if (a < b)
			return -1;
		return 0;
	}
}
