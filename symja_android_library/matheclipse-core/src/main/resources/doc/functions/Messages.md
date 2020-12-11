## Messages

```
Messages(symbol)
```

> return all messages which are asociated to `symbol`.

### Examples

```
>> a::hello:="Hello world"; a::james:="Hello `1`, Mr 00`2`!"

>> Messages(a) 
{HoldPattern(a::james):>Hello `1`, Mr 00`2`!,HoldPattern(a::hello):>Hello world}
```
