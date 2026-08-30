# Bundled world basemap

| File | Size | Records | Contents |
| --- | ---: | ---: | --- |
| `ne_110m_land.geojson` | 138 KB | 127 polygons | World landmass outlines at 1:110m scale, the basemap `GeoGraphics` draws under geographic primitives. |

Taken verbatim from [Natural Earth](https://github.com/nvkelso/natural-earth-vector)
(commit `ca96624a56bd`, 2022-06-02), `geojson/ne_110m_land.geojson`. Loaded by
`org.matheclipse.astro.geo.WorldOutline`.

Natural Earth data is **public domain** — see
<https://www.naturalearthdata.com/about/terms-of-use/>. No permission is required and crediting is
optional; the recommended short form is "Made with Natural Earth."

Coordinates are GeoJSON `[longitude, latitude]` in degrees on WGS84, which is what
`GeoPosition` already carries, so no datum conversion happens on load.

## Why this resolution

1:110m is the coarsest Natural Earth scale and the right one for a whole-world chart: 138 KB, and
detail finer than a few pixels is wasted at that zoom. The next step up, `ne_50m_land.geojson`, is
1.6 MB, and `ne_110m_admin_0_countries.geojson` (839 KB) would add political borders. Either can be
dropped in beside this file if a use for them appears.
