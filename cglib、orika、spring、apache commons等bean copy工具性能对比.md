# 简介

在实际项目中，考虑到不同的数据使用者，我们经常要处理 VO、DTO、Entity、DO 等对象的转换，如果手动编写 setter/getter 方法一个个赋值，将非常繁琐且难维护。通常情况下，这类转换都是同名属性的转换（类型可以不同），我们更多地会使用 bean copy 工具，例如 Apache Commons BeanUtils、Cglib BeanCopier 等。

在使用 bean copy 工具时，我们更多地会考虑性能，有时也需要考虑深浅复制的问题。本文将**对比几款常用的 bean copy 工具的性能，并介绍它们的原理、区别和使用注意事项**。

# 项目环境

本文使用 jmh 作为测试工具。

os：win 10

jdk：1.8.0_231

jmh：1.25

选择的 bean copy 工具及对应的版本如下：

apache commons beanUtils：1.9.4

spring beanUtils：5.2.10.RELEASE

cglib beanCopier：3.3.0

orika mapper：1.5.4

# 测试代码

本文使用的 java bean 如下，这个是之前测试序列化工具时用过的。一个用户对象，一对一关联部门对象和岗位对象，其中部门对象又存在自关联。

```java
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    // 普通属性--129个
    private String id;
    private String account;
    private String password;
    private Integer status;
    // ······
    
    /**
     * 所属部门
     */
    private Department department;
    /**
     * 岗位
     */
    private Position position;
    
    // 以下省略setter/getter方法
}
public class Department implements Serializable {
    private static final long serialVersionUID = 1L;
    // 普通属性--7个
    private String id;
    private String parentId;
    // ······
    /**
     * 子部门
     */
    private List<Department> children;
    
    // 以下省略setter/getter方法
}
public class Position implements Serializable {
    private static final long serialVersionUID = 1L;
    // 普通属性--6个
    private String id;
    private String name;
    // ······
    // 以下省略setter/getter方法
}
```

下面展示部分测试代码，完整代码见末尾链接。

## apache commons beanUtils

apache commons beanUtils 的 API 非常简单，通常只要一句代码就可以了。它支持自定义转换器（这个转换器是全局的，将替代默认的转换器）。

```java
    @Benchmark
    public UserVO testApacheBeanUtils(CommonState commonState) throws Exception {
        /*ConvertUtils.register(new Converter() {
            @Override
            public <T> T convert(Class<T> type, Object value) {
                if (Boolean.class.equals(type) || boolean.class.equals(type)) {
                    final String stringValue = value.toString().toLowerCase();
                    for (String trueString : trueStrings) {
                        if (trueString.equals(stringValue)) {
                            return type.cast(Boolean.TRUE);
                        }
                    }
                    // ······
                }
                return null;
            }
        }, Boolean.class);*/
        UserVO userVO = new UserVO();
        org.apache.commons.beanutils.BeanUtils.copyProperties(userVO, commonState.user);
        assert "zzs0".equals(userVO.getName());
        return userVO;
    }
```

apache commons beanUtils 的原理比较简单，浓缩起来就是下面的几行代码。可以看到，**源对象属性值的获取、目标对象属性值的设置，都是使用反射实现**，所以，apache commons beanUtils 的性能稍差。还有一点需要注意，**它的复制只是浅度复制**。

```java
        // 获取目标类的BeanInfo对象（这个会缓存起来，不用每次都重新创建）
        BeanInfo targetBeanInfo = Introspector.getBeanInfo(target.getClass());
        // 获取目标类的PropertyDescriptor数组（这个会缓存起来，不用每次都重新创建）
        PropertyDescriptor[] targetPds = targetBeanInfo.getPropertyDescriptors();
        
        // 遍历PropertyDescriptor数组，并给同名属性赋值
        for(PropertyDescriptor targetPd : targetPds) {
            // 获取源对象中同名属性的PropertyDescriptor对象，当然，这个也是通过Introspector获取的
            PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
            // 读取源对象中该属性的值
            Method readMethod = sourcePd.getReadMethod();
            Object value = readMethod.invoke(source);
            // 设置目标对象中该属性的值
            Method writeMethod = targetPd.getWriteMethod();
            writeMethod.invoke(target, value);
        }
```

## spring beanUtils

spring beanUtils 的 API 和 apache commons beanUtils 差不多，也是简单的一句代码。但是，**前者只支持同类型属性的转换，且不支持自定义转换器**。

```java
    @Benchmark
    public UserVO testSpringBeanUtils(CommonState commonState) throws Exception {
        UserVO userVO = new UserVO();
        org.springframework.beans.BeanUtils.copyProperties(commonState.user, userVO);
        assert "zzs0".equals(userVO.getName());
        return userVO;
    }
```

