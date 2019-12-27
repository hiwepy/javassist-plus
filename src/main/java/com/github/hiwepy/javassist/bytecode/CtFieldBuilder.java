/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.hiwepy.javassist.bytecode;

import org.apache.commons.lang3.builder.Builder;

import com.github.hiwepy.javassist.utils.JavassistUtils;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.NotFoundException;

public class CtFieldBuilder implements Builder<CtField>{

	private CtField field;
	
	private CtFieldBuilder(CtClass declaring, CtClass fieldClass, String fieldName, String fieldValue) throws CannotCompileException {
		
		// 检查字段是否已经定义
		if(!JavassistUtils.hasField(declaring, fieldName)) {
			
			/** 添加属性字段 */
			field = new CtField(fieldClass, fieldName, declaring);
	        field.setModifiers(Modifier.PROTECTED);

	        //新增Field
	        declaring.addField(field, "\"" + fieldValue + "\"");
		} else {
			
			try {
				field = declaring.getDeclaredField(fieldName);
			} catch (NotFoundException e) {
			}
			
		}
		
	}
	
	public static  CtFieldBuilder create(CtClass declaring, CtClass fieldClass, String fieldName) throws CannotCompileException {
		return new CtFieldBuilder(declaring, fieldClass, fieldName, null);
	}

	public static  CtFieldBuilder create(CtClass declaring, CtClass fieldClass, String fieldName, String fieldValue) throws CannotCompileException {
		return new CtFieldBuilder(declaring, fieldClass, fieldName, fieldValue);
	}

	@Override
	public CtField build() {
		return field;
	}
	
}
