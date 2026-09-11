## ImageSizeAction

```
ImageSizeAction
```

> an option for `Pane` and related constructs giving what to do when the `ImageSize` setting does not match the size of the contents (Wolfram Language 6.0).

Symja recognises `ImageSizeAction` as a built-in `System` symbol, so a package or a front end that writes it - the WLJS notebook's graphics code does - refers to that symbol rather than creating one in its own context. Symja does not act on it.

The WLJS notebook also reads it as an option of `Graphics`.

### Implementation status

* &#x1F9EA; - experimental