看过 spring beanUtils 源码就会发现，它只是一个简单的工具类，只有短短几行代码。原理的话，和 apache commons beanUtils 一样的，所以，**它的复制也是浅度复制**。

## cglib beanCopier

cglib beanCopier 需要先创建一个`BeanCopier`（这个对象会缓存起来，不需要每次都创建），然后再执行 copy 操作。它也支持设置自定义转换器，需要注意的是，**这种转换器仅限当前调用有效，而且，我们需要在同一个转换器里处理所有类型的转换**。

```java
    @Benchmark
    public UserVO testCglibBeanCopier(CommonState commonState) throws Exception {
        BeanCopier copier = BeanCopier.create(commonState.user.getClass(), UserVO.class, false);
        UserVO userVO = new UserVO();
        copier.copy(commonState.user, userVO, null);
        assert "zzs0".equals(userVO.getName());
        return userVO;
        
        // 设置自定义转换器
        /**BeanCopier copier = BeanCopier.create(commonState.user.getClass(), UserVO.class, true);
        UserVO userVO = new UserVO();
        copier.copy(commonState.user, userVO, new Converter() {
            @Override
            public Object convert(Object value, Class target, Object context) {
                if(Integer.class.isInstance(value)) {
                    System.err.println("赋值Integer属性");
                }
                return value;
            }
        });
        assert "zzs0".equals(userVO.getName());
        return userVO;**/
    }
```

cglib beanCopier 的原理也不复杂，它是使用了 asm 生成一个包含所有 setter/getter 代码的代理类，通过设置以下系统属性可以在指定路径输出生成的代理类：

```properties
cglib.debugLocation=D:/growUp/test
```

打开上面例子生成的代理类，可以看到，**源对象属性值的获取、目标对象属性值的设置，都是直接调用对应方法，而不是使用反射**，通过后面的测试会发现它的速度接近我们手动 setter/getter。另外，**cglib beanCopier 也是浅度复制**。

```java

public class Object$$BeanCopierByCGLIB$$6bc9202f extends BeanCopier
{
    public void copy(final Object o, final Object o2, final Converter converter) {
        final UserVO userVO = (UserVO)o2;
        final User user = (User)o;
        userVO.setAccount(user.getAccount());
        userVO.setAddress(user.getAddress());
        userVO.setAge(user.getAge());
        userVO.setBirthday(user.getBirthday());
        userVO.setDepartment(user.getDepartment());
        userVO.setDiploma(user.getDiploma());
        // ······
    }
}
```

## orika mapper

相比其他 bean copy 工具，orika mapper 的 API 要复杂一些，相对地，它的功能也更强大，不仅支持注册自定义转换器，还支持注册对象工厂、过滤器等。使用 orika mapper 需要注意，**`MapperFactory`对象可复用，不需要重复创建**。

```java
    @Benchmark
    public UserVO testOrikaBeanCopy(CommonState commonState, OrikaState orikaState) throws Exception {
        MapperFacade mapperFacade = orikaState.mapperFactory.getMapperFacade();// MapperFacade对象始终是同一个
        UserVO userVO = mapperFacade.map(commonState.user, UserVO.class);
        assert "zzs0".equals(userVO.getName());
        return userVO;
    }
    @State(Scope.Benchmark)
    public static class OrikaState {
        MapperFactory mapperFactory;
        @Setup(Level.Trial)
        public void prepare() {
            mapperFactory = new DefaultMapperFactory.Builder().build();
            /*mapperFactory.getConverterFactory().registerConverter(new CustomConverter<Boolean, Integer>() {
                @Override
                public Integer convert(Boolean source, Type<? extends Integer> destinationType, MappingContext mappingContext) {
                    if(source == null) {
                        return null;
                    }
                    return source ? 1 : 0;
                }
            });*/
        }
    }
```

**orika mapper 和 cglib beanCopier 有点类似，也会生成包含所有 setter/getter 代码的代理类，不同的是 orika mapper 使用的是 javassist，而 cglib beanCopier 使用的是 asm**。

通过设置以下系统属性可以在指定路径输出生成的代理类（本文选择直接输出java文件）：

```properties
# 输出java文件
ma.glasnost.orika.GeneratedSourceCode.writeSourceFiles=true
ma.glasnost.orika.writeSourceFilesToPath=D:/growUp/test
# 输出class文件
# ma.glasnost.orika.GeneratedSourceCode.writeClassFiles=true
# ma.glasnost.orika.writeClassFilesToPath=D:/growUp/test
```

