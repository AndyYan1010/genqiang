package com.bt.andy.gainstrong.exception;

import android.util.Log;

/**
 * @author AndyYan
 */
public class L {
	private static final String sPrefix = "LOG-";
	private static String contextName(Object o) {
		if (o instanceof String) return sPrefix + (String)o;
		if (o instanceof Class) return sPrefix + ((Class<?>)o).getSimpleName();
		return sPrefix + o.getClass().getSimpleName();
	}

	public static void i(Object o, String s) {
		if(Debug.LOG) Log.i(contextName(o), s);
	}

	public static void i(Object o, String s, Throwable e) {
		if(Debug.LOG) Log.i(contextName(o), s, e);
	}

	public static void i(Object o, Throwable e) {
		i(o, "", e);
	}

	public static void d(Object o, String s) {
		if(Debug.LOG) Log.d(contextName(o), s);
	}

	public static void d(Object o, String s, Throwable e) {
		if(Debug.LOG) Log.d(contextName(o), "", e);
	}

	public static void d(Object o, Throwable e) {
		d(o, "", e);
	}

	public static void e(Object o, String s) {
		if(Debug.LOG) Log.e(contextName(o), s);
		//发送错误统计数据
	}

	public static void e(Object o, String s, Throwable e) {
		if(Debug.LOG) Log.e(contextName(o), s, e);
		//发送错误统计数据
	}

	public static void e(Object o, Throwable e) {
		e(o, "", e);
	}

	public static void w(Object o, String s) {
		if(Debug.LOG) Log.w(contextName(o), s);
	}

	public static void w(Object o, String s, Throwable e) {
		if(Debug.LOG) Log.w(contextName(o), s, e);
	}

	public static void w(Object o, Throwable e) {
		w(o, "", e);
	}
}