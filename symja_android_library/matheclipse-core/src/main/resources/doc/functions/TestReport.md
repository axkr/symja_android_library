## TestReport

```
TestReport("file-name-string")
```

> load the unit tests from a `file-name-string` and print a summary of the `VerificationTest` included in the file. 

```
TestReport("url-string")
```

> load the unit tests from a URL `url-string` (starting with `http://` or `https://`) and print a summary of the `VerificationTest` included in the file. 

See
* [Wikipedia - Unit_testing](https://en.wikipedia.org/wiki/Unit_testing)

### Examples

In the [MMA console](https://github.com/axkr/symja_android_library/wiki/MMA-console-usage) execute a test located in a Github repository

```
>> TestReport["https://raw.githubusercontent.com/antononcube/MathematicaForPrediction/master/UnitTests/SSparseMatrix-tests.wlt"]

```

### Related terms 
[TestResultObject](TestResultObject.md), [VerificationTest](VerificationTest.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of TestReport](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/UnitTestingFunctions.java#L48) 
