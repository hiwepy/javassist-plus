package com.github.hiwepy.javassist;

import java.io.File;
import java.io.FileOutputStream;

import org.junit.Test;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

/**
 * https://www.cnblogs.com/coshaho/p/5105545.html
 * https://blog.csdn.net/tscyds/article/details/78415172
 */
public class JavassistExample3 {

	@SuppressWarnings("unchecked")
	@Test
	public void createDynamicInterface() throws Exception {
		
		ClassPool pool = ClassPool.getDefault();
		
		// 创建类
		CtClass declaring = pool.makeInterface("com.github.hiwepy.javassist.IDynamicHelloWorld", pool.getCtClass("java.lang.Cloneable"));
		
		
		//pool.makeInterface("com.github.hiwepy.javassist.DynamicHelloWorld");
		//cc.setSuperclass(pool.getCtClass("java.lang.Cloneable"));
		
		ClassFile ccFile = declaring.getClassFile();
		ConstPool constPool = ccFile.getConstPool();

		// 添加类注解
		AnnotationsAttribute bodyAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
		Annotation bodyAnnot = new Annotation("javax.jws.WebService", constPool);
		bodyAnnot.addMemberValue("name", new StringMemberValue("HelloWoldService", constPool));
		bodyAttr.addAnnotation(bodyAnnot);

		ccFile.addAttribute(bodyAttr);
		
		// 创建抽象方法
		
		// 参数： 1：返回类型 2：方法名称 3：传入参数类型 4：所属类CtClass
		CtClass[] parameters = new CtClass[] { pool.get("java.lang.String") };
		CtClass[] exceptions = new CtClass[] { pool.get("java.lang.Exception") };
		CtMethod ctMethod = CtNewMethod.abstractMethod(pool.get("java.lang.String"), "sayHello", parameters , exceptions, declaring);
		ctMethod.setModifiers(Modifier.PUBLIC);
		declaring.addMethod(ctMethod);
		
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
		byte[] byteArr = declaring.toBytecode();
		FileOutputStream fos = new FileOutputStream(new File("D://IDynamicHelloWorld.class"));
		fos.write(byteArr);
		fos.close();
		
	}
 
}