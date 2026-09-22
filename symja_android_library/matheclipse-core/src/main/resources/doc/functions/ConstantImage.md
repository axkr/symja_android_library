## ConstantImage

```
ConstantImage(v, size)
```

> gives an image whose every pixel is `v`: a number for a greyscale image, a list of channel values
> or a colour for a colour one. `size` is `{width, height}`, or one number for a square.

### Examples

```
>> ImageDimensions(ConstantImage(Red, {3, 2}))
{3,2}
```

### Related terms
[ImageApply](ImageApply.md), [ImageData](ImageData.md)

### Implementation status

* &#x1F9EA; - experimental
