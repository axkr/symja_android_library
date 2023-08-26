## FilePrint

```
FilePrint(file)
```

> prints the raw contents of `file`.

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






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of FilePrint](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/FileFunctions.java#L536) 