和 cglib beanCopier 不同，orika mapper 生成了三个文件。根本原因在于 **orika mapper 是深度复制**，用户对象中的部门对象和岗位对象也会生成新的实例对象并拷贝属性。

![orika_class](D:\growUp\git_repository\performance-testing\beanCopy-tool-demo\img\orika_class.png)

打开其中一个文件，可以看到，普通属性直接赋值，像部门对象这种，会调用`BoundMapperFacade`继续拷贝。

```java
public class Orika_UserVO_User_Mapper166522553009000$0 extends ma.glasnost.orika.impl.GeneratedMapperBase {

    public void mapAtoB(java.lang.Object a, java.lang.Object b, ma.glasnost.orika.MappingContext mappingContext) {

        super.mapAtoB(a, b, mappingContext);
        // sourceType: User
        cn.zzs.bean.copy.other.User source = ((cn.zzs.bean.copy.other.User)a);
        // destinationType: UserVO
        cn.zzs.bean.copy.other.UserVO destination = ((cn.zzs.bean.copy.other.UserVO)b);

        destination.setAccount(((java.lang.String)source.getAccount()));
        destination.setAddress(((java.lang.String)source.getAddress()));
        destination.setAge(((java.lang.Integer)source.getAge()));
        if(!(((cn.zzs.bean.copy.other.Department)source.getDepartment()) == null)) {
            if(((cn.zzs.bean.copy.other.Department)destination.getDepartment()) == null) {
                destination.setDepartment((cn.zzs.bean.copy.other.Department)((ma.glasnost.orika.BoundMapperFacade)usedMapperFacades[0]).map(((cn.zzs.bean.copy.other.Department)source.getDepartment()), mappingContext));
            } else {
                destination.setDepartment((cn.zzs.bean.copy.other.Department)((ma.glasnost.orika.BoundMapperFacade)usedMapperFacades[0]).map(((cn.zzs.bean.copy.other.Department)source.getDepartment()), ((cn.zzs.bean.copy.other.Department)destination.getDepartment()), mappingContext));
            }
        } else {
            {
                destination.setDepartment(null);
            }
        }

        // ······

        if(customMapper != null) {
            customMapper.mapAtoB(source, destination, mappingContext);
        }
    }

    public void mapBtoA(java.lang.Object a, java.lang.Object b, ma.glasnost.orika.MappingContext mappingContext) {
        // ······
    }
}
```

# 测试结果

以下以吞吐量作为指标，相同条件下，吞吐量越大越好。

cmd 指令如下：

```shell
mvn clean package
java -ea -jar target/benchmarks.jar -f 1 -t 1 -wi 10 -i 10
```

测试结果如下：

```shell
# JMH version: 1.25
# VM version: JDK 1.8.0_231, Java HotSpot(TM) 64-Bit Server VM, 25.231-b11
# VM invoker: D:\growUp\installation\jdk1.8.0_231\jre\bin\java.exe
# VM options: -ea
# Warmup: 10 iterations, 10 s each
# Measurement: 10 iterations, 10 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
Benchmark                          Mode  Cnt      Score     Error   Units
BeanCopyTest.testApacheBeanUtils  thrpt   10      4.184 ±   0.033  ops/ms
BeanCopyTest.testCglibBeanCopier  thrpt   10   7583.844 ±  79.572  ops/ms
BeanCopyTest.testDeadCode         thrpt   10  15830.863 ± 601.998  ops/ms
BeanCopyTest.testOrikaBeanCopy    thrpt   10   1305.527 ±  45.422  ops/ms
BeanCopyTest.testSpringBeanUtils  thrpt   10     92.738 ±  17.369  ops/ms
```

根据测试结果，对象拷贝速度方面：

**手动拷贝 > cglib beanCopier > orika mapper > spring beanUtils > apache commons beanUtils**

由于 apache commons beanUtils 和 spring beanUtils 使用了大量反射，所以速度较慢；

cglib beanCopier 和 orika mapper 使用动态代理生成包含 setter/getter 的代码的代理类，不需要调用反射来赋值，所以，速度较快。orika mapper 是深度复制，需要额外处理对象类型的属性转换，也增加了部分开销。



以上数据仅供参考。感谢阅读。




> 相关源码请移步：[ beanCopy-tool-demo](https://github.com/ZhangZiSheng001/beanCopy-tool-demo)

> 本文为原创文章，转载请附上原文出处链接：[https://www.cnblogs.com/ZhangZiSheng001/p/13948414.html](https://www.cnblogs.com/ZhangZiSheng001/p/13948414.html)