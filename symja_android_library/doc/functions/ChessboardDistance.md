## ChessboardDistance

```
ChessboardDistance(u, v)
```

> returns the chessboard distance (also known as Chebyshev distance) between `u` and `v`, which is the number of moves a king on a chessboard needs to get from square `u` to square `v`.


See:  
* [Wikipedia - Chebyshev distance](https://en.wikipedia.org/wiki/Chebyshev_distance)


### Examples

```
>> ChessboardDistance({-1, -1}, {1, 1})
2
```