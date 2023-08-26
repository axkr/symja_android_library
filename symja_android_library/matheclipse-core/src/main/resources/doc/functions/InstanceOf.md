## InstanceOf

```
InstanceOf[java-object, "class-name"]
```

> return the result of the Java expression `java-object instanceof class`.

**Note**: the Java specific functions which call Java native classes are only available in the MMA mode in a local installation. All symbol and method names have to be case sensitive.

### Examples

```
>> loc = JavaNew["java.util.Locale", "US"] 
JavaObject[class java.util.Locale]

>> InstanceOf[loc, "java.util.Locale"]
True

>> InstanceOf[loc, "java.io.Serializable"] 
True
```

### Related terms 
[JavaClass](JavaClass.md), [JavaNew](JavaNew.md), [JavaObject](JavaObject.md), [JavaObjectQ](JavaObjectQ.md), [LoadJavaClass](LoadJavaClass.md), [SameObjectQ](SameObjectQ.md)






### Implementation status

* &#x2615; - supported on Java virtual machine 

### Github

* [Implementation of InstanceOf](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/JavaFunctions.java#L125) 
