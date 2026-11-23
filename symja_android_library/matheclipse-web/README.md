# matheclipse-web (CheerpJ spike)

Feasibility spike: run Symja in the browser with CheerpJ 5.0 in library mode, inside a Web Worker.
`SymjaCheerpJ` (Java) exposes `init`, `eval`, `benchmarkInputs`, `selfTestNames` and `selfTest`, all returning JSON strings.
`spike.html` starts `spike-worker.js`, times each step, and exposes the results as `window.spikeResults` (`window.spikeDone` is set when finished).

## Build

From `symja_android_library/`:

    mvn -pl matheclipse-web -am package -DskipTests

Jars and `classpath.txt` end up in `matheclipse-web/target/site/jars/`.

## Run

    node matheclipse-web/tools/range-server.mjs [--port 8787] [--no-store] [--site <dir>] [--jars <dir>]

The server supports HTTP Range requests (required by CheerpJ), never compresses, serves `/jars/*` from `target/site/jars/` and the rest from `src/main/site/`.
`--no-store` disables caching so every jar load is cold. Stats are at `/__stats` (`?reset=1` clears them).

Open <http://localhost:8787/spike.html?version=17&auto=1> (`version` is 17 or 21; without `auto=1` press Start).

Built with CheerpJ (Community licence).
