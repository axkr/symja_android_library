## GoldbachList

```
GoldbachList(even-number)
```

> return the list of Goldbach prime pairs for the `even-number`

```
GoldbachList(even-number, max-results)
```

> return `max-results` number of pairs. 

See
* [Wikipedia - Goldbach's_conjecture](https://en.wikipedia.org/wiki/Goldbach%27s_conjecture) 
 
### Examples

``` 
>> GoldbachList(64)
{{3,61},{5,59},{11,53},{17,47},{23,41}} 

>> GoldbachList(3325581707333960528,1)
{{9781,3325581707333950747}}
```
