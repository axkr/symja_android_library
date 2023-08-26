## BetweennessCentrality

```
BetweennessCentrality(graph)
```

> Computes the betweenness centrality of each vertex of a `graph`.

See
* [Wikipedia - Betweenness centrality](https://en.wikipedia.org/wiki/Betweenness_centrality)
* [Youtube - Betweenness centrality](https://youtu.be/0CCrq62TF7U)

### Examples

```
>> BetweennessCentrality( Graph({agent1,agent2,agent3,agent4,agent5}, {agent1<->agent2,agent1<->agent3,agent2<->agent3,agent3<->agent4,agent3<->agent5})) 
{0.0,0.0,5.0,0.0,0.0}
        
>> BetweennessCentrality( Graph({1, 3, 2, 6, 4, 5}, { 2->5, 3->6, 4->6, 1->5, 5->4, 6->1})) 
{5.0,0.0,0.0,7.0,5.0,7.0}
```
    






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of BetweennessCentrality](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/GraphFunctions.java#L1282) 
