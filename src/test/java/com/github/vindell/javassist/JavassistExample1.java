/*
 * Copyright (c) 2017, vindell (https://github.com/vindell).
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
package com.github.vindell.javassist;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;

public class JavassistExample1 {
	
    public static void main(String[] args) throws Exception {  
    	
        ClassPool pool = ClassPool.getDefault();  
        CtClass cc = pool.makeClass("org.apache.cxf.spring.boot.entity.User");  
          
        //创建属性  
        CtField field01 = CtField.make("private int id;",cc);  
        CtField field02 = CtField.make("private String name;", cc);  
        cc.addField(field01);  
        cc.addField(field02);  
  
        //创建方法  
        CtMethod method01 = CtMethod.make("public String getName(){return name;}", cc);  
        CtMethod method02 = CtMethod.make("public void setName(String name){this.name = name;}", cc);  
        cc.addMethod(method01);  
        cc.addMethod(method02);  
          
        //添加有参构造器  
        CtConstructor constructor = new CtConstructor(new CtClass[]{CtClass.intType,pool.get("java.lang.String")},cc);  
        constructor.setBody("{this.id=id;this.name=name;}");  
        cc.addConstructor(constructor);  
        //无参构造器  
        CtConstructor cons = new CtConstructor(null,cc);  
        cons.setBody("{}");  
        cc.addConstructor(cons);  
          
        cc.writeFile("D:/TestCompiler/src");  
    }  
}
