## LoadJavaClass

```
LoadJavaClass["class-name"]
```

> loads the class with the specified `class-name` and return a `JavaClass` expression. All static method names are assigned to a context which will be created by the last part of the class name. 

**Note**: the Java specific functions which call Java native classes are only available in the MMA mode in a local installation. All symbol and method names have to be case sensitive.

### Examples

```
>> clazz= LoadJavaClass["org.jsoup.Jsoup"]
JavaClass[org.jsoup.Jsoup]

>> conn=Jsoup`connect["https://jsoup.org/"]
JavaObject[class org.jsoup.helper.HttpConnection]

>> doc=conn@get[ ];
```

Print the title of the HTML page.

```
>> Print[doc@title[ ]] 
jsoup Java HTML Parser, with the best of HTML5 DOM methods and CSS selectors.
```

### Related terms 
[InstanceOf](InstanceOf.md), [JavaClass](JavaClass.md), [JavaNew](JavaNew.md), [JavaObject](JavaObject.md), [JavaObjectQ](JavaObjectQ.md)






### Implementation status

* &#x2615; - supported on Java virtual machine 

### Github

* [Implementation of LoadJavaClass](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/JavaFunctions.java#L468) 
