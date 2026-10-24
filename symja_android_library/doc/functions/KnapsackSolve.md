## KnapsackSolve

```
KnapsackSolve({{cost1, value1}, {cost2, value2}, ...}, maxCost)
```

> the number of times each item is taken so that the total value is as large as possible and the total cost is at most `maxCost`. Every item can be taken any number of times.

```
KnapsackSolve({{cost1, value1, maxCount1}, {cost2, value2, maxCount2}, ...}, maxCost)
```

> item `i` can be taken at most `maxCount_i` times; `Infinity` is allowed.

```
KnapsackSolve({{{c11, c12, ...}, value1}, ...}, {maxCost1, maxCost2, ...})
```

> the costs have several dimensions, for example a weight and a volume, and for each dimension `j` the total cost is at most `maxCost_j`.

Costs and values must be exact non-negative rational numbers. `KnapsackSolve` needs the module `matheclipse-or-tools`, a Java port of the knapsack solver of [Google OR-Tools](https://github.com/google/or-tools): an item which may be taken up to `m` times becomes items which are taken `1, 2, 4, ...` times, and the multidimensional branch and bound of OR-Tools finds the best selection. Without the module `KnapsackSolve` stays unevaluated.

See
* [Wikipedia - Knapsack problem](https://en.wikipedia.org/wiki/Knapsack_problem)

### Examples

Three times the item of cost `3` and value `50`:

```
>> KnapsackSolve({{5, 10}, {4, 40}, {6, 30}, {3, 50}}, 10)
{0,0,0,3}
```

The second and the fourth item at most once:

```
>> KnapsackSolve({{5, 10}, {4, 40, 1}, {6, 30}, {3, 50, 1}}, 10)
{0,1,0,1}
```

Weight and volume:

```
>> KnapsackSolve({{{2,3}, 3, 1}, {{3,1}, 4, 1}, {{4,2}, 5, 1}, {{5,5}, 6, 1}}, {5,4})
{1,1,0,0}
```

### Related terms
[LinearOptimization](LinearOptimization.md), [LinearProgramming](LinearProgramming.md), [Maximize](Maximize.md)

### Implementation status

* &#x2611; - partially implemented
