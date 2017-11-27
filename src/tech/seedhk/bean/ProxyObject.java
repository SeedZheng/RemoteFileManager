package tech.seedhk.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ProxyObject implements Serializable{
	
		
	private static final long serialVersionUID = 1L;
	private  Class<?> className;	//类名
	//private  Class<?> returnType;	//返回类型
	private  String methodName;	//方法名
	private  Class<?>[] classs;		//参数列表
	private Object[] objects;		//参数值
	
	
	public static ProxyObject newInstance(Class<?> className){
		return null;
	}
	
	
	public static  Object invoke(String classname,String methodname,Object[] objects,Class<?>... clazz){
		/*
		 * 思路：
		 * 暂不支持数组作为参数
		 * clazz类型必须是装箱类型
		 */
		
		Class<?> c;
		Method m;
		Object ret = null;
		Class<?>[] paramTypes;
		try {
			c=Class.forName(classname);
			m=c.getMethod(methodname, clazz);
			paramTypes=m.getParameterTypes();
/*			for(Class<?> s:paramTypes){
				String type=s.getName();
				System.out.println(type);
			}*/
			Object object=c.newInstance();
			ret=m.invoke(object, objects);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	
	public void getMethod(String classname,String methodname,Object[] objects,Class<?>... clazz) {

		Class<?> c;
		Method method;
		//Class<?> retType;
		try {
			c = Class.forName(classname);
			method=c.getMethod(methodname, clazz);
			//retType=method.getReturnType();
			className=c;
			//returnType=retType;
			methodName=method.getName();
			classs=clazz;
			objects=objects;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public static byte[] bean2byte(Object obj){
		
		byte[] b=null;
		try {
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			ObjectOutputStream oos=new ObjectOutputStream(baos);
			oos.writeObject(obj);
			b = baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	public static Object byte2Bean(byte[] b){
		Object o=null;
		try {
			ByteArrayInputStream bais=new ByteArrayInputStream(b);
			ObjectInputStream ios=new ObjectInputStream(bais);
			o=ios.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return o;
	}
		


	@Override
	public String toString() {
		return "ProxyObject : className="+className.getName()+" , methodName="+
				methodName+" , classs="+listParam(classs);
	}
	
	private String listParam(Class<?>[] classs){
		StringBuilder sb=new StringBuilder();
		if(null!=classs && classs.length>0)
			for(Class<?> s:classs){
				sb.append(s.getName()).append(" ") ;
			}
		else
			sb.append("null");
		sb.append("\n");
		return sb.toString();
	}


	public static void main(String[] args) throws Exception {

		ProxyObject po1=new ProxyObject();
		po1.className=Object.class;
		po1.methodName="test";
		po1.classs=new Class[]{String.class};
		po1.objects=new Object[]{4,5,6};
		
		byte[] b=bean2byte(po1);
		
		ProxyObject po=(ProxyObject) byte2Bean(b);
		
		System.out.println(po.toString());
		
	}
	
	
	public List<Map<String,Object>> test(String param1,Integer param2,char param3){
		int[] j=new int[]{Integer.parseInt(param1),param2,(int)param3};
		List<Map<String,Object>> list=new ArrayList<>();
		for(int i=0;i<3;i++){
			Map<String,Object> map=new HashMap<>();
			map.put(""+i, j[i]);
			list.add(map);
		}
		return list;
	}
	
	
	
	

}
