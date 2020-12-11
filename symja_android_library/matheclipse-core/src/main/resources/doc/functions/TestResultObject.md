## TestResultObject

```
TestResultObject( ... )
```

> is an association wrapped in a `TestResultObject`returned from `VerificationTest` which stores the results from executing a single unit test.

### Examples


```
>> VerificationTest(3! < 3^3) 
TestResultObject(Outcome->Success,TestID->None)
```

### Related terms 
[TestReport](TestReport.md), [VerificationTest](VerificationTest.md)