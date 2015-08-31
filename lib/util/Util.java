package com.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
	public static String getTime() {
		Date date = new Date();
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}
	public static String getDate() {
		Date date = new Date();
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}
}
