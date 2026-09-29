# matheclipse-astro - third-party notices

This module is licensed under the GNU General Public License version 3. It contains code and data
from the projects below.

## Night Vision

[Night Vision](https://sourceforge.net/projects/nightvision/) 5.5, Copyright (C) 2011-2026 Brian
Simpson, licensed under the GNU General Public License version 3 or (at your option) any later
version. Source: `https://git.code.sf.net/p/nightvision/code`, commit `84b2522`.

Only formulas, numeric tables and the star database were taken; the drawing code, the Swing user
interface and the preferences were not. Every ported file keeps the original copyright and licence
notice and says how it was modified.

| Symja file | Ported from (`com.nvastro.nvj`) |
| --- | --- |
| `meeus/DeltaT.java` | `DeltaT` |
| `meeus/Nutation.java` | `Nutate` |
| `meeus/Precession.java` | `Rotation` (precession) |
| `meeus/SiderealTime.java` | `LST` (sidereal time), `Rotation` (horizontal coordinates) |
| `meeus/Mat3.java` | `Matrix3x3`, `Matrix3x1` |
| `meeus/Vsop87.java`, `meeus/Vsop87*.java` | `NearSkyDB` (`Planet` class and its tables; the table classes are generated) |
| `meeus/PlutoTheory.java` | `NearSkyDB` (`Pluto` class) |
| `meeus/MoonTheory.java` | `NearSkyDB` (`Moon` class) |
| `meeus/MeeusEphemeris.java` | `NearSkyDB` (`getCoordinates`, parallax, FK5 conversion) |
| `meeus/PlanetPhotometry.java` | `NearSkyDB` (planet magnitudes, angular sizes) |
| `meeus/SpectralColor.java` | `StarDB` (star colours) |
| `sky/DeepStarCatalog.java` | `StarDB` (`star.db` reader) |
| `resources/star-data/star.db` | `data/star.db`, byte for byte |

The algorithms are from Jean Meeus, *Astronomical Algorithms*, 2nd edition, Willmann-Bell 1998.
The delta T table is from `https://webspace.science.uu.nl/~gent0113/deltat/deltat_modern.htm`. The
star colours are after Mitchell Charity, `http://www.vendian.org/mncharity/dir3/starcolor/`.

## ASCC-2.5

`star-data/star.db` is derived from the All-Sky Compiled Catalogue of 2.5 million stars, 3rd
version: Kharchenko N.V., Roeser S., 2009, VizieR catalogue I/280B. See
`src/main/resources/star-data/README.md`.

## d3-celestial

The files in `src/main/resources/sky-data/` are from
[d3-celestial](https://github.com/ofrohn/d3-celestial), BSD 3-Clause licence. See the README there.

## Natural Earth

`src/main/resources/geo-data/ne_110m_land.geojson` is from
[Natural Earth](https://www.naturalearthdata.com/), public domain.

## Orekit data

The files in `src/main/resources/orekit-data/` are a subset of the
[Orekit data](https://gitlab.orekit.org/orekit/orekit-data) bundle. See the README there.
