## OutputStream

```
OutputStream("file-name")
```

> opens a file and returns an OutputStream.

### Examples

```
>> f = FileNameJoin({$TemporaryDirectory, \"test_open.txt\"})
/tmp/test_open.txt

>> str = OpenWrite(f);  

>> Write(str, x^2 - y^2)
 
>> Close(str);
```

`FilePrint` prints the file content:

```
>> FilePrint(f)
```

as:

```
x^2 - y^2 
```

```
>> str = OpenAppend(f); 

>> Write(str, x^2 + y^2) 

>> Close(str);
```

`FilePrint` prints the file content:

```
>> FilePrint(f)
```

as:

```
x^2 - y^2
x^2 + y^2 
```
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of OutputStream](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/FileFunctions.java#L825) 
