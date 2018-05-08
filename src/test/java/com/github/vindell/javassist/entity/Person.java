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
package com.github.vindell.javassist.entity;

public class Person implements Smileable, Talkable {  
	  
    private String name;  
      
    public Person(String name) {  
        this.name = name;  
    }  
      
    public Object talk(String words) throws Exception {  
        System.out.println(name + " says: " + words);  
        return words;  
    }  
      
    public Object smile() throws Exception {  
        System.out.println(name + " start smiling");  
        System.out.println(name + " stop smiling");  
        return null;  
    }  
  
}  
