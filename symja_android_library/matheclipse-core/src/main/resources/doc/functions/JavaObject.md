## JavaNew

```
JavaNew["class-name"]
```

> create a `JavaObject` from the `class-name` default constructor.

```
JavaNew["class-name", arg1, arg2,...]
```

> create a `JavaObject` from the `class-name` constructor matching the arguments `arg1, arg2,...`

**Note**: the Java specific functions which call Java native classes are only available in the MMA mode in a local installation. All symbol and method names have to be case sensitive.

### Examples

```
>> loc = JavaNew["java.util.Locale", "US"] 
JavaObject[class java.util.Locale]

>> ds =  JavaNew["java.text.DecimalFormatSymbols", loc]
JavaObject[class java.text.DecimalFormatSymbols]

>> dm = JavaNew["java.text.DecimalFormat", "#.00", ds]
JavaObject[class java.text.DecimalFormat]
```

Calls the `format` method of the `dm` Java object.

```
>> dm@format[0.815] // InputForm
".81"
```

### Related terms 
[InstanceOf](InstanceOf.md), [JavaClass](JavaClass.md), [JavaNew](JavaNew.md), [JavaObjectQ](JavaObjectQ.md), [JavaShow](JavaShow.md), [LoadJavaClass](LoadJavaClass.md), [SameObjectQ](SameObjectQ.md)






### Implementation status

* &#x2615; - supported on Java virtual machine 

### Github

* [Implementation of JavaObject](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/JavaFunctions.java#L323) 
