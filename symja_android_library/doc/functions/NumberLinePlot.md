## NumberLinePlot

```
NumberLinePlot( list-of-numbers )  
```

or

```
NumberLinePlot( { list-of-numbers1, list-of-numbers2, ... } )   
```

> generates a JavaScript control, which plots a list of values along a line. for the `list-of-numbers`.
	 
**Note**: This feature is available in the console app and in the web interface.

### Examples
 
```
>> NumberLinePlot(Table(Prime(x), {x, 10}))
```
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of NumberLinePlot](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NumberLinePlot.java#L14) 
