## VerificationTest

```
VerificationTest(test-expr)
```

> create a `TestResultObject` by testing if `test-expr` evaluates to `True`. 

```
VerificationTest(test-expr, expected-expr)
```

> create a `TestResultObject` by testing if `test-expr` evaluates to `expected-expr`. 

`SameTest` can be used as an option. The default option used is `SameTest->SameQ`.

See
* [Wikipedia - Unit_testing](https://en.wikipedia.org/wiki/Unit_testing)

### Examples


```
>> VerificationTest(3^3, 27) 
TestResultObject(Outcome->Success,TestID->None)
```

### Related terms 
[TestReport](TestReport.md), [TestResultObject](TestResultObject.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of VerificationTest](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/UnitTestingFunctions.java#L180) 
