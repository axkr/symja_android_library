## InputString


```
InputString()
```
 
> if the file system is enabled, the user can input a string. 


```
InputString("string")
```
 
> if the file system is enabled, additionally show the `string` in the console.

 


### Examples
 
Convert the input with `ToExpression` into a Symja expression

```
>> ToExpression(InputString("::"))^3
```

### Related terms
[Input](Input.md)[Import](Import.md)