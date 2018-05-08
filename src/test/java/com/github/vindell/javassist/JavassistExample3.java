package com.github.vindell.javassist;

import java.io.File;
import java.io.FileOutputStream;

import org.junit.Test;

import com.github.vindell.javassist.proxy.JavassistProxy;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

/**
 * https://www.cnblogs.com/coshaho/p/5105545.html
 */
public class JavassistExample3 {

	@SuppressWarnings("unchecked")
	@Test
	public void createDynamicInterface() throws Exception {
		
		ClassPool pool = ClassPool.getDefault();
		
		
		
		
		// 创建类
		CtClass cc = pool.makeInterface("com.github.vindell.javassist.DynamicHelloWorld", pool.getCtClass("java.lang.Cloneable"))
				;
		//pool.makeInterface("com.github.vindell.javassist.DynamicHelloWorld");
		//cc.setSuperclass(pool.getCtClass("java.lang.Cloneable"));
		
		ClassFile ccFile = cc.getClassFile();
		ConstPool constPool = ccFile.getConstPool();

		// 添加类注解
		AnnotationsAttribute bodyAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
		Annotation bodyAnnot = new Annotation("javax.jws.WebService", constPool);
		bodyAnnot.addMemberValue("name", new StringMemberValue("HelloWoldService", constPool));
		bodyAttr.addAnnotation(bodyAnnot);

		ccFile.addAttribute(bodyAttr);
		
		// 创建方法
		CtClass ccStringType = pool.get("java.lang.String");
		// 参数： 1：返回类型 2：方法名称 3：传入参数类型 4：所属类CtClass
		CtMethod ctMethod = new CtMethod(ccStringType, "sayHello", new CtClass[] { ccStringType }, cc);
		ctMethod.setModifiers(Modifier.PUBLIC);
		/*StringBuffer body = new StringBuffer();
		body.append("{");
		body.append("\n    System.out.println($1);");
		body.append("\n    return \"Hello, \" + $1;");
		body.append("\n}");
		ctMethod.setBody(body.toString());*/
		cc.addMethod(ctMethod);
		
		// 添加方法注解
		AnnotationsAttribute methodAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
		Annotation methodAnnot = new Annotation("javax.jws.WebMethod", constPool);
		methodAnnot.addMemberValue("operationName", new StringMemberValue("sayHelloWorld", constPool));
		methodAttr.addAnnotation(methodAnnot);

		Annotation resultAnnot = new Annotation("javax.jws.WebResult", constPool);
		resultAnnot.addMemberValue("name", new StringMemberValue("result", constPool));
		methodAttr.addAnnotation(resultAnnot);
		
		ctMethod.getMethodInfo().addAttribute(methodAttr);

		// 添加参数注解
		ParameterAnnotationsAttribute parameterAtrribute = new ParameterAnnotationsAttribute(constPool,
				ParameterAnnotationsAttribute.visibleTag);
		Annotation paramAnnot = new Annotation("javax.jws.WebParam", constPool);
		paramAnnot.addMemberValue("name", new StringMemberValue("name", constPool));
		Annotation[][] paramArrays = new Annotation[1][1];
		paramArrays[0][0] = paramAnnot;
		parameterAtrribute.setAnnotations(paramArrays);

		ctMethod.getMethodInfo().addAttribute(parameterAtrribute);

		// 把生成的class文件写入文件
		byte[] byteArr = cc.toBytecode();
		FileOutputStream fos = new FileOutputStream(new File("D://DynamicHelloWorld2.class"));
		fos.write(byteArr);
		fos.close();

		Object obj = JavassistProxy.getProxy(cc.toClass());
				
		obj.toString();

		/*Object obj = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] {cc.toClass()}, new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				
				System.out.println("------- intercept before --------");  
		        // 调用原来的方法  
		        Object result = method.invoke(proxy, args);  
		        System.out.println("--------intercept after ---------");  
		        return result;  
				
			}
		});  
		
		obj.toString();*/
		
	}
 
}