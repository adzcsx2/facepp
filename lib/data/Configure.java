package com.util.facepp.data;

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
		sProps.setProperty(API_KEY, "6fe4aa4dc1e759578632f98f8b6d57ad");
		sProps.setProperty(API_SECRET, "Z3pV3LOer1WNth1ehNxt8lsW3dCt0Joy");
	}
	
	public static String getValue(String key){
		return sProps.getProperty(key);
	}
	
	public static void updateProperties(String key,String value) {
		sProps.setProperty(key, value);
	}
}
