# Bundled Orekit data

These files are a subset of the official
[orekit-data](https://gitlab.orekit.org/orekit/orekit-data) bundle
(`https://gitlab.orekit.org/orekit/orekit-data/-/archive/main/orekit-data-main.zip`).
They are loaded from the classpath by
`org.matheclipse.astro.data.AstroDataContext`, which lists them in its
`BUNDLED_RESOURCES` constant. The directory layout mirrors the upstream bundle so
that refreshing a file is a plain copy; Orekit itself matches its loaders against
the file name only and ignores the directories.

Only what positional astronomy needs is included.

| File | Size | Purpose | Coverage |
| --- | --- | --- | --- |
| `tai-utc.dat` | 3.3 KB | leap seconds (TAI-UTC). Without it there is no UTC time scale and no astronomy function works at all. | 1972 – end of 2026 |
| `itrf-versions.conf` | 4.7 KB | maps the IERS data files to ITRF versions | — |
| `Earth-Orientation-Parameters/IAU-2000/finals2000A.all` | 3.6 MB | IERS Earth orientation parameters, CIO based. Needed for ITRF/TIRF, UT1 and sidereal time. | 1973 – late 2026 (predicted from Aug 2026) |
| `DE-440-ephemerides/lnxp1990.440` | 14.2 MB | JPL DE 440 planetary and lunar ephemerides. Positions of the Sun, Moon and planets. | 1990 – 2149 |

## Deliberately not included

| Upstream file or directory | Size | Why not |
| --- | --- | --- |
| `Earth-Orientation-Parameters/IAU-1980/finals.all` | 3.6 MB | equinox based EOP, only needed for the `IERS_1996`/`IERS_2003` equinox frames. `AstroConvert.earthFrame()` uses `IERSConventions.IERS_2010`, which reads the IAU-2000 file. Add this file if equinox based frames are ever exposed. |
| `Potential/` | 2.6 MB | Eigen 6S gravity field - orbit propagation only |
| `MSAFE/` | 5.6 MB | Marshall Solar Activity Future Estimation - atmospheric drag only |
| `CSSI-Space-Weather-Data/` | 3.4 MB | atmospheric density - drag only |
| `Space-Environment-Data/` | 1.9 MB | Jacchia-Bowman 2008 atmosphere - drag only |
| `fes2004_Cnm-Snm.dat` | 3.5 MB | FES 2004 ocean tide model - orbit propagation only |
| `update.sh`, `pyproject.toml`, `_pyinstaller/`, `.gitattributes` | — | upstream tooling |

## Date range

A date outside the range of these files does not throw - the astronomy functions
report the `orekitdata` message and stay unevaluated:

```
>> SunPosition(GeoPosition({0,0}), DateObject({1989,1,1,12,0,0}))
SunPosition: The external Orekit data files are not available: no data generated around date: 1989-01-01T12:00:00.000Z
```

To widen the range, or to refresh the leap seconds and Earth orientation
parameters as IERS publishes new ones, install the full bundle externally. An
external data set is registered *before* these resources, so it takes precedence:

* set the `orekit.data.path` system property (several directories or ZIP/JAR
  archives, separated by the platform path separator), or
* set the `OREKIT_DATA_PATH` environment variable, or
* unpack the bundle to `$HOME/orekit-data`, or leave the download at
  `$HOME/orekit-data.zip`.
