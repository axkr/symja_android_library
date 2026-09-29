# GraalVM reachability metadata for matheclipse-astro

## resources

Every data file of the module is read from the classpath at run time, which a native image only
supports for resources registered here. Without an entry the file is simply missing in the binary.

* **`star-data/star.db`** - the ASCC-2.5 star database of Night Vision, read by
  `sky.DeepStarCatalog` for `AstroGraphics` charts fainter than magnitude 8.5 and for
  `StarData(..., "SpectralClass")`. It adds about 25 MB to the image. Without it
  `DeepStarCatalog.isAvailable()` is false: charts stop at magnitude 8.5 and the spectral class is
  `Missing`.
* **`sky-data/*.json`** - the d3-celestial catalogue read by `sky.SkyCatalog`: stars, star names,
  constellations, deep sky objects, the Milky Way.
* **`geo-data/*.geojson`** - the Natural Earth land outline read by `geo.WorldOutline` for
  `GeoGraphics`.
* **`orekit-data/...`** - the bundled Orekit data subset, found by `data.AstroDataContext` through
  Orekit's `ClasspathCrawler` (the file list is `AstroDataContext.BUNDLED_RESOURCES`). Without
  `tai-utc.dat` there is no UTC time scale and every astronomy function reports `orekitdata`.

A new resource file needs an entry here too; the files are listed individually rather than with
`**` so that the upstream READMEs beside them stay out of the image.
