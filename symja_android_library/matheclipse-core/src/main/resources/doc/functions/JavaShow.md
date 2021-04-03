## JavaShow

```
JavaShow[ java.awt.Window ]
```

> show the `JavaObject` which has to be an instance of `java.awt.Window`.
 

**Note**: the Java specific functions which call Java native classes are only available in the MMA mode in a local installation. All symbol and method names have to be case sensitive.

### Examples

```
>> frame= JavaNew["javax.swing.JFrame", "Simple JFrame Demo"];

JavaShow[frame]
```

### Related terms 
[InstanceOf](InstanceOf.md), [JavaClass](JavaClass.md), [JavaNew](JavaNew.md), [JavaObject](JavaObject.md), [JavaObjectQ](JavaObjectQ.md), [LoadJavaClass](LoadJavaClass.md), [SameObjectQ](SameObjectQ.md)