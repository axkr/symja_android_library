## ImageApply

```
ImageApply(f, image)
```

> replaces every pixel of `image` by `f` of it: a number for a greyscale image, the list of channel
> values for a colour one, on the scale `0.0 ... 1.0`.

```
ImageApply(f, image, Masking -> mask)
```

> applies `f` only where `mask` is positive and passes every other pixel through unchanged.

The mask is an image or a matrix of the same size, or a smaller one centred on the image, or a
`Graphics` drawn at the image's size, whose drawn pixels are the ones included. `Masking -> All` and
`Masking -> None` apply `f` everywhere.

### Examples

```
>> ImageData(ImageApply(1-#&, Image({{0.25,0.5},{0.75,0.0}}), Masking -> {{1,0},{0,1}}), "Byte")
{{191,128},{191,255}}
```

### Related terms
[ConstantImage](ConstantImage.md), [ImageData](ImageData.md)

### Implementation status

* &#x1F9EA; - experimental
