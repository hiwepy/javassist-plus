package com.github.vindell.javassist.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.github.vindell.javassist.JavassistWebserviceGenerator;
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
		        Object result = method.invoke(proxy, args);  
		        System.out.println("--------intercept after ---------");  
		        return result;  
				
			}
		});  
		
		obj.toString();
		
	}
 
}