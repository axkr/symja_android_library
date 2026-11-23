# matheclipse-web (Symja in the browser with CheerpJ)

Runs Symja in the browser with CheerpJ in library mode, inside a Web Worker. There are two pages:

- **Notebook**: the matheclipse-io notebook UI with its kernel running in the browser instead of on a server.
- **Spike**: the original feasibility and timing page (`spike.html`).

All commands below are run from `symja_android_library/` (the folder with the parent `pom.xml`).

## Notebook

The notebook page (`index.html` and `media/js/*` of matheclipse-io) normally talks to the `/ajax/*` servlets.
Here `src/main/notebook/kernel*.js` hand those requests to a Web Worker running CheerpJ.
`NotebookKernel` (`call`, `upload`, `download`) answers them through `BrowserNotebook`, which runs the real matheclipse-io servlets' `doGet`/`doPost` with stand-in request, session and response objects.

1. Build the kernel jars into `matheclipse-web/target/notebook/jars/`:

       mvn -pl matheclipse-web -am package -DskipTests

2. Export the documentation pane as static JSON into `matheclipse-web/target/notebook/doc-json/` (or skip this step and pass `--mock` in step 3):

       matheclipse-web/tools/doc-export/export-docs.sh

3. Assemble the static site in `matheclipse-web/target/notebook-site/`:

       matheclipse-web/tools/build-notebook-site.sh

4. Serve it:

       node matheclipse-web/tools/range-server.mjs --site matheclipse-web/target/notebook-site --jars matheclipse-web/target/notebook-site/jars

5. Open <http://localhost:8787/>.

`tools/deploy-cloudflare.sh` deploys `target/notebook-site/` to Cloudflare (`--assemble-only` stops before the upload).
It takes the account ID and API token from `matheclipse-web/.env` (`CF_ACCOUNT_ID`, `CF_WORKER_TOKEN`; git-ignored) or, when that file is missing, from the macOS Keychain items `symja-cf-account-id` and `symja-cf-worker-token`:

    security add-generic-password -a "$USER" -s symja-cf-account-id -w
    security add-generic-password -a "$USER" -s symja-cf-worker-token -w

Each command prompts for the value, so it never lands in the shell history.

## Spike

`SymjaCheerpJ` (Java) exposes `init`, `eval`, `benchmarkInputs`, `selfTestNames` and `selfTest`, all returning JSON strings.
`spike.html` starts `spike-worker.js`, times each step, and exposes the results as `window.spikeResults` (`window.spikeDone` is set when finished).

### Build

    mvn -pl matheclipse-web -am package -DskipTests -Dspike.skip=false

Jars and `classpath.txt` end up in `matheclipse-web/target/site/jars/`.
Without `-Dspike.skip=false`, only the notebook jars are written and the spike page fails with `classpath.txt HTTP 404`.

### Run

    node matheclipse-web/tools/range-server.mjs [--port 8787] [--no-store] [--no-range] [--site <dir>] [--jars <dir>]

The server supports HTTP Range requests (required by CheerpJ), never compresses, serves `/jars/*` from `target/site/jars/` and the rest from `src/main/site/`.
`--no-store` disables caching so every jar load is cold. Stats are at `/__stats` (`?reset=1` clears them).

Open <http://localhost:8787/spike.html?version=17&auto=1> (`version` is 17 or 21; without `auto=1` press Start).

Built with CheerpJ (Community licence).
