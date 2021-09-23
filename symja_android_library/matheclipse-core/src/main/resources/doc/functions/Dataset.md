## Dataset

``
Dataset( association )
```

> create a `Dataset` object from the `association` 

Dataset uses:   
* [Github - JTablesaw - Java dataframe and visualization library ](https://github.com/jtablesaw/tablesaw)

### Examples

```
>> dset=Dataset@<|101 -> <|"t" -> 42, "r" -> 7.5|>, 102 -> <|"t" -> 42, "r" -> 7.5|>, 103 -> <|"t" -> 42, "r" -> 7.5|>|>

      |  t   |   r   |
----------------------
 101  |  42  |  7.5  |
 102  |  42  |  7.5  |
 103  |  42  |  7.5  |
```
    

### Related terms
[SemanticImport](SemanticImport.md), [SemanticImportString](SemanticImportString.md)