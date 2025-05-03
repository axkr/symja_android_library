## KeyValuePattern

```
KeyValuePattern( {rule-pattern1, rule-pattern1,...})
```

> `KeyValuePattern` is a pattern-matching construct used to identify elements in a collection (such as a list of associations) that match a specified set of key-value pairs. It is particularly useful for filtering data structures like lists of dictionaries or associations based on specific criteria.

### Examples

```
>> peopleData = {<|"Name" -> "Alice", "Age" -> 30, "City" -> "New York"|>,<|"Name" -> "Bob", "Age" -> 25, "City" -> "Los Angeles"|>,<|"Name" -> "Charlie", "Age" -> 35, "City" -> "Chicago"|>,<|"Name" -> "David", "Age" -> 25, "City" -> "New York"|>,<|"Name" -> "Eve", "Age" -> 40, "City" -> "Los Angeles"|>};

>> Cases[peopleData, KeyValuePattern[{"Age" -> 25}]] 
{<|Name->Bob,Age->25,City->Los Angeles|>,<|Name->David,Age->25,City->New York|>}
```
