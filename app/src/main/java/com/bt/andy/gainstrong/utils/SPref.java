package com.bt.andy.gainstrong.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;


/**
 * @author AndyYan
 */
public class SPref {

	public static void saveAsFile(Context context, String fileName, String content) {
		FileVersioned.saveAsFileInDefaultDir(context, fileName, content);
	}

	public static void saveAsFile(Context context, Object o, String key, String content) {
		saveAsFile(context, fileName(o, key), content);
	}

	public static void saveAsFile(Context context, Class<?> c, String key, String content) {
		saveAsFile(context, fileName(c, key), content);
	}

	public static String getFromFile(Context context, String fileName) {
		return FileVersioned.getStringFromFileInDefaultDir(context, fileName);
	}

	public static String getFromFile(Context context, Object o, String key) {
		return getFromFile(context, fileName(o, key));
	}

	public static String getFromFile(Context context, Class<?> c, String key) {
		return getFromFile(context, fileName(c, key));
	}

	public static void saveBooleanAsFile(Context context, String fileName, boolean content) {
		FileVersioned.saveAsFileInDefaultDir(context, fileName, content ? sTrue : sFalse);
	}

	public static void saveBooleanAsFile(Context context, Object o, String key, boolean content) {
		saveBooleanAsFile(context, fileName(o, key), content);
	}

	public static void saveBooleanAsFile(Context context, Class<?> c, String key, boolean content) {
		saveBooleanAsFile(context, fileName(c, key), content);
	}

	public static boolean getBooleanFromFile(Context context, String fileName) {
		String s = FileVersioned.getStringFromFileInDefaultDir(context, fileName);
		return s != null && s.equals(sTrue);
	}

	public static boolean getBooleanFromFile(Context context, Object o, String key) {
		return getBooleanFromFile(context, fileName(o, key));
	}

	public static boolean getBooleanFromFile(Context context, Class<?> c, String key) {
		return getBooleanFromFile(context, fileName(c, key));
	}

	public static Editor edit(Context context, String fileName) {
		return getSPref(context, fileName).edit();
	}

	public static SharedPreferences getSPref(Context context, String fileName) {
		return context.getSharedPreferences(fileName, getMode());
	}

	public static Editor edit(Context context, Object o) {
		return getSPref(context, o).edit();
	}

	public static SharedPreferences getSPref(Context context, Object o) {
		return getSPref(context, fileName(o));
	}

	public static Editor edit(Context context, Class<?> c) {
		return getSPref(context, c).edit();
	}

	public static SharedPreferences getSPref(Context context, Class<?> c) {
		return getSPref(context, fileName(c));
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private static int getMode() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? Context.MODE_MULTI_PROCESS : Context.MODE_PRIVATE;
	}

	private static String fileName(Object o) {
		if(o instanceof String) return (String)o;
		return replace$(fileName(o.getClass()));
	}

	private static String fileName(Class<?> c) {
		return replace$(c.getName());
	}

	private static String fileName(String s, String key) {
		return replace$(s + (key == null ? "" : "." + key));
	}

	private static String fileName(Object o, String key) {
		return fileName(fileName(o), key);
	}

	private static String fileName(Class<?> c, String key) {
		return fileName(fileName(c), key);
	}

	private static String replace$(String s) {
		return s.replaceAll("\\$", ".");	//不然会被解释为正则的的末尾字符，然后在末尾加上.字符
	}

	private static final String sTrue		= "1";
	private static final String sFalse		= "0";

	//扩展 存储对象
	/**
	 * 根据key和预期的value类型获取value的值
	 *
	 * @param key
	 * @param clazz
	 * @return
	 */
	public static <T> T getValue(Context context,String key, Class<T> clazz) {
		if (context == null) {
			throw new RuntimeException("请先调用带有context，name参数的构造！");
		}
		SharedPreferences sp = context.getSharedPreferences(fileName(clazz), Context.MODE_PRIVATE);
		return getValue(key, clazz, sp);
	}

	/**
	 * 针对复杂类型存储<对象>
	 *
	 * @param key
	 * @param
	 */
	public static void setObject(Context context,Class<?> c,String key, Object object) {
		SharedPreferences sp = context.getSharedPreferences(fileName(c), Context.MODE_PRIVATE);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = null;
		try {

			out = new ObjectOutputStream(baos);
			out.writeObject(object);
			String objectVal = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
			Editor editor = sp.edit();
			editor.putString(key, objectVal);
			editor.commit();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getObject(Context context, Class<T> clazz,String key) {
		SharedPreferences sp = context.getSharedPreferences(fileName(clazz), Context.MODE_PRIVATE);
		if (sp.contains(key)) {
			String objectVal = sp.getString(key, null);
			byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
			ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(bais);
				T t = (T) ois.readObject();
				return t;
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					if (bais != null) {
						bais.close();
					}
					if (ois != null) {
						ois.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 对于外部不可见的过渡方法
	 *
	 * @param key
	 * @param clazz
	 * @param sp
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <T> T getValue(String key, Class<T> clazz, SharedPreferences sp) {
		T t;
		try {

			t = clazz.newInstance();

			if (t instanceof Integer) {
				return (T) Integer.valueOf(sp.getInt(key, 0));
			} else if (t instanceof String) {
				return (T) sp.getString(key, "");
			} else if (t instanceof Boolean) {
				return (T) Boolean.valueOf(sp.getBoolean(key, false));
			} else if (t instanceof Long) {
				return (T) Long.valueOf(sp.getLong(key, 0L));
			} else if (t instanceof Float) {
				return (T) Float.valueOf(sp.getFloat(key, 0L));
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
			Log.e("system", "类型输入错误或者复杂类型无法解析[" + e.getMessage() + "]");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			Log.e("system", "类型输入错误或者复杂类型无法解析[" + e.getMessage() + "]");
		}
		Log.e("system", "无法找到" + key + "对应的值");
		return null;
	}


}
