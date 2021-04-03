## JavaClass

```
JavaClass[class-name]
```

> a `JavaClass` expression can be created with the `LoadJavaClass` function and wraps a Java `java.lang.Class` object. All static method names are assigned to a context which will be created by the last part of the class name. 

**Note**: the Java specific functions which call Java native classes are only available in the MMA mode in a local installation. All symbol and method names have to be case sensitive.

### Examples

```
>> clazz= LoadJavaClass["java.lang.Math"]
JavaClass[java.lang.Math]

>> Math`sin[0.5]
0.479426

>> Math`sinh[0.6]
0.636654
```
 

### Related terms
[InstanceOf](InstanceOf.md), [JavaNew](JavaNew.md), [JavaObject](JavaObject.md), [JavaObjectQ](JavaObjectQ.md), [JavaShow](JavaShow.md), [LoadJavaClass](LoadJavaClass.md), [SameObjectQ](SameObjectQ.md)