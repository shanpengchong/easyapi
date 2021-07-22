package cn.easyutil.easyapi.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class ObjectUtil {
	
	/***
	 * 将对象序列化后进行base64处理
	 * @param obj	对象
	 * @return	base64的序列化对象数据
	 */
	public static String toBase64(Object obj) {
		return StringUtil.base64Encode(toByte(obj));
	}
	
	/**
	 * 将对象进行序列化
	 * @param obj	对象
	 * @return	对象序列化后的数据
	 */
	private static byte[] toByte(Object obj) {
		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
			objOut.writeObject(obj);
			return byteOut.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/** 
	 * 给对象的属性赋值 
	 * @param obj	对象
	 * @param attrName	对象的属性名
	 * @param value	对象的属性值
	 */
	public static void setAttribute(Object obj, String attrName, Object value) {
		try{
			Class clazz = obj.getClass();
			while(!clazz.equals(Object.class)) {
				try {
					Field f = clazz.getDeclaredField(attrName);
					if (f == null) {
						continue;
					}
					try {
						//set方法的名称
						String setMothedName = "SET"+attrName.toUpperCase();
						Method[] ms = clazz.getDeclaredMethods();
						for (int i = 0; i < ms.length; i++) {
							if(ms[i].getName().toUpperCase().equals(setMothedName)){
								ms[i].setAccessible(true);
								ms[i].invoke(obj,value);
								ms[i].setAccessible(false);
								return;
							}
						}
						throw new RuntimeException();
					} catch (Exception e) {
						f.setAccessible(true);
						if (value == null || f.getType().isAssignableFrom(value.getClass())) {
							f.set(obj, value);
						} else {
							f.set(obj, parseToObject(value, f.getType()));
						}
						f.setAccessible(false);
						return;
					}
				} catch (NoSuchFieldException e) {
					clazz = clazz.getSuperclass();
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 从对象中取值
	 * @param obj	对象
	 * @param attrName	要取值的属性名
	 * @return	值
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getAttributeValue(Object obj, String attrName) {
		try {
			Class clazz = obj.getClass();
			while(!clazz.equals(Object.class)) {
				try {
					Field f = clazz.getDeclaredField(attrName);
					try {
						Class<?> type = f.getType();
						//get方法的名称
						String getMothedName;
						if(type == boolean.class){
							getMothedName = "IS"+attrName.toUpperCase();
						}else{
							getMothedName = "GET"+attrName.toUpperCase();
						}
						Method[] ms = clazz.getDeclaredMethods();
						for (int i = 0; i < ms.length; i++) {
							if(ms[i].getName().toUpperCase().equals(getMothedName)){
								ms[i].setAccessible(true);
								Object result = ms[i].invoke(obj);
								ms[i].setAccessible(false);
								return (T) result;
							}
						}
						throw new RuntimeException();
					} catch (Exception e) {
						f.setAccessible(true);
						Object value = f.get(obj);
						f.setAccessible(false);
						return (T) value;
					}
				} catch (NoSuchFieldException e) {
					clazz = clazz.getSuperclass();
				}
			}
			
			return null; 
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 获取对象中的所有属性
	 * @param bean	对象
	 * @return	属性和值(Map[属性名, 属性值])
	 */
	public static Map<String, Object> getAttributes(Object bean) {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			for(Class clazz=bean.getClass(); !clazz.equals(Object.class); clazz=clazz.getSuperclass()) {
				Field[] fs = clazz.getDeclaredFields();
				for(Field f : fs) {
					// 子类最大，父类值不覆盖子类
					if(map.containsKey(f.getName())) {
						continue;
					}
					f.setAccessible(true);
					Object value = f.get(bean);
					f.setAccessible(false);
					map.put(f.getName(), value);
				}
			}
			map.remove("serialVersionUID");
			return map;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 获取对象中的所有非空字段
	 * @param bean
	 * @return
	 */
	public static Map<String, Object> getNotNullAttributes(Object bean){
		Map<String,Object> result = new HashMap<String, Object>();
		Map<String, Object> map = getAttributes(bean);
		for (String key : map.keySet()) {
			if(map.get(key)!=null && !map.get(key).equals("")){
				result.put(key, map.get(key));
			}
		}
		return result;
	}
	
	/**
	 * 依据属性名获取该类中对应名字的属性
	 * (未包含此属性则返回null)
	 * @param aclass	类class
	 * @param name		属性名
	 * @return	属性对象集合
	 */
	public static Field getField(Class<?> aclass, String name) {
		try {
			for (Class clazz = aclass; !clazz.equals(Object.class); clazz = clazz.getSuperclass()) {
				Field[] fs = clazz.getDeclaredFields();
				for (Field f : fs) {
					if(name.equals(f.getName())) {
						return f;
					}
				}
			}
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 获取类的所有属性与属性的类型
	 * @param clazz	类
	 * @return	该类的所有属性名与属性类型(包含父类属性)
	 */
	public static Map<String, Class> getFieldClass(Class clazz) {
		try {
			Map<String, Class> attrMap = new HashMap<String, Class>();
			for(; !clazz.equals(Object.class); clazz=clazz.getSuperclass()) {
				Field[] fs = clazz.getDeclaredFields();
				for(Field f : fs) {
					attrMap.put(f.getName(), f.getType());
				}
			}
			attrMap.remove("serialVersionUID");
			return attrMap;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T newInstance(Class<T> tClass){
		try {
			return tClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Class forName(String canonicalName){
		if(StringUtil.isEmpty(canonicalName)){
			return null;
		}
		if(canonicalName.equals("int")){
			canonicalName = "java.lang.Integer";
		}else if(canonicalName.equals("double")){
			canonicalName = "java.lang.Double";
		}else if(canonicalName.equals("char")){
			canonicalName = "java.lang.Char";
		}else if(canonicalName.equals("long")){
			canonicalName = "java.lang.Long";
		}
		try {
			return Class.forName(canonicalName);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
    
	/***
	 * 依据类，获取该类的泛型class
	 * @param bean	类对象
	 * @return	泛型类型
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Object> Class<T> getGeneric(Class clazz) {
		try {
			Type type = clazz;
			if(type instanceof ParameterizedType) {
				Type[] params = ((ParameterizedType) type).getActualTypeArguments();
				return (Class<T>) params[0];
			}
			Type genType = clazz.getGenericSuperclass();
			if (!(genType instanceof ParameterizedType)) {
				return (Class<T>) Object.class;
			}
			Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
			return (Class<T>) params[0];
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/***
	 * 获取一个类的注解
	 * @param aclass	类的class
	 * @return	注解集合
	 */
	public static List<Annotation> getClassAnnotation(Class<?> aclass) {
		List<Annotation> aList = new ArrayList<Annotation>();
		try {
			for (Class clazz = aclass; !clazz.equals(Object.class); clazz = clazz.getSuperclass()) {
				Annotation[] classAnnotations = clazz.getDeclaredAnnotations();
				for (Annotation f : classAnnotations) {
					// 子类最大，父类值不覆盖子类
					if (aList.contains(f)) {
						continue;
					}
					aList.add(f);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return aList;
	}
	/**
	 * 获取类上指定的注解
	 * @param clazz 类
	 * @return key:类名 val:类上注解实例
	 */
	public static <T> Map<String, T> getClassAnnotation(Class clazz,Class<T> annotation){
		Map<String,T> map = new HashMap<String,T>();
		for (Class cl=clazz;!cl.equals(Object.class);cl=cl.getSuperclass()) {
			Annotation[] ans = cl.getDeclaredAnnotations();
			for(Annotation an : ans){
				if(an.annotationType().equals(annotation)){
					@SuppressWarnings("unchecked")
					T t = (T) an;
					map.put(cl.getName(), t);
				}
			}
		}
		return map;
	}
	/**
	 * 获取类属性上指定的注解
	 * @param clazz 
	 * @param annotation	指定注解
	 * @return
	 */
	public static <T> Map<String, T> getFieldAnnotation(Class clazz,Class<T> annotation){
		return getFieldAnnotation(clazz, null, annotation);
	}
	/**
	 * 获取类属性上指定的注解
	 * @param clazz
	 * @param fieldName 指定属性
	 * @param annotation	指定注解
	 * @return
	 */
	public static <T> Map<String, T> getFieldAnnotation(Class clazz,String fieldName,Class<T> annotation){
		Map<String,T> map = new HashMap<String,T>();
		for (Class cl=clazz;!cl.equals(Object.class);cl=cl.getSuperclass()) {
			Field[] fields = cl.getDeclaredFields();
			for (Field f : fields) {
				Annotation[] ans = f.getDeclaredAnnotations();
				for(Annotation an : ans){
					if(an.annotationType().equals(annotation)){
						@SuppressWarnings("unchecked")
						T t = (T) an;
						if(map.get(f.getName()) == null){
							map.put(f.getName(), t);
						}
						if(!StringUtil.isEmpty(fieldName) && f.getName().equals(fieldName)){
							return map;
						}
					}
				}
			}
		}
		return map;
	}
	
	
	/**
	 * 将对象转换为byte数据
	 * @param obj	对象
	 * @return	byte数据
	 */
	public static byte[] parseObjForByte(Object obj) {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream objOut = null;
		try {
			objOut = new ObjectOutputStream(byteOut);
			objOut.writeObject(obj);
			return byteOut.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if(objOut != null) {
					objOut.close();
				}
				if(byteOut != null) {
					byteOut.close();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/***
	 * 转换类型
	 * @param value	字符串的值
	 * @param type	要转换的类型
	 * @return	转换后的值
	 */
	@SuppressWarnings("unchecked")
	private static <T> T parseToObject(Object value, Class<T> type) {
		Object result = null;
		if(value==null || type==String.class) {
			result = value==null?null:value.toString();
		}
		else if(type==Character.class || type==char.class) {
			char[] chars = value.toString().toCharArray();
			result = chars.length>0?chars.length>1?chars:chars[0]:Character.MIN_VALUE;
		}
		else if(type==Boolean.class || type==boolean.class) {
//			result = Boolean.parseBoolean(value.toString());
			result = value.toString().equalsIgnoreCase("true")?true:value.toString().equalsIgnoreCase("false")?false:value;
		}
//		// 处理boolean值转换
//		else if(type==Double.class || type==double.class) {
//			
//		}
		else if(type==Long.class || type==long.class) {
			result = Long.parseLong(value.toString());
		}
		else if(type==Integer.class || type==int.class) {
			result = Integer.parseInt(value.toString());
		}
		else if(type==Double.class || type==double.class) {
			result = Double.parseDouble(value.toString());
		}
		else if(type==Float.class || type==float.class) {
			result = Float.parseFloat(value.toString());
		}
		else if(type==Byte.class || type==byte.class) {
			result = Byte.parseByte(value.toString());
		}
		else if(type==Short.class || type==short.class) {
			result = Short.parseShort(value.toString());
		}
		return (T) result;
	}
	
	
	/***
     * 校验是否是九种基础类型(即：非用户定义的类型)
     * @param value 字符串的值	要校验的值
     * @return  是否是基础类型(true:已经是基础类型了)
     */
    public static boolean isBaseClass(Class value) {
        if(value==null) {
            return true;
        } else if(value.equals(Long.class) || value.equals(long.class)) {
            return true;
        } else if(value.equals(Integer.class) || value.equals(int.class)) {
            return true;
        } else if(value.equals(Double.class) || value.equals(double.class)) {
            return true;
        } else if(value.equals(Float.class) || value.equals(float.class)) {
            return true;
        } else if(value.equals(Byte.class) || value.equals(byte.class)) {
            return true;
        } else if(value.equals(Boolean.class) || value.equals(boolean.class)) {
            return true;
        } else if(value.equals(Short.class) || value.equals(short.class)) {
            return true;
        } else if(value.equals(Character.class)) {
            return true;
        } else if(value.equals(String.class)) {
            return true;
        } 
        return false;
    }
    
    public static boolean isBaseObject(Object value) {
        if(value==null) {
            return true;
        } else if(value instanceof Long) {
            return true;
        } else if(value instanceof Integer) {
            return true;
        } else if(value instanceof Double) {
            return true;
        } else if(value instanceof Float) {
            return true;
        } else if(value instanceof Byte) {
            return true;
        } else if(value instanceof Boolean) {
            return true;
        } else if(value instanceof Short) {
            return true;
        } else if(value instanceof Character) {
            return true;
        } else if(value instanceof String) {
            return true;
        } 
        return false;
    }
	
	/** 清空对象中所有属性的初始值 */
	public static <T>void cleanInitValue(T bean) {
		if(bean == null) {
			return;
		}
		try {
			Class<?> clazz = bean.getClass();
			Object obj = clazz.newInstance();
			for(; !clazz.equals(Object.class); clazz=clazz.getSuperclass()) {
				Field[] fs = clazz.getDeclaredFields();
				for(Field f : fs) {
					if(Modifier.isFinal(f.getModifiers())) {
						continue;
					}
					f.setAccessible(true);
					Object initValue = f.get(obj);
					Object oldValue = f.get(bean);
					if(initValue!=null && initValue.equals(oldValue)) {
						f.set(bean, null);
					}
					f.setAccessible(false);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
