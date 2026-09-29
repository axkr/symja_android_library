# Bundled deep star catalogue

`star.db` is the star database of [Night Vision](https://sourceforge.net/projects/nightvision/)
5.5 by Brian Simpson (repository `https://git.code.sf.net/p/nightvision/code`, commit `84b2522`,
file `data/star.db`), copied byte for byte. Night Vision is licensed under the GNU General Public
License version 3 or later. It is loaded from the classpath by
`org.matheclipse.astro.sky.DeepStarCatalog`.

Night Vision states that the database is derived from the **ASCC-2.5, 3rd version** (All-Sky
Compiled Catalogue of 2.5 million stars; Kharchenko N.V., Roeser S., 2009, VizieR catalogue
I/280B). Please cite that catalogue when you publish results that rely on these star positions.

| Property | Value |
| --- | --- |
| Size | 25,546,464 bytes |
| SHA-256 | `01cd2ef5d05b964ffd778a2f743418575aca38401433684a33c226d487582f60` |
| Records | 1,064,436 stars, magnitude -1.46 (Sirius) to 11.09 |
| Order | strictly by magnitude, brightest first |
| Epoch | J2000; no proper motion |

Each record is 24 bytes, big endian (Java `DataInputStream`):

| Bytes | Type | Contents |
| ---: | --- | --- |
| 0-7 | `double` | right ascension in radians |
| 8-15 | `double` | declination in radians |
| 16-17 | `short` | visual magnitude times 100 |
| 18-19 | 2 ASCII characters | spectral type, such as `A1`; two NUL bytes when unknown (58% of the stars) |
| 20 | `byte` | constellation number (Night Vision's own numbering) |
| 21 | `byte` | Bayer letter number, 0 for none |
| 22 | `byte` | Flamsteed number, 0 for none |
| 23 | `byte` | Night Vision flags |

Cumulative star counts: 5,254 to magnitude 6.0, 47,580 to 8.0, 135,565 to 9.0, 371,994 to 10.0,
982,168 to 11.0.

## How it is used

Because the file is sorted by magnitude, `DeepStarCatalog` reads only the prefix a query needs:
a chart to magnitude 9 reads the first 3 MB. The loaded part is kept as unit vectors in `float`
arrays, 16 bytes per star, about 17 MB for the whole file.

`AstroGraphics` draws stars from the d3-celestial files in `../sky-data/` down to magnitude 8.5,
which keeps their names and designations, and switches to this file only for fainter limits.
`StarData` reads the spectral class from here by matching positions and magnitudes.

## Refreshing

Do not refresh this file casually: every copy adds another 20 MB blob to the Git history. If it
has to change, copy `data/star.db` from the Night Vision repository, update the commit, size,
checksum and counts above, and run `DeepStarCatalogTest`.
