## Span
```
Span
```
> is the head of span ranges like `1;;3`.

Examples
```
>> ;; // FullForm
Span(1, All)
>> 1;;4;;2 // FullForm
Span(1, 4, 2)
>> 2;;-2 // FullForm
Span(2, -2)
>> ;;3 // FullForm
Span(1, 3)
```

Parsing: 8 cases to consider
```
>> a ;; b ;; c // FullForm
Span(a, b, c)
 
>>   ;; b ;; c // FullForm
Span(1, b, c)
 
>> a ;;   ;; c // FullForm
Span(a, All, c)
 
>>   ;;   ;; c // FullForm
Span(1, All, c)
 
>> a ;; b      // FullForm
Span(a, b)
 
>>   ;; b      // FullForm
Span(1, b)
 
>> a ;;        // FullForm
Span(a, All)
 
>>   ;;        // FullForm
Span(1, All)
```

## Formatting
```
>> a ;; b ;; c
a ;; b ;; c
 
>> a ;; b
a ;; b
 
>> a ;; b ;; c ;; d
(1 ;; d) (a ;; b ;; c)
```