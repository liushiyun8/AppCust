package com.eke.cust.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * 将JSonObject的数据转成对应的类 要求，jsonObject里面的name要和类的成员变量的名字要一样
 * 
 */
public class TransformUtil {
	private static final String TAG = "TransformUtil";

	public static <T> T getEntityFromJson(JSONObject json, Class<T> className)
			throws InstantiationException, IllegalAccessException {
		T entity = className.newInstance();
		Field[] fields = entity.getClass().getDeclaredFields();

		for (Field field : fields) {
			try {
				if (json.opt(field.getName()) != null
						&& !json.opt(field.getName()).toString().equals("null")) {
					field.setAccessible(true);
					field.set(entity, json.opt(field.getName()));
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG, e.toString());
			}

		}
		return entity;
	}

	public static <T> List<T> getListFromArray(JSONArray array,
			Class<T> className) {
		List<T> list = new ArrayList<T>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject json;
			try {
				json = array.getJSONObject(i);
				T entity = className.newInstance();
				Field[] fields = entity.getClass().getDeclaredFields();
				for (Field field : fields) {
					try {
						if (json.opt(field.getName()) != null) {
							field.setAccessible(true);
							field.set(entity, json.opt(field.getName()));
						}
					} catch (Exception e) {

					}

				}
				list.add(entity);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		return list;
	}
	 /**
     * get Long from jsonObject
     *
     * @param jsonObject
     * @param key
     * @param defaultValue
     * @return <ul>
     * <li>if jsonObject is null, return defaultValue</li>
     * <li>if key is null or empty, return defaultValue</li>
     * <li>if {@link JSONObject#getLong(String)} exception, return defaultValue</li>
     * <li>return {@link JSONObject#getLong(String)}</li>
     * </ul>
     */
    public static Long getLong(JSONObject jsonObject, String key, Long defaultValue) {
        if (jsonObject == null || StringUtils.isEmpty(key)) {
            return defaultValue;
        }

        try {
            return jsonObject.getLong(key);
        } catch (JSONException e) {
          
            return defaultValue;
        }
    }

    /**
     * get Long from jsonData
     *
     * @param jsonData
     * @param key
     * @param defaultValue
     * @return <ul>
     * <li>if jsonObject is null, return defaultValue</li>
     * <li>if jsonData {@link JSONObject#JSONObject(String)} exception, return defaultValue</li>
     * <li>return </li>
     * </ul>
     */
    public static Long getLong(String jsonData, String key, Long defaultValue) {
        if (StringUtils.isEmpty(jsonData)) {
            return defaultValue;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            return getLong(jsonObject, key, defaultValue);
        } catch (JSONException e) {
            
            return defaultValue;
        }
    }

    /**
     * @param jsonObject
     * @param key
     * @param defaultValue
     * @return
     * @see JSONUtils#getLong(JSONObject, String, Long)
     */
    public static long getLong(JSONObject jsonObject, String key, long defaultValue) {
        return getLong(jsonObject, key, (Long) defaultValue);
    }

    /**
     * @param jsonData
     * @param key
     * @param defaultValue
     * @return
     * @see JSONUtils#getLong(String, String, Long)
     */
    public static long getLong(String jsonData, String key, long defaultValue) {
        return getLong(jsonData, key, (Long) defaultValue);
    }

    /**
     * get Int from jsonObject
     *
     * @param jsonObject
     * @param key
     * @param defaultValue
     * @return <ul>
     * <li>if jsonObject is null, return defaultValue</li>
     * <li>if key is null or empty, return defaultValue</li>
     * <li>if {@link JSONObject#getInt(String)} exception, return defaultValue</li>
     * <li>return {@link JSONObject#getInt(String)}</li>
     * </ul>
     */
    public static Integer getInt(JSONObject jsonObject, String key, Integer defaultValue) {
        if (jsonObject == null || StringUtils.isEmpty(key)) {
            return defaultValue;
        }

        try {
            return jsonObject.getInt(key);
        } catch (JSONException e) {
         
            return defaultValue;
        }
    }

    /**
     * get Int from jsonData
     *
     * @param jsonData
     * @param key
     * @param defaultValue
     * @return <ul>
     * <li>if jsonObject is null, return defaultValue</li>
     * <li>if jsonData {@link JSONObject#JSONObject(String)} exception, return defaultValue</li>
     * <li>return </li>
     * </ul>
     */
    public static Integer getInt(String jsonData, String key, Integer defaultValue) {
        if (StringUtils.isEmpty(jsonData)) {
            return defaultValue;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            return getInt(jsonObject, key, defaultValue);
        } catch (JSONException e) {
          
            return defaultValue;
        }
    }

    /**
     * @param jsonObject
     * @param key
     * @param defaultValue
     * @return
     * @see JSONUtils#getInt(JSONObject, String, Integer)
     */
    public static int getInt(JSONObject jsonObject, String key, int defaultValue) {
        return getInt(jsonObject, key, (Integer) defaultValue);
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     * @see JSONUtils#getInt(String, String, Integer)
     */
    public static int getInt(String jsonData, String key, int defaultValue) {
        return getInt(jsonData, key, (Integer) defaultValue);
    }

    /**
     * get Double from jsonObject
     *
     * @param jsonObject
     * @param key
     * @param defaultValue
     * @return <ul>
     * <li>if jsonObject is null, return defaultValue</li>
     * <li>if key is null or empty, return defaultValue</li>
     * <li>if {@link JSONObject#getDouble(String)} exception, return defaultValue</li>
     * <li>return {@link JSONObject#getDouble(String)}</li>
     * </ul>
     */
    public static Double getDouble(JSONObject jsonObject, String key, Double defaultValue) {
        if (jsonObject == null || StringUtils.isEmpty(key)) {
            return defaultValue;
        }

        try {
            return jsonObject.getDouble(key);
        } catch (JSONException e) {
          
            return defaultValue;
        }
    }

    /**
     * get Double from jsonData
     *
     * @param jsonData
     * @param key
     * @param defaultValue
     * @return <ul>
     * <li>if jsonObject is null, return defaultValue</li>
     * <li>if jsonData {@link JSONObject#JSONObject(String)} exception, return defaultValue</li>
     * <li>return </li>
     * </ul>
     */
    public static Double getDouble(String jsonData, String key, Double defaultValue) {
        if (StringUtils.isEmpty(jsonData)) {
            return defaultValue;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            return getDouble(jsonObject, key, defaultValue);
        } catch (JSONException e) {
          
            return defaultValue;
        }
    }

    /**
     * @param jsonObject
     * @param key
     * @param defaultValue
     * @return
     * @see JSONUtils#getDouble(JSONObject, String, Double)
     */
    public static double getDouble(JSONObject jsonObject, String key, double defaultValue) {
        return getDouble(jsonObject, key, (Double) defaultValue);
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     * @see JSONUtils#getDouble(String, String, Double)
     */
    public static double getDouble(String jsonData, String key, double defaultValue) {
        return getDouble(jsonData, key, (Double) defaultValue);
    }
	/**
	 * get String from jsonObject
	 * 
	 * @param jsonObject
	 * @param key
	 * @param defaultValue
	 * @return <ul>
	 *         <li>if jsonObject is null, return defaultValue</li>
	 *         <li>if key is null or empty, return defaultValue</li>
	 *         <li>if {@link JSONObject#getString(String)} exception, return
	 *         defaultValue</li>
	 *         <li>return {@link JSONObject#getString(String)}</li>
	 *         </ul>
	 */
	public static String getString(JSONObject jsonObject, String key,
			String defaultValue) {
		if (jsonObject == null || StringUtils.isEmpty(key)) {
			return defaultValue;
		}

		try {
			return jsonObject.getString(key);
		} catch (JSONException e) {

			return defaultValue;
		}
	}

	/**
	 * get String from jsonData
	 * 
	 * @param jsonData
	 * @param key
	 * @param defaultValue
	 * @return <ul>
	 *         <li>if jsonObject is null, return defaultValue</li>
	 *         <li>if jsonData {@link JSONObject#JSONObject(String)} exception,
	 *         return defaultValue</li>
	 *         </ul>
	 */
	public static String getString(String jsonData, String key,
			String defaultValue) {
		if (StringUtils.isEmpty(jsonData)) {
			return defaultValue;
		}

		try {
			JSONObject jsonObject = new JSONObject(jsonData);
			return getString(jsonObject, key, defaultValue);
		} catch (JSONException e) {

			return defaultValue;
		}
	}

	/**
	 * get String from jsonObject
	 * 
	 * @param jsonObject
	 * @param defaultValue
	 * @param keyArray
	 * @return <ul>
	 *         <li>if jsonObject is null, return defaultValue</li>
	 *         <li>if keyArray is null or empty, return defaultValue</li>
	 *         <li>get {@link #getJSONObject(JSONObject, String, JSONObject)} by
	 *         recursion, return it. if anyone is null, return directly</li>
	 *         </ul>
	 */
	public static String getStringCascade(JSONObject jsonObject,
			String defaultValue, String... keyArray) {
		if (jsonObject == null || ArrayUtils.isEmpty(keyArray)) {
			return defaultValue;
		}

		String data = jsonObject.toString();
		for (String key : keyArray) {
			data = getStringCascade(data, key, defaultValue);
			if (data == null) {
				return defaultValue;
			}
		}
		return data;
	}

	/**
	 * get String from jsonData
	 * 
	 * @param jsonData
	 * @param defaultValue
	 * @param keyArray
	 * @return <ul>
	 *         <li>if jsonData is null, return defaultValue</li>
	 *         <li>if keyArray is null or empty, return defaultValue</li>
	 *         <li>get {@link #getJSONObject(JSONObject, String, JSONObject)} by
	 *         recursion, return it. if anyone is null, return directly</li>
	 *         </ul>
	 */
	public static String getStringCascade(String jsonData, String defaultValue,
			String... keyArray) {
		if (StringUtils.isEmpty(jsonData)) {
			return defaultValue;
		}

		String data = jsonData;
		for (String key : keyArray) {
			data = getString(data, key, defaultValue);
			if (data == null) {
				return defaultValue;
			}
		}
		return data;
	}

	/**
	 * get String array from jsonObject
	 * 
	 * @param jsonObject
	 * @param key
	 * @param defaultValue
	 * @return <ul>
	 *         <li>if jsonObject is null, return defaultValue</li>
	 *         <li>if key is null or empty, return defaultValue</li>
	 *         <li>if {@link JSONObject#getJSONArray(String)} exception, return
	 *         defaultValue</li>
	 *         <li>if {@link JSONArray#getString(int)} exception, return
	 *         defaultValue</li>
	 *         <li>return string array</li>
	 *         </ul>
	 */
	public static String[] getStringArray(JSONObject jsonObject, String key,
			String[] defaultValue) {
		if (jsonObject == null || StringUtils.isEmpty(key)) {
			return defaultValue;
		}

		try {
			JSONArray statusArray = jsonObject.getJSONArray(key);
			if (statusArray != null) {
				String[] value = new String[statusArray.length()];
				for (int i = 0; i < statusArray.length(); i++) {
					value[i] = statusArray.getString(i);
				}
				return value;
			}
		} catch (JSONException e) {

			return defaultValue;
		}
		return defaultValue;
	}

	/**
	 * get String array from jsonData
	 * 
	 * @param jsonData
	 * @param key
	 * @param defaultValue
	 * @return <ul>
	 *         <li>if jsonObject is null, return defaultValue</li>
	 *         <li>if jsonData {@link JSONObject#JSONObject(String)} exception,
	 *         return defaultValue</li>
	 *         <li>return</li>
	 *         </ul>
	 */
	public static String[] getStringArray(String jsonData, String key,
			String[] defaultValue) {
		if (StringUtils.isEmpty(jsonData)) {
			return defaultValue;
		}

		try {
			JSONObject jsonObject = new JSONObject(jsonData);
			return getStringArray(jsonObject, key, defaultValue);
		} catch (JSONException e) {

			return defaultValue;
		}
	}

	/**
	 * get String list from jsonObject
	 * 
	 * @param jsonObject
	 * @param key
	 * @param defaultValue
	 * @return <ul>
	 *         <li>if jsonObject is null, return defaultValue</li>
	 *         <li>if key is null or empty, return defaultValue</li>
	 *         <li>if {@link JSONObject#getJSONArray(String)} exception, return
	 *         defaultValue</li>
	 *         <li>if {@link JSONArray#getString(int)} exception, return
	 *         defaultValue</li>
	 *         <li>return string array</li>
	 *         </ul>
	 */
	public static List<String> getStringList(JSONObject jsonObject, String key,
			List<String> defaultValue) {
		if (jsonObject == null || StringUtils.isEmpty(key)) {
			return defaultValue;
		}

		try {
			JSONArray statusArray = jsonObject.getJSONArray(key);
			if (statusArray != null) {
				List<String> list = new ArrayList<String>();
				for (int i = 0; i < statusArray.length(); i++) {
					list.add(statusArray.getString(i));
				}
				return list;
			}
		} catch (JSONException e) {

			return defaultValue;
		}
		return defaultValue;
	}

	/**
	 * get String list from jsonData
	 * 
	 * @param jsonData
	 * @param key
	 * @param defaultValue
	 * @return <ul>
	 *         <li>if jsonObject is null, return defaultValue</li>
	 *         <li>if jsonData {@link JSONObject#JSONObject(String)} exception,
	 *         return defaultValue</li>
	 *         <li>return
	 *         {@link JSONUtils#getStringList(JSONObject, String, List)}</li>
	 *         </ul>
	 */
	public static List<String> getStringList(String jsonData, String key,
			List<String> defaultValue) {
		if (StringUtils.isEmpty(jsonData)) {
			return defaultValue;
		}

		try {
			JSONObject jsonObject = new JSONObject(jsonData);
			return getStringList(jsonObject, key, defaultValue);
		} catch (JSONException e) {

			return defaultValue;
		}
	}

	/**
	 * get JSONObject from jsonObject
	 * 
	 * @param jsonObject
	 * @param key
	 * @param defaultValue
	 * @return <ul>
	 *         <li>if jsonObject is null, return defaultValue</li>
	 *         <li>if key is null or empty, return defaultValue</li>
	 *         <li>if {@link JSONObject#getJSONObject(String)} exception, return
	 *         defaultValue</li>
	 *         <li>return {@link JSONObject#getJSONObject(String)}</li>
	 *         </ul>
	 */
	public static JSONObject getJSONObject(JSONObject jsonObject, String key,
			JSONObject defaultValue) {
		if (jsonObject == null || StringUtils.isEmpty(key)) {
			return defaultValue;
		}

		try {
			return jsonObject.getJSONObject(key);
		} catch (JSONException e) {

			return defaultValue;
		}
	}

	/**
	 * get JSONObject from jsonData
	 * 
	 * @param jsonData
	 * @param key
	 * @param defaultValue
	 * @return <ul>
	 *         <li>if jsonData is null, return defaultValue</li>
	 *         <li>if jsonData {@link JSONObject#JSONObject(String)} exception,
	 *         return defaultValue</li>
	 *         <li>return
	 *         {@link JSONUtils#getJSONObject(JSONObject, String, JSONObject)}</li>
	 *         </ul>
	 */
	public static JSONObject getJSONObject(String jsonData, String key,
			JSONObject defaultValue) {
		if (StringUtils.isEmpty(jsonData)) {
			return defaultValue;
		}

		try {
			JSONObject jsonObject = new JSONObject(jsonData);
			return getJSONObject(jsonObject, key, defaultValue);
		} catch (JSONException e) {

			return defaultValue;
		}
	}

	/**
	 * get JSONObject from jsonObject
	 * 
	 * @param jsonObject
	 * @param defaultValue
	 * @param keyArray
	 * @return <ul>
	 *         <li>if jsonObject is null, return defaultValue</li>
	 *         <li>if keyArray is null or empty, return defaultValue</li>
	 *         <li>get {@link #getJSONObject(JSONObject, String, JSONObject)} by
	 *         recursion, return it. if anyone is null, return directly</li>
	 *         </ul>
	 */
	public static JSONObject getJSONObjectCascade(JSONObject jsonObject,
			JSONObject defaultValue, String... keyArray) {
		if (jsonObject == null || ArrayUtils.isEmpty(keyArray)) {
			return defaultValue;
		}

		JSONObject js = jsonObject;
		for (String key : keyArray) {
			js = getJSONObject(js, key, defaultValue);
			if (js == null) {
				return defaultValue;
			}
		}
		return js;
	}

	/**
	 * get JSONObject from jsonData
	 * 
	 * @param jsonData
	 * @param defaultValue
	 * @param keyArray
	 * @return <ul>
	 *         <li>if jsonData is null, return defaultValue</li>
	 *         <li>if keyArray is null or empty, return defaultValue</li>
	 *         <li>get {@link #getJSONObject(JSONObject, String, JSONObject)} by
	 *         recursion, return it. if anyone is null, return directly</li>
	 *         </ul>
	 */
	public static JSONObject getJSONObjectCascade(String jsonData,
			JSONObject defaultValue, String... keyArray) {
		if (StringUtils.isEmpty(jsonData)) {
			return defaultValue;
		}

		try {
			JSONObject jsonObject = new JSONObject(jsonData);
			return getJSONObjectCascade(jsonObject, defaultValue, keyArray);
		} catch (JSONException e) {

			return defaultValue;
		}
	}

	/**
	 * get JSONArray from jsonObject
	 * 
	 * @param jsonObject
	 * @param key
	 * @param defaultValue
	 * @return <ul>
	 *         <li>if jsonObject is null, return defaultValue</li>
	 *         <li>if key is null or empty, return defaultValue</li>
	 *         <li>if {@link JSONObject#getJSONArray(String)} exception, return
	 *         defaultValue</li>
	 *         <li>return {@link JSONObject#getJSONArray(String)}</li>
	 *         </ul>
	 */
	public static JSONArray getJSONArray(JSONObject jsonObject, String key,
			JSONArray defaultValue) {
		if (jsonObject == null || StringUtils.isEmpty(key)) {
			return defaultValue;
		}

		try {
			return jsonObject.getJSONArray(key);
		} catch (JSONException e) {

			return defaultValue;
		}
	}

	/**
	 * get JSONArray from jsonData
	 * 
	 * @param jsonData
	 * @param key
	 * @param defaultValue
	 * @return <ul>
	 *         <li>if jsonObject is null, return defaultValue</li>
	 *         <li>if jsonData {@link JSONObject#JSONObject(String)} exception,
	 *         return defaultValue</li>
	 *         <li>return</li>
	 *         </ul>
	 */
	public static JSONArray getJSONArray(String jsonData, String key,
			JSONArray defaultValue) {
		if (StringUtils.isEmpty(jsonData)) {
			return defaultValue;
		}

		try {
			JSONObject jsonObject = new JSONObject(jsonData);
			return getJSONArray(jsonObject, key, defaultValue);
		} catch (JSONException e) {

			return defaultValue;
		}
	}

	/**
	 * get Boolean from jsonObject
	 * 
	 * @param jsonObject
	 * @param key
	 * @param defaultValue
	 * @return <ul>
	 *         <li>if jsonObject is null, return defaultValue</li>
	 *         <li>if key is null or empty, return defaultValue</li>
	 *         <li>return {@link JSONObject#getBoolean(String)}</li>
	 *         </ul>
	 */
	public static boolean getBoolean(JSONObject jsonObject, String key,
			Boolean defaultValue) {
		if (jsonObject == null || StringUtils.isEmpty(key)) {
			return defaultValue;
		}

		try {
			return jsonObject.getBoolean(key);
		} catch (JSONException e) {

			return defaultValue;
		}
	}

	/**
	 * get Boolean from jsonData
	 * 
	 * @param jsonData
	 * @param key
	 * @param defaultValue
	 * @return <ul>
	 *         <li>if jsonObject is null, return defaultValue</li>
	 *         <li>if jsonData {@link JSONObject#JSONObject(String)} exception,
	 *         return defaultValue</li>
	 *         <li>return
	 *         {@link JSONUtils#getBoolean(JSONObject, String, Boolean)}</li>
	 *         </ul>
	 */
	public static boolean getBoolean(String jsonData, String key,
			Boolean defaultValue) {
		if (StringUtils.isEmpty(jsonData)) {
			return defaultValue;
		}

		try {
			JSONObject jsonObject = new JSONObject(jsonData);
			return getBoolean(jsonObject, key, defaultValue);
		} catch (JSONException e) {

			return defaultValue;
		}
	}

}
