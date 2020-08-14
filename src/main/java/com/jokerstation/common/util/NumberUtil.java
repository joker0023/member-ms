package com.jokerstation.common.util;

public class NumberUtil {

	public static String toYuan(Long cent) {
		return Double.toString(cent / 100.0);
	}
}
