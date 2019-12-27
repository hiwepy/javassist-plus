package com.github.hiwepy.javassist.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.github.hiwepy.javassist.JavassistWebserviceGenerator;
import org.junit.Test;

/**
 * https://www.cnblogs.com/coshaho/p/5105545.html
 */
public class JavassistProxyExample1 {

	@Test
	public void testDynamicInterface() throws Exception {
		
		JavassistWebserviceGenerator javassistLearn = new JavassistWebserviceGenerator();
		
		Class<?> webservice = javassistLearn.createDynamicInterface();
		
		// JDK代理
		Object obj = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] { webservice }, new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				
				System.out.println("------- intercept before --------");  
		        // 调用原来的方法  
		       // Object result = method.invoke(proxy, args);  
		        System.out.println("--------intercept after ---------");  
		        return null;  
				
			}
		});  
		
		Class<?> clazz = obj.getClass();
		
		System.err.println("=========Type Annotations======================");
		for (Annotation element : clazz.getAnnotations()) {
			System.out.println(element.toString());
		}
		
		System.err.println("=========Fields======================");
		for (Field element : clazz.getDeclaredFields()) {
			System.out.println(element.getName());
			for (Annotation anno : element.getAnnotations()) {
				System.out.println(anno.toString());
			}
		}
		System.err.println("=========Methods======================");
		for (Method element : clazz.getDeclaredMethods()) {
			System.out.println(element.getName());
			for (Annotation anno : element.getAnnotations()) {
				System.out.println(anno.toString());
			}
		}
		System.err.println("=========sayHello======================");
		
		Method mt =  obj.getClass().getDeclaredMethod("sayHello", String.class);
		mt.invoke(obj, "xxx");
		
	}
 
}