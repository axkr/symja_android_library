## TeXForm

```
TeXForm(expr)
```

> returns the TeX form of the evaluated `expr`. 
 
See
* [Wikipedia - LaTeX](https://en.wikipedia.org/wiki/LaTeX)

### Examples

```
>> TeXForm(D(sin(x)*cos(x),x))
"{\cos(x)}^{2}-{\sin(x)}^{2}"
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of TeXForm](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/OutputFunctions.java#L773) 
