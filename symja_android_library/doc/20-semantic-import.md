# Semantic import and Datasets

The semantic import and datasets functionality is implemented with the help of the [Tablesaw](https://github.com/jtablesaw/tablesaw) dataframe and visualization library. We'll use Tablesaw to look at data about Tornadoes.  

## Exploring Tornadoes 

To give a better sense of how `SemanticImport` and `Dataset` works, we’ll use a tornado data set from NOAA. Here’s what we’ll cover:

* Read a CSV file
* Viewing dataset metadata
* Previewing data
* Sorting
* Running descriptive stats (mean, min, max, etc.) 
* Filtering rows
* Totals and sub-totals
* Saving your data

All the data is in the Symja *data* folder.  

### Read a CSV file

Here we read a `csv` file of tornado data. The `SemanticImport` function infers the column types by sampling the data and returns a `Dataset` variable named `ds`.

```
>> ds = SemanticImport("./data/tornadoes_1950-2014.csv")
```

**Note:** that the file is addressed relative to the current working directory. You may have to change it for your code. 

If you would like to create smaller datasets you can use the `SemanticImportString` function, which creates a `Dataset` from a String representation:

```
>> sds = SemanticImportString("Products,Sales,Market_Share\n
a,5500,3\n
b,12200,4\n
c,60000,33")
```

### Viewing table metadata

Often, the best way to start is to print the column names for reference:

```
>> Keys(ds)
```

The `Dimensions` function displays the row and column counts:

```
>> Dimensions(ds)
```

`Structure` shows the index, name and type of each column. Like many other `Dataset` functions, `Structure` returns a `Dataset`.

```
>> ts=Structure(ds)
```

You can then produce a string representation for display. For convenience, calling `ToString` on a `Dataset` produces a string representation of the table. 

```
>> ts // ToString // InputForm
```

You can also perform other table operations on it. For example, the code below removes all columns whose type isn’t DOUBLE:
            
```
>> ts(Select(#"Column Type" == "DOUBLE" &))
```

Of course, that also returned a `Dataset`. We’ll cover selecting rows in more detail later.

### Previewing data

The `Span` operator `;;` returns a new table containing the first 3 rows.
        
```
>> ds(1;;3)
```

This will create a new table containing all rows but only the `State` column

```
>> ds(All, "State")
```

This will create a new table containing all rows but only the columns 3 to 5

```
>> ds(All,3;;5)
```

This will create a new table containing rows `1` to `10` but only the columns `State`, `Length` and `Width`

```
>> ds(1;;10,{"State", "Length", "Width"})
```

The `Normal` function converts a table into a list of Symja associations `<|"column-name1"->value1, ... |>`.

```
>> Normal(ds(1;;3))
```

The `InputForm` function shows that the column names are converted to keys of type `String` in the associations.

```
>> Normal(ds(1;;3)) // InputForm
```

### Sorting

Now lets sort the table in reverse order by the id column. The negative sign before the name indicates a descending sort.

```
@@snip [sort_on](./src/main/java/tech/tablesaw/docs/Tutorial.java)
```

### Descriptive statistics

Descriptive statistics are calculated using the `Summary` function:

```
>> Summary(ds) 
```

### Filtering

You can write your own `Select` function to filter rows.

```
>> ds(Select(Slot("Width") > 300 && Slot("Length") > 10 &))
```

The next example returns a `Dataset` containing only the columns named in the parameter list, rather than all the columns in the original.

```
>> ds(All, {"State", "Date"})
```

### Totals and sub-totals

Column metrics can be calculated using methods like Total, Mean, Max, etc.

You can apply those methods to a table, calculating results on one column, grouped by the values in another.

```
>> ds(Total, "Injuries")

>> ds(Mean, "Injuries")
```

### Saving your data

To save a table, you can write it as a CSV file:

```
>> Export("tornado_first_3_rows.csv", ds(1;;3)) 
```

If you would like to create a `String` representation you can use the function `ExportString`

```
>> ExportString(ds(1;;3), "csv")
```