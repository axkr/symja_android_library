## PolarPlot

```
PolarPlot(function, {t, tMin, tMax})  
```

> generate a JavaScript control for the polar plot expressions `function` in the `t` range `{t, tMin, tMax}`.

See
* [Wikipedia - Polar coordinate system](https://en.wikipedia.org/wiki/Polar_coordinate_system)

### Examples

A polar coordinate system is a two-dimensional coordinate system in which each point on a plane is determined by a distance from a reference point and an angle from a reference direction.

    
This command shows an HTML page with a JavaScript parametric plot control.
 
```
>> PolarPlot(1-Cos(t), {t, 0, 2*Pi})
```

With `JSForm` you can display the generated JavaScript form of the `PolarPlot` function

```
>> PolarPlot(1-Cos(t), {t, 0, 2*Pi}) // JSForm
```

Here is a 5-blade propeller, or maybe a flower, using `PolarPlot`:

```
>> PolarPlot(Cos(5*t), {t, 0, Pi})
```
 
The number of blades and be change by adjusting the `t` multiplier. A slight change adding `Abs` turns this a clump of grass:

```
>> PolarPlot(Abs(Cos(5*t)), {t, 0, Pi})
```

Coils around a ring:

```
>> PolarPlot({1, 1 + Sin(20*t) / 5}, {t, 0, 2*Pi})
```

A spring having 16 turns:

```
>> PolarPlot(Sqrt(t), {t, 0, 16*Pi})
```


### Related terms 
[JSForm](JSForm.md), [Manipulate](Manipulate.md), [ParametricPlot](ParametricPlot.md) [Plot](Plot.md), [Plot3D](Plot3D.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of PolarPlot](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/PolarPlot.java#L14) 
