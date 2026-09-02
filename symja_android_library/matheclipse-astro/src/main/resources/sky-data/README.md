# Bundled sky catalogue

These files are taken verbatim from [d3-celestial](https://github.com/ofrohn/d3-celestial)
(commit `7e720a3de062`, 2022-07-05), which is released under the **BSD 3-Clause licence**. They are
loaded from the classpath by `org.matheclipse.astro.sky.SkyCatalog`.

Coordinates are GeoJSON `[longitude, latitude]` in degrees at the **J2000** epoch, with right
ascension mapped to the range −180…180. `SkyCatalog` converts longitude back to right ascension in
0…360 on load. Storing the sky this way is what lets
`org.matheclipse.astro.project.MapProjection` serve both sky charts and world maps — projecting the
celestial sphere is the same problem as projecting the Earth.

Each file is parsed on first use and then cached, so a session that only asks for
`StarData("Sirius")` never reads the 5 MB magnitude-8.5 file or the 3 MB deep-sky one.

| File | Size | Records | Contents |
| --- | ---: | ---: | --- |
| `stars.6.json` | 657 KB | 5,044 | Stars to magnitude 6 — everything visible to the unaided eye. Used for whole-sky charts. `id` is the Hipparcos number; properties are `mag` and `bv`. |
| `stars.8.json` | 5.4 MB | ~40,000 | Stars to magnitude 8.5. Used once a chart is zoomed in far enough to show them. |
| `starnames.json` | 681 KB | 4,869 | Keyed by Hipparcos number: `name` (proper name), `bayer`, `flam`, `var`, `hd`, `gl`, `c` (IAU constellation code), plus names in many languages. |
| `constellations.json` | 51 KB | 89 | IAU code, `name`, `gen` (Latin genitive), `rank`, label position. 89 rather than 88 because Serpens is split into Caput and Cauda. |
| `constellations.lines.json` | 27 KB | 150 rings | Constellation figure lines. |
| `constellations.bounds.json` | 41 KB | 89 rings | IAU constellation boundaries (Davenhall & Leggett 1989). |
| `messier.json` | 22 KB | 110 | The Messier objects: `name`, `desig`, `alt`, `type`, `mag`, `dim`. |
| `dsos.14.json` | 3.0 MB | ~14,000 | Deep-sky objects to magnitude 14, from the Saguaro Astronomy Club database. |
| `dsonames.json` | 236 KB | — | Deep-sky object name cross-index. |
| `milkyway.json` | 534 KB | 202 rings | Milky Way outline, drawn by `AstroBackground`. |

## Deliberately not included

The **HYG database** would add distance, parallax, spectral class, absolute magnitude and proper
motion, and would let `StarData` cover most of what the Wolfram Language offers. It is
CC BY-SA 4.0, which would put a share-alike obligation on this otherwise LGPL-3.0 module, so it was
left out by decision. `StarData` reports those properties as unsupported rather than guessing at
them.

The magnitude-14 and magnitude-20 star files, the Chinese sky culture, the asterism set and the
timezone polygons are all present upstream and are not needed here.

## Refreshing

Copy the files from the upstream `data/` directory and update the commit hash above. Nothing in the
loader depends on file ordering, and unknown property keys are ignored, so a newer upstream release
should drop in.
