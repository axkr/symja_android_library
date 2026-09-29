# Time zone reference locations

`zone.tab` is the file of that name from the [IANA time zone database](https://www.iana.org/time-zones),
release **2026c**, copied unchanged. It is in the public domain (see its header).

It lists, for every time zone, the country and the coordinates of the zone's principal city -
`DE	+5230+01322	Europe/Berlin`. `org.matheclipse.core.data.GeoLocations` reads it to estimate
where a computer is from its time zone, which is what `FindGeoLocation()` and an unset
`$GeoLocation` fall back on in a desktop kernel.

`zone.tab` is used rather than `zone1970.tab` on purpose: `zone1970.tab` merges countries whose
clocks have agreed since 1970, so `Europe/Amsterdam` is missing there and would have to be read as
Brussels. `zone.tab` keeps one entry per country and zone.

To refresh it, copy `zone.tab` from a newer tzdata release (on macOS and Linux it is in
`/usr/share/zoneinfo/`) and update the release above.
