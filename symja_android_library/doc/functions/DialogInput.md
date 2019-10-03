## DialogInput 


```
DialogInput()
```
 
> if the file system is enabled, the user can input a string in a dialog box. 


```
DialogInput("info-string")
```
 
> if the file system is enabled, additionally show the `info-string` in the dialog box.

 


### Examples

Convert the input with `ToExpression` into a Symja expression

```
>> ToExpression(DialogInput("::"))^3
```
 