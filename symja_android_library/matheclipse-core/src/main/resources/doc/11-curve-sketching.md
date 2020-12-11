## Curve sketching

Let’s sketch the function

```
>> f(x_) := 4 x / (x ^ 2 + 3 x + 5)
```

The derivatives are

```
>> {f'(x), f''(x), f'''(x)} // Together
{(20-4*x^2)/(25+30*x+19*x^2+6*x^3+x^4),(-120-120*x+8*x^3)/(125+225*x+210*x^2+117*x^
3+42*x^4+9*x^5+x^6),(480+1440*x+720*x^2-24*x^4)/(625+1500*x+1850*x^2+1440*x^3+
771*x^4+288*x^5+74*x^6+12*x^7+x^8)}
```
 
To get the extreme values of `f`, compute the zeroes of the first derivatives:

```
>> extremes = Solve(f'(x) == 0, x)
{{x->-Sqrt(5)},{x->Sqrt(5)}}
```
 
And test the second derivative:

```
>> f''(x) /. extremes // N
{1.65086,-0.064079}
```

Thus, there is a local maximum at `x = Sqrt(5)` and a local minimum at `x = -Sqrt(5)`. Compute the inflection points numerically, choping imaginary parts close to `0`:

```
>> inflections = Solve(f''(x) == 0, x) // N // Chop
{{x->4.29983},{x->-1.0852},{x->-3.21463}}
```
 
Insert into the third derivative:

```
>> f'''(x) /. inflections
{0.00671894,-3.67683,0.694905}
```

Being different from `0`, all three points are actual inflection points. `f` is not defined where its denominator is `0`:

```
>> Solve(Denominator(f(x)) == 0, x)
{{x->1/2*(-3-I*Sqrt(11))},{x->1/2*(-3+I*Sqrt(11))}}
```

These are non-real numbers, consequently `f` is defined on all real numbers. The behaviour of `f` at the boundaries of its definition:

```
>> Limit(f(x), x -> Infinity)
0 

>> Limit(f(x), x -> -Infinity)
0
```

Finally, let’s plot f:

```
>> Plot(f(x), {x, -8, 6})
```