package com.qwerty.data;

import java.util.Comparator;

/**
 * @author vinitnayak
 * Comparator for line objects used to return lines in correct order.
 */
public class LineComparator implements Comparator<Line> {

	@Override
	public int compare(Line arg0, Line arg1) {
		if (arg0.getmPosition() > arg1.getmPosition()) {
			return 1;
		}
		
		if (arg0.getmPosition() < arg1.getmPosition()) {
			return -1;
		}
		
		return 0;
	}
	
}