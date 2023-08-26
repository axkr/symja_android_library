## Get

```
Get("path-to-package-file-name")
```

or

```
<<"path-to-package-file-name"
```

> load the package defined in `path-to-package-file-name`. 

This function doesn't work in the web interface. A file system has to be available to load a package.

### Examples

Load the package `VectorAnalysis.m` from file system:

```
<<"VectorAnalysis.m"
```
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Get](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/FileFunctions.java#L591) 
