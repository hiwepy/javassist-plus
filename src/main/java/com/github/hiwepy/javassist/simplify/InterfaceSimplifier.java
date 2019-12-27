/*
* RHQ Management Platform
* Copyright (C) 2005-2011 Red Hat, Inc.
* All rights reserved.
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation version 2 of the License.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/
package com.github.hiwepy.javassist.simplify;


import java.lang.reflect.InvocationHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hiwepy.javassist.bytecode.MethodSignature;
import com.github.hiwepy.javassist.utils.ClassPoolFactory;
import com.github.hiwepy.javassist.utils.JavassistUtils;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ClassMemberValue;

/**
 * 
 * The scripts can use simplified interfaces that omit the first "InvocationHandler"
 * argument to most methods . This helper class prepares
 * such simplified interfaces.
 * @author Greg Hinkle
 * @author Lukas Krejci
 */
public class InterfaceSimplifier {

	private static final Logger LOG = LoggerFactory.getLogger(InterfaceSimplifier.class);

	private InterfaceSimplifier() {

	}

	public static Class<?> simplify(Class<?> intf) {
		try {
			
			ClassPool classPool = ClassPoolFactory.getClassPoolForCurrentContextClassLoader();

			String simplifiedName = getSimplifiedName(intf);
			LOG.debug("Simplifying " + intf + " (simplified interface name: " + simplifiedName + ")...");

			CtClass cached = null;
			try {
				cached = classPool.get(simplifiedName);
				return Class.forName(simplifiedName, false, classPool.getClassLoader());
			} catch (NotFoundException e) {
				// ok... load it
			} catch (ClassNotFoundException e) {
				LOG.debug("Class [" + simplifiedName + "] not found - cause: " + e, e);
				if (cached != null) {
					// strange - we found the class definition in the class pool, which means we
					// must have touched it
					// before but Class.forName failed to find the class in the class pool's class
					// loader.
					return cached.toClass();
				}
			}

			CtClass originalClass = classPool.get(intf.getName());
			ClassFile originalClassFile = originalClass.getClassFile();

			CtClass newClass = classPool.makeInterface(simplifiedName);
			
			//解冻  : 执行 defrost()方法后，CtClass对象就可再次被修改
			newClass.defrost();

			ClassFile newClassFile = newClass.getClassFile();

			// we'll be adding new constants to the class file (for generics and
			// annotations)
			ConstPool constPool = newClassFile.getConstPool();

			// copy the annotations on the class
			AnnotationsAttribute annotations = (AnnotationsAttribute) originalClassFile.getAttribute(AnnotationsAttribute.visibleTag);
			AnnotationsAttribute newAnnotations = JavassistUtils.copyAnnotations(annotations, constPool);

			// add our @Simplified annotation to the new class
			newAnnotations = addSimplifiedClassAnnotation(originalClass.getName(), newAnnotations, constPool);
			newClassFile.addAttribute(newAnnotations);

			// copy the generic signature of the class
			SignatureAttribute signature = (SignatureAttribute) originalClassFile.getAttribute(SignatureAttribute.tag);
			if (signature != null) {
				newClassFile.addAttribute(new SignatureAttribute(constPool, signature.getSignature()));
			}

			// now copy over the methods
			CtMethod[] methods = originalClass.getMethods();

			for (CtMethod originalMethod : methods) {

				// we are only simplifying interfaces here, but the CtClass.getMethods() also
				// returns concrete methods
				// inherited from Object. Let's just skip those - we don't need to worry about
				// them...
				if (!Modifier.isAbstract(originalMethod.getModifiers())) {
					continue;
				}

				CtClass[] params = originalMethod.getParameterTypes();

				// capture all the runtime visible method annotations on the original method
				annotations = (AnnotationsAttribute) originalMethod.getMethodInfo()
						.getAttribute(AnnotationsAttribute.visibleTag);

				// capture all the runtime visible parameter annotations on the original method
				ParameterAnnotationsAttribute parameterAnnotations = (ParameterAnnotationsAttribute) originalMethod
						.getMethodInfo().getAttribute(ParameterAnnotationsAttribute.visibleTag);

				// capture the generic signature of the original method.
				signature = (SignatureAttribute) originalMethod.getMethodInfo().getAttribute(SignatureAttribute.tag);

				boolean simplify = params.length > 0 && params[0].getName().equals(InvocationHandler.class.getName());

				if (simplify) {
					
					// generate new params, leaving out the first parameter (the handler)
					CtClass[] simpleParams = new CtClass[params.length - 1];
					System.arraycopy(params, 1, simpleParams, 0, params.length - 1);
					params = simpleParams;
					
				}

				// generate the new method with possibly modified parameters
				CtMethod newMethod = CtNewMethod.abstractMethod(originalMethod.getReturnType(),
						originalMethod.getName(), params, originalMethod.getExceptionTypes(), newClass);

				// copy over the method annotations
				annotations = JavassistUtils.copyAnnotations(annotations, constPool);

				if (simplify) {
					
					// add the @SimplifiedMethod to the method annotations
					annotations = addSimplifiedMethodAnnotation(annotations, constPool);
					
					if (signature != null) {
						
						// fun, we need to modify the signature, too, because we have left out the
						// parameter
						MethodSignature sig = MethodSignature.parse(signature.getSignature());
						sig.paramTypes.remove(0);
						signature = new SignatureAttribute(constPool, sig.toString());
						
					}

					// next, we need to copy the parameter annotations
					parameterAnnotations = JavassistUtils.copyParameterAnnotations(parameterAnnotations, constPool, 1);
					
				} else {
					
					// just copy the sig and parameter annotations verbatim
					if (signature != null) {
						signature = new SignatureAttribute(constPool, signature.getSignature());
					}

					parameterAnnotations = JavassistUtils.copyParameterAnnotations(parameterAnnotations, constPool, 0);
				}

				if (parameterAnnotations != null) {
					newMethod.getMethodInfo().addAttribute(parameterAnnotations);
				}

				if (signature != null) {
					newMethod.getMethodInfo().addAttribute(signature);
				}

				if (annotations != null) {
					newMethod.getMethodInfo().addAttribute(annotations);
				}
				
				// it is important to add the method directly to the classfile, not the class
				// because otherwise the generics info wouldn't survive
				newClassFile.addMethod(newMethod.getMethodInfo());
			}

			return newClass.toClass();

		} catch (Exception e) {
			String msg = "Failed to simplify " + intf + ".";
			LOG.error(msg, e);
			throw new IllegalStateException(msg, e);
		}
	}
	
	private static String getSimplifiedName(Class<?> interfaceClass) {
		String fullName = interfaceClass.getName();
		String simpleName = interfaceClass.getSimpleName();
		Package pkg = interfaceClass.getPackage();
		String packageName = (pkg != null) ? pkg.getName() : fullName.substring(0, fullName.length() - (simpleName.length() + 1));
		return packageName + ".wrapped." + simpleName + "Simple";
	}
	
	private static AnnotationsAttribute addSimplifiedClassAnnotation(String originalClassName,
			AnnotationsAttribute annotations, ConstPool constPool) {

		if (annotations == null) {
			annotations = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
		}
		
		Annotation simplified = new Annotation(SimplifiedClass.class.getName(), constPool);
		simplified.addMemberValue("originalClass", new ClassMemberValue(originalClassName, constPool));

		annotations.addAnnotation(simplified);

		return annotations;
	}

	private static AnnotationsAttribute addSimplifiedMethodAnnotation(AnnotationsAttribute annotations,
			ConstPool constPool) {

		if (annotations == null) {
			annotations = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
		}
		
		annotations.addAnnotation(new Annotation(SimplifiedMethod.class.getName(), constPool));

		return annotations;
	}
}
