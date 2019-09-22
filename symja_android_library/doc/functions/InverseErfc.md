## InverseErfc

```
InverseErfc(z)
```

> returns the inverse complementary error function of `z`.

See
* [Wikipedia - Error_function - Inverse functions](https://en.wikipedia.org/wiki/Error_function#Inverse_functions) 

### Examples 
```  
>> InverseErfc /@ {0, 1, 2}
{Infinity,0,-Infinity}
```