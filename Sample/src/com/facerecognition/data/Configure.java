package com.facerecognition.data;

import java.util.Properties;

/**
 * 
 * @author liliang
 * @date 2012-11-28
 * @desc 常量存于此配置类中
 */
public class Configure {
	public static final String API_KEY = "api_key";
	public static final String API_SECRET = "api_secret";
	private static Properties sProps;
	
	static{
		sProps = new Properties();
		//TODO add your api_key and api_secret here
		sProps.setProperty(API_KEY, "b7d79040d0e01b7764432604fc6f9bf4");
		sProps.setProperty(API_SECRET, "lDK64dM11jEWlMEwONSquoLt6zcpEhbG");
	}
	
	public static String getValue(String key){
		return sProps.getProperty(key);
	}
	
	public static void updateProperties(String key,String value) {
		sProps.setProperty(key, value);
	}
}
