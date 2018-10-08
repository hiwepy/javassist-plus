# javassist-plus
javassist-plus

### Maven Dependency

``` xml
<dependency>
	<groupId>com.github.vindell</groupId>
	<artifactId>javassist-plus</artifactId>
	<version>${project.version}</version>
</dependency>
```

#[Javassist基础与实践](https://blog.csdn.net/tscyds/article/details/78415172)
###概述

对于Javassist可理解成在java语言层面上来操纵java字节码的一种工具。经典应用场景是在构建java代码阶段直接修改字节码，如代码插桩。

###特点：

    更改原有class：增、删和修改class中的字段或方法；修改类的继承结构
    新增class：增加字段、方法、构造方法；注意不能新增接口。
    新增接口：只适用于接口
    新增注解：用于创建注解

###核心基础

    CtClass：Javassist并不是直接操纵java字节码文件（class），而是将class封装抽象成CtClass，再使用CtClass来完成class的修改。
        获取CtClass：借助于ClassPool.get(String classname)方法，classname必须是全限定类名。
        输出文件：对于CtClass的修改仅是内存上的，如果需要持久化的修改则需要借助于CtClass.writeFile方法，输出路径：rootProject/包名/类.class
        CtClass状态：分为冻结与解冻状态，当CtClass执行了writeFile、toClass或 toBytecode方法后CtClass会进入冻结状态，处于冻结状态的CtClass不能被修改。
        冻结与解冻：使用freeze方法对CtClass冻结，使用defrost方法对CtClass解冻。
        继承与实现：修改类继承结构，extend使用setSuperclass方法，implements则使用setInterfaces方法。

    ClassPool：专门用于存放CtClass容器，所有的CtClass对象实例必须使用ClassPool来获取。
        获取CtClass范围：默认ClassPool所获取到的CtClass范围除了本工程代码，还包括java、javax以及classpath配置中的路径，至于远程路径则需要额外的配置。
        新增CtClass：makeClass用于创建新的字节码文件，注意不能创建接口，但能创建抽象类。
        新增接口：makeInterface专门用于创建新的接口，注意接口方法只能通过CtNewMethod.abstractMethod来创建。

    CtField：Javassist将class中的成员变量抽象成CtField，借助于CtField来实现对class中的成员变量的操纵。
        新建CtField：使用CtField.make方法完成CtField对象的创建，注意创建的语句必须以分号结尾，否则抛CannotCompileException异常。
        CtField与CtClass关系：新建好的CtField通过CtClass.addField添加到CtClass。

    CtMethod：Javassist将class中除了构造方法以外的其它方法抽象成CtMethod，借助于CtMethod来实现对class中的这些方法的操纵。
        新建CtMethod：CtMethod提供了几个静态make方法，也可使用专门构造CtMethod实例的CtNewMethod类来实现。
        CtNewMethod：简单理解就是一个工厂类，执行创建普通方法、抽象方法、setter/getter等方法。
        CtMethod与CtClass关系：新建好的CtField通过CtClass.addMethod添加到CtClass。

    CtConstructor：Javassist单独将class中的构造方法抽象成CtConstructor，借助于CtConstructor来实现对class中的构造方法的操纵。
        新建CtConstructor：使用几个重载的CtConstructor方法，也可使用专门构造CtConstructor实例的CtNewConstructor类来实现。
        修改方法体：CtConstructor.setBody，注意方法体是必须包含在{}中。
        CtConstructor与CtClass关系：新建好的CtField通过CtClass.addConstructor添加到CtClass。
