package com.github.vindell.javassist.proxy;

import org.junit.Test;

import com.github.vindell.javassist.JavassistWebserviceGenerator;

/**
 * https://www.cnblogs.com/coshaho/p/5105545.html
 */
public class JavassistProxyExample3 {

	@Test
	public void testDynamicInterface() throws Exception {
		
		JavassistWebserviceGenerator javassistLearn = new JavassistWebserviceGenerator();
		
		Class<?> webservice = javassistLearn.createDynamicInterface();

		// Javassist Proxy
		Object obj = JavassistProxy.getProxy(webservice);
				
		obj.toString();
		
	}
 
}