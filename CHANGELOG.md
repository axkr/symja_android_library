# Changelog

Noteworthy changes are documented in this file.

## [Unreleased](https://github.com/axkr/symja_android_library/compare/v3.2.0...HEAD)

- A backslash before a space in a string literal only marks the space: `"a\ b"` is `"a b"`, as in
  Mathematica (confirmed 2026-09-23). The scanner dropped the space along with the backslash, so the
  string came out as `"ab"`. Found through ad-si/Woxi#930.

- `Item("heading")` and `Text("heading")` between the controls of a `Manipulate` are subheadings, as
  a bare string already was; they used to be dropped. Found through ad-si/Woxi#938.

- `KnotData(knot, "ImageData")`: the tube round the knot as a list of one `GraphicsComplex`, its
  rings centred on the space curve. `KnotData(knot)` draws that surface, and the static SVG renderer
  builds its tubes with the same ring geometry. Found through ad-si/Woxi#935.

- A 3D plot of one surface is that surface's `GraphicsComplex`, as Mathematica writes it:
  `Graphics3D(GraphicsComplex(..), opts)` rather than `Graphics3D({GraphicsComplex(..)}, opts)`. So
  `First(RegionPlot3D(..))` is the complex itself, and can be moved or rotated into a scene of its
  own. Several surfaces stay a list. Found through ad-si/Woxi#931.

- `OutputForm` writes an operator as one: `x == y`, `a -> b`, `p && q`, `x < y`, `a; b` rather than
  `Equal(x, y)` and so on. Its two-dimensional printer knew only `+`, `*` and `^` and wrote any other
  head in function form; it now reads the spelling and the precedence from the parser's own
  operator table, and a fraction beside an operator keeps its three lines. Found beside
  ad-si/Woxi#922.

- `Text(expr)` outside a picture is its contents. `Text@Grid(...)` - the Demonstrations idiom for a
  table set as plain text, and a common `Manipulate` body - showed the literal `Text( ... )` round the
  table in the notebook, in MathML and in TeX. As a picture, a `Grid`, `Row` or `Column` cell that is a
  `Dynamic` now shows what it currently evaluates to instead of its source. Found through
  ad-si/Woxi#924.

- `IAST.asList()` gives Java code a read-only `java.util.List<IExpr>` view of an expression: index
  0 is the head and indices 1..n the arguments as `getRule(int)` returns them, so an association
  lists its rules. The view is backed by the expression rather than copied; `subList(1, size())`
  is the arguments alone, which `IAST.asArgsList()` also gives directly. `IAST` cannot implement
  `List` itself because `IExpr.add(IExpr)` is the Hipparchus field addition and returns the sum
  where `List.add` returns a boolean. Symja's own code reads variable, constraint and function
  lists through the view now: the polynomial engines that hand a variable list to the Java Algebra
  System, the plotters that split a list-valued function, and the dataset and molecule builtins
  used to copy those arguments into a fresh list each time only to read them.

- A `Tube` along a path that returns to its start is one closed tube, so `KnotData("Trefoil")` no
  longer shows a seam with a thin black ring where the knot's two ends met. The ring was the outline
  WebGL draws round an open tube's end rims; tubes draw no outline now, in either renderer, which
  also removes the dark stripes the static SVG renderer drew along every tube - its ten sides meet
  at more than the outline angle. The static renderer joins a closed tube's last ring to its first
  and spreads the twist its frame picks up round the loop, so the join shows no kink.

- Lines that lie on a 3D surface - its rim, its mesh, the levels of its `MeshFunctions` - are no
  longer drawn dashed by the static SVG renderer. Its painter sorted each line by its own middle,
  so a line and the face under it came out in random order and the face covered most of the line.
  A line inside a `GraphicsComplex` that also draws polygons is now marked as lying on it; the SVG
  renderer sorts such a line segment by segment and nudges it towards the camera by the depth of a
  face, which still leaves the far side of a sphere's mesh hidden. The WebGL renderer already kept
  hairlines in front of their faces; a line thick enough to be drawn as a tube now gets the same
  nudge.

- The lines `MeshFunctions` draws on a `Plot3D` or `ParametricPlot3D` surface are part of its
  `GraphicsComplex`, as Mathematica writes a mesh: `{style, Line({{i, j}, ...}, VertexColors -> None)}`
  over vertices of the surface, each crossing taking the normal and colour of its nearer sample.
  They used to be separate lines with coordinates beside the complex. They also replace the
  sampling grid's lines instead of being drawn on top of them, as `ParametricPlot3D` already did.

- `TabView` shows the pane its selector picks. `TabView({lbl -> e, ...}, i)` selects by position
  and `TabView({v -> {lbl, e}, ...}, v)` by key; the selector may be a `Dynamic`, as a `Manipulate`
  drives the tabs from a control of its own, and one that names no tab shows the first. The web
  notebook shows the selected pane as it would show it on its own - a 3D pane keeps its interactive
  view - and as a picture the tab strip is drawn above it with the selected label in bold. It used
  to have no rendering at all. Found through ad-si/Woxi#919.

- `ImageApply(f, image, Masking -> mask)` applies `f` only where the mask is positive: an image or
  a matrix, centred on the image when smaller, or a `Graphics` drawn at the image's size. `All` and
  `None` apply it everywhere, and an unknown option is `ImageApply::optx`. The call accepted two
  arguments only. `ConstantImage(v, size)` is new, a grey or a coloured image of one value. Found
  through ad-si/Woxi#915.

- `KnotData` for the torus knots: `"Trefoil"`, `"CinquefoilKnot"`, `"SeptafoilKnot"` and every coprime
  `{"TorusKnot", {p, q}}`, with `"CrossingNumber"`, `"AlexanderBriggsNotation"` and `"SpaceCurve"`,
  `KnotData(knot)` drawn as a tube, and `Entity("Knot", name)` for `EntityValue`. The space curve is
  the textbook parametrization, the same knot as the reference's but not its coefficients. Found
  through ad-si/Woxi#913.

- `ExportString(expr, "SVG")` draws a `Row`, `Column`, `Grid` or `Pane`, and a `SwatchLegend`,
  `LineLegend` or `PointLegend` standing on its own, as the picture a notebook shows. They left the
  call unevaluated. Every cell keeps its natural size and may be a picture, text, a number or a
  further layout; a `Spacer(w)` is a gap of that width rather than the text of its name, and an
  `Animate` or a `Manipulate` in a cell is its first frame. Found through ad-si/Woxi#895, #901 and
  #906.

- `FrameLabel -> Grid(...)` is a table under the frame. A label that is a layout or a picture used
  to be written out as the source text of the expression; it is now drawn as a picture of its own,
  with the room it needs, on the bottom or the left edge. Found through ad-si/Woxi#902.

- `CapForm(None)` leaves a `Cylinder` or a `Cone` in `Graphics3D` open at its ends, in the WebGL and
  in the static SVG renderer. The directive was read for lines in two dimensions only. Found through
  ad-si/Woxi#918.

- `ParametricPlot3D` honours `MeshFunctions`, `Mesh -> {{v1, ...}}` and `MeshShading`. A mesh
  function of the point and the two parameters draws its level lines in place of the parameter
  grid, and `MeshShading -> {s1, s2, ...}` colours the bands between them - a matrix of shades for
  two functions. The options were accepted and ignored. `Plot3D` shares the same code, and now also
  reads explicit levels and a count per function from `Mesh`. The shading is per vertex, so a face
  the boundary crosses blends across it instead of being cut exactly as Mathematica cuts it. Found
  through ad-si/Woxi#916.

- An option given as `Dynamic(...)` is drawn with its current value. `PlotRange -> Dynamic(r)`
  used to leave the option unreadable, so a picture was drawn exactly as if it had no plot range;
  `ViewPoint -> Dynamic(vp)` outside a `Manipulate` likewise fell back to the default view. Both
  the SVG and the WebGL option readers resolve it now. Found through ad-si/Woxi#908.

- `CoordinateBoundingBox` and `CoordinateBounds` take the box around nested points. The faces of
  `Cases(g, Polygon(x_) :> x, Infinity)` are lists of point lists, and the first face used to be read
  as one three dimensional point, giving `{{0,0,1},{2,3,3}}` instead of `{{0,0},{3,3}}`. Found
  through ad-si/Woxi#905.

- `Manipulate` reads `"name" -> spec` as a named group of controls, also with `:>` and with a list
  of specifications. A string keyed rule was read as an option and dropped, so a panel made only of
  groups had no control at all and the whole `Manipulate` vanished. Found through ad-si/Woxi#910.

- `Solve(eqns, dom)` and `NSolve(eqns, dom)` with the domain in place of the variables find the
  variables as the one argument form does: `Solve(x^2==1, Reals)` is `{{x->-1},{x->1}}` rather than
  unevaluated. An `Element(x, dom)` among the equations which only repeats the domain being solved
  over is dropped instead of rejected as no equation. Found through ad-si/Woxi#911.

- `VectorPlot` and `VectorPlot3D` honour an explicit list `VectorPoints -> {p1, p2, ...}`, one arrow
  at each point, and `VectorStyle`. The list used to give the default grid, and the style was
  ignored; a colour in it now replaces the colouring by length unless `VectorColorFunction` is
  given. Found through ad-si/Woxi#895.

- `GroebnerBasis` accepts indeterminates which are not symbols, `{f(x), g(y)}`, as
  `PolynomialReduce` already does: they are computed in symbols standing for them and given back.
  Found through ad-si/Woxi#914.

- Colour directives accept their channels packed into one list. `Hue({h,s,b})`,
  `GrayLevel({g})`, `CMYKColor({c,m,y,k})`, `XYZColor({..})` and `LABColor({..})` are the same
  colours as the spread-out forms, as `RGBColor({r,g,b})` already was - the form
  `Table(RGBColor(RandomReal(1,3)), ...)` produces. `Hue` used to read the list as its hue alone and
  draw red whatever the list said, `GrayLevel` drew black, and the other three were rejected; the
  SVG and WebGL renderers and the colour swatch boxes all read the one shape now. Found through
  ad-si/Woxi#893.

- `ListPlot3D` of scattered `{x,y,z}` points honours `ColorFunction`, `Mesh` and `MeshStyle`. The
  triangulated form was handed no colour builder at all, so a named gradient or a colour function
  changed nothing on it while it worked on a height array; and it drew no mesh. It is coloured by
  the same builder now, and its mesh is every triangle edge once, since a triangulation has no grid
  lines to thin out. Found through ad-si/Woxi#889.

- `SetSystemOptions` answers as the reference implementation does. It was a stub returning `Null`,
  so a notebook's `SetSystemOptions["MungoLevel" -> 3];` was silently discarded. A name that is no
  system option is now `SetSystemOptions::sysname` and the call stays unevaluated - Mathematica's
  answer, probed 2026-09-22, and not the flat rule list ad-si/Woxi#897 returns - while the names
  `SystemOptions` reports are accepted and given back. `SystemOptions` and `SetSystemOptions` have
  documentation pages.

- `Area` of a ball cut by a plane, and of a ball and a box cutting one together. A ball meets a
  plane in a disk of the radius the plane is left with, and a box cuts that disk down to the part
  lying inside its own cross section, so
  `Area(RegionIntersection(Ball({0.5,0.5,0.5},0.5), RegionIntersection(Cube({0.5,0.5,0.5},1), ImplicitRegion(x+y==1,{x,y,z}))))`
  is the `Pi/4` disk that just fits inside the `Sqrt(2)` rectangle. Both shapes are measured in the
  plane's own two directions, where the disk is cut against the polygon wedge by wedge, so a ball
  reaching over the box's edges keeps only what is inside them. A plane that misses the ball, or
  only touches it, cuts nothing. This is the second half of ad-si/Woxi#871.

- `Area` of a box cut by a plane. A solid and a plane meet in a flat cross section, which has an
  area although neither of the two regions does -
  `Area(RegionIntersection(Cube({0.5,0.5,0.5},1), ImplicitRegion(x+y==1,{x,y,z})))` is the
  `Sqrt(2)` rectangle the saw leaves, and a plane that misses the box cuts nothing. The corners are
  the box edges that cross the plane, put in order around the middle and measured by Newell's
  formula. A surface that is not flat, or a region that asks for either part rather than both, is
  left alone. This is the numeric bug behind ad-si/Woxi#871, where the unevaluated area silently
  zeroed a notebook's total.

- `RegionDimension` of a `BooleanRegion` and of an `ImplicitRegion`. An equation cuts one dimension
  away from an implicit region and an inequality cuts none, so `ImplicitRegion(x+y==1,{x,y,z})` is
  a surface. A `BooleanRegion` asking for all of its parts is as thin as the thinnest of them -
  a box cut by a plane is a surface, two solids meet in a solid - and one asking for either of them
  is as wide as the widest, which is what a union always is. Several equations cut away as many
  dimensions as they are independent - the rank of the system they make up - so `x == 1 && y == 1`
  of the space is a line while `x + y == 1 && 2*x + 2*y == 2` is the one plane it is written as
  twice, and implicit parts of a `BooleanRegion` over the same variables are counted together the
  same way. With that, `Area` of two solids that meet in a solid answers `Undefined` instead of
  staying unevaluated. Declined rather than guessed: an `Or` of conditions, equations that
  contradict each other, curved surfaces counted together, and a conjunction with more than one
  part thinner than the space. Checked against Mathematica on 2026-09-20.

- `RegionIntersection` takes a `BooleanRegion` of its own apart. Intersecting a region with an
  intersection that was carried as `BooleanRegion(#1 && #2 &, {...})` asks for all of the parts at
  once, so they are now flattened into one `BooleanRegion(#1 && #2 && #3 &, {...})` as the
  reference implementation writes it, rather than nested. A region asking for either of its parts
  stays one part of its own. Checked against Mathematica on 2026-09-20.

- `RegionIntersection` computes something. It had no evaluator at all - it was an inert head that
  `RegionMember` read as the conjunction of its parts, so even two concentric balls came back as
  they were written. It now works the intersection out wherever it is certainly known: the same
  region twice, an `EmptyRegion` or `FullRegion` among the parts, axis-aligned boxes (the box they
  overlap in, or `EmptyRegion` when they do not), balls and disks about one centre, and
  `ImplicitRegion`s over the same variables, `Interval`s of the line, and regions whose bounds lie
  apart from one another (which meet in nothing). Nested intersections are flattened, and parts
  living in different spaces report `RegionIntersection::regdims` rather than quietly doing
  nothing. Anything else stays as it was written, which keeps `RegionMember` answering for it as
  before - and `Area` and `RegionMeasure` now reach a result wherever the intersection became a
  shape. A pair that cannot be drawn as one shape is carried as
  `BooleanRegion(#1 && #2 &, {reg1, reg2})`, the form the reference gives, and `RegionMember` now
  answers for a `BooleanRegion` - a hand-written one included - by applying its function to the
  parts' own conditions, so a symbolic point gets the condition back. Every case was checked
  against Mathematica on 2026-09-20.

- A `BoundaryMeshRegion` may write its cells the way the reference writes them.
  `BoundaryMeshRegion({{0,0},{1,0},{0,1}}, Line({1,2,3,1}))` was not read as a region at all - it
  had no area, no cells and no picture, in the console and in the servlets alike - because the
  cells were not in a list and the `Line` was a walk along the boundary rather than its edges one
  by one. Both forms are now read: a single cell may stand on its own, and a `Line` of more than
  two indices is that many edges, one per step. The three ways of writing the same triangle now
  give the same area, the same cells and the same picture.

- A parametric plot of nothing is an empty picture. `ParametricPlot({{}}, ...)` and
  `ParametricPlot3D({{}}, ...)` echoed the call back instead of drawing an empty frame, where
  `Plot` already drew one. An empty list is no curve, so a specification of nothing but those is
  nothing to draw - which is what `ParametricPlot3D({If(cond, curves, {}), ...})` comes to when the
  condition removes every curve. A curve that cannot be read is still told apart from no curve, and
  keeps the call unevaluated. Noticed while checking ad-si/Woxi@b274ca0, whose own examples - an
  empty list among the curves, and several curves produced at once by `Through({f,g}(t))` - already
  worked here.

- A mesh region can be looked at. `BoundaryMeshRegion` was dropped without a trace by both the SVG
  and the WebGL renderer, `ExportString(mesh, "SVG")` returned nothing, and `Show` had no evaluator
  at all. A mesh is now drawn the way Mathematica draws it - a two dimensional region as the polygon
  its boundary encloses, a three dimensional one as its faces, unboxed and lit, in the reference's
  own colours - and `Show(mesh)` returns that picture as `Graphics` or `Graphics3D`. Cells styled by
  `MeshCellStyle` are drawn over it in their style, and a styled face replaces the one beneath, so a
  translucent face stays translucent. Both notebook servlets show a mesh result as that picture
  too, without `Show`. `Show(g1, g2, ..., options)` combines graphics of one kind,
  keeping each one's directives to itself and letting the first setting of an option win; it does
  not join plot ranges or combine two and three dimensional graphics.

  This found a bug in the 2D renderer: a list inside `Directive` - `Directive[{Red,
  EdgeForm[Blue]}]`, which is the form a mesh is drawn with - was collected in a scope of its own and
  thrown away, so the shape came out black and without an edge.

- `ConvexHullMesh` takes options. Any argument after the points used to be rejected. Options are now
  placed where Mathematica places them: `MeshCellStyle` is written out cell by cell as a
  `Properties` option ahead of `Method` - a bare style covers every cell of every dimension,
  `{d, All}` and `{d, i}` one dimension or one cell - and any other option is kept, in a list, at
  the end. An argument that is not an option is reported as points that span no hull, as Mathematica
  reports it. From checking ad-si/Woxi@22b4d339, which keeps `MeshCellStyle` as it was given rather
  than in the form Mathematica returns.

- `LinearModelFit` measured R-squared about zero. Its intercept is a column of the design matrix, so
  the regression ran "without intercept", and the same flag chose the uncentered sum of squares - the
  convention for a fit through the origin. A model with a constant term is now measured about the
  mean, as Mathematica does (`0.998301` where it used to say `0.99989`), with the adjusted value
  scaled by `(n-1)/(n-p)`. `IncludeConstantBasis -> False` asks for a fit through the origin, which
  is measured about zero.

  `NonlinearModelFit` is new, on `FindFit`'s Levenberg-Marquardt fitter. Its R-squared is taken
  about the mean too, but its adjusted value scales by `n/(n-p)` - Mathematica's convention for a
  nonlinear model, and the one ad-si/Woxi@0a15f1cb gets wrong. Its `"BestFitParameters"` are rules.

  Every `FittedModel` now answers `"PredictedResponse"` and `"Properties"`, is its best fit function
  when evaluated at a point (`lm(2.5)`), reports an unknown property as `FittedModel::elmntavs` with
  the nearest name there is, and equals its own serialized copy - it used to compare its regression
  object by identity. All of it was checked against Mathematica on 2026-09-19.

- `PlotMarkers -> Automatic` drew nothing. `Automatic` was both the option's internal default and
  the value meaning "no marker", so asking for the standard markers could not be told from not
  asking for anything; only an explicit marker such as `PlotMarkers -> {"x"}` did anything, which
  is why the option matrix never caught it. `Automatic` is now the standard sequence of shapes -
  a disk, a square, a diamond and the two triangles - one per dataset, cycling in step with the
  colours, and `"OpenMarkers"` is the same sequence unfilled.

  The other settings the reference documents work too, and are read the same way by every plot
  that takes the option: `g`, `{g, s}`, `{g1, g2, ...}` and `{{g1,s1}, {g2,s2}, ...}`. A size used
  to be parsed out of a `{marker, size}` pair and then dropped; it is now honoured, as a number of
  printer's points, as `Tiny`/`Small`/`Medium`/`Large`, as `Offset(d)` or as a `Scaled(s)` fraction
  of the plot.

  Every point of a dataset carries a marker, where a joined curve used to get at most sixteen. A
  plot that samples a function keeps the spacing, since a marker on each of a thousand adaptive
  samples is an unreadable smear. `Plot`, `ParametricPlot` and `PolarPlot` honoured the option
  without declaring it, so `Options(Plot)` did not list it and it was undiscoverable there.

  Incidental: an isolated point at the end of a `ListPlot` dataset was added to the picture twice.

- A single isotope can be named. `IsotopeData` used to answer only for an element - a list of its
  mass numbers, and properties of its most abundant isotope - so there was no way to ask about
  carbon-14 rather than about carbon. An isotope is now an entity, `Entity("Isotope", "Carbon12")`,
  written that way or as `"Carbon12"` or `"C12"`, and it answers for `"AtomicMass"`,
  `"AtomicNumber"`, `"BindingEnergy"`, `"IsotopeAbundance"`, `"MassNumber"`, `"NeutronNumber"` and
  `"StandardName"`. `IsotopeData(6)` is the isotopes of carbon and `IsotopeData()` all 3171 of
  them, over every element rather than only the ones that occur in nature - the table was always
  there, in the Chemistry Development Kit, and only the naturally occurring entries were reachable.
  `"Isotope"` is registered as an entity type, so `EntityValue` and `EntityList` reach it like any
  other. A name the table does not know stays unevaluated.

  Every answer was checked against Mathematica on 2026-09-19, and five of them had to be corrected
  from what ad-si/Woxi#835 - the pull request that prompted the work - expects: `"BindingEnergy"` is
  per nucleon (carbon-12 is 7.6801 MeV, not its 92.16 MeV total), `"AtomicMass"` carries
  `"AtomicMassUnit"` rather than `"Daltons"`, `"IsotopeAbundance"` is a `Quantity` in percent and
  zero rather than missing for a nuclide that does not occur in nature, an unknown name stays
  unevaluated, and `{"Carbon", 12}` is not a specifier. `"BindingEnergy"` is computed rather than
  looked up, from the mass excess `(Z m(1H) + N m(n) - M(A,Z)) c^2 / A` with CODATA 2018
  constants, and matches the reference to the figures it prints.

  `ElementData` follows: `"KnownIsotopes"` answers with the isotope entities and
  `"IsotopeAbundances"` with an association keyed by them. It also gained `"StableIsotopes"`, listed
  among its properties as the reference lists it. Stable means stable rather than "occurs in
  nature" - uranium occurs in nature and has no stable isotope - and CDK records abundance but
  nothing about decay, so the stable isotopes are a table of their own, copied from Mathematica for
  all 118 elements and checked against it entry for entry. It keeps the reference's own choices,
  thorium-232 in and bismuth-209 out among them.

- `ColorRules` in `ArrayPlot` and `MatrixPlot` matches a cell the way `Replace` does, instead of by
  a hand-written scan over structural equality. A pattern on the left of a rule now names every
  value it matches - `ColorRules -> {_?Positive -> Red}` used to paint nothing at all, because the
  pattern was compared to each cell as if it were a value - and a `RuleDelayed` such as
  `{x_ :> GrayLevel(x)}` computes its colour from the value it matched, where its right hand side
  used to be taken literally with nothing substituted. The first rule written wins, as under
  `Replace`. This drops the one place the old scan was more forgiving than the Wolfram Language: a
  rule written `1 -> Red` no longer reaches a cell holding `1.0`, just as `1.0 /. 1 -> Red` leaves
  the real alone. Noticed while checking ad-si/Woxi@49410d8 against Symja - the defect fixed there,
  non-real values collapsing through a machine double and taking the colour written for `0`, never
  applied here, because the double conversion was only a fallback behind structural equality.

- A `Compile`d function's list result is a packed tensor, as in the Wolfram Language: its elements
  are unified to the widest numeric type among them, so `Compile({{x, _Real}}, {x, 1})[2.5]` is
  `{2.5, 1.}` and `Clip` of a real argument is real, while an all-integer tensor and an
  integer-valued result such as `Length` stay exact. The Wolfram Language's own serialized
  `CompiledFunction[version, types, ..., Function[...], ...]` - what a notebook saved with
  `SaveDefinitions -> True` or the `InputForm` of a compiled function contains - can now be called:
  it is applied through the uncompiled `Function` it embeds, with `_Real` arguments read as machine
  numbers. Both gaps came from checking Woxi's `Compile` test history against Symja.

- A `Compile`d function with `RuntimeAttributes -> {Listable}` threads over the dimensions of an
  argument beyond the rank its argument template declares, one dimension at a time, as `Listable`
  means in the Wolfram Language (the fix Woxi made in ad-si/Woxi#807). It used to leave threading to
  the engine, which takes every list argument apart - so an array-typed argument was peeled down to
  its scalars, and `Compile({{seg, _Real, 2}}, Length(seg), RuntimeAttributes -> {Listable})` failed
  even when called with a single matrix. A batch of matrices now gives one result per matrix, a
  deeper batch nests, and a kernel reapplied by `Nest` to its own growing result keeps working.
  Separately, an array argument of the wrong rank is reported as `CompiledFunction::cfta` and takes
  the uncompiled fallback: the vector and matrix conversions answer `null` for it, which used to
  reach the compiled body as a `NullPointerException` or a silent `Null`.

- `StruveH` and `StruveL` for a large argument, where the library routine's series in `z^2` needs a
  working precision that grows with it: `StruveH(-0.8+1.2*I, 10007)` took 396 seconds at 25 digits
  and now takes 103 ms, agreeing to every digit. Each is a Bessel function -- which the library has
  a quick large argument method for -- plus an algebraic series (DLMF 11.6.1, 11.6.2), summed like
  the `AngerJ` and `HermiteH` expansions to its smallest term. An integer order of `StruveH` stays
  with the library routine: its `BesselY` divides by `Sin(nu*Pi)` there, and at `1.0` with
  `z == 30` the rounding it divided by instead cost ten of 25 digits.
  All three expansions now accept an answer only when its error estimate is three digits short of
  the working precision, rather than at a fixed 1E-14 -- which had let a 25 digit question be
  answered with 17 good digits wherever the series happened to stop there.

- `AngerJ` and `HermiteH` answer where the cost of the library routine used to grow without
  bound. Both are reached through hypergeometric functions whose working precision has to cover the
  order or the argument, so `AngerJ(-9223372036854775808/11, -0.8)` and
  `HermiteH(1.5707963267948966, 1009)` never returned -- values Mathematica gives at once. Each now
  has the expansion that holds in that regime: for `AngerJ`, integration by parts of
  `1/Pi*Integral(Cos(nu*t - z*Sin(t)))`, whose cost is the same at any order; for `HermiteH`, the
  large argument series `(2*z)^nu*Sum(Pochhammer(-nu/2, k)*Pochhammer((1-nu)/2, k)/k!*(-1/z^2)^k)`.
  Both are asymptotic, so each is summed to its smallest term and that term decides whether the
  answer is returned at all -- otherwise the library routine still has it. Checked against that
  routine at 25 digits: 63 points for `AngerJ` and 62 for `HermiteH`, none differing by more than
  1.5E-24.

- `PolyLog(-n, z)` for a positive integer `n` is the Eulerian numbers over `(1-z)^(n+1)`, and the
  `PolyLogRules.m` rule reached each of those through the explicit double sum -- a power of a big
  integer per term, built as an expression, with a degree-`n` polynomial over a rational left for
  `Together` to cancel. `PolyLog(-40, -3/2)` took 252 ms and `PolyLog(-128, -3/2)` did not finish.
  The recurrence, in Java, is the same arithmetic without the expressions: 5 ms and 2 ms, the
  latter matching Mathematica digit for digit. Orders past 1000 -- 213 ms, and a fraction with
  thousands of digits -- are left unevaluated rather than begun.

- `GammaRegularized(a, z)` is zero at every negative integer `a`, where `Gamma(a)` has a pole, and
  said so only while the integer was still exact. In numeric mode `-2147483648` reaches the rule as
  `-2.147483648*^9`, the test on the type missed it, and `Gamma(a,z)/Gamma(a)` sent apfloat off for
  minutes to reach the same zero -- it arrived quickly only while `|a|` was small.
  `GammaRegularized(-2147483646, 3.0)` answers at once now, as Mathematica does.

- Two searches that outran their own deadline now end with it. `TimeConstrained` and the engine's
  time limit interrupt the thread they wait on, and every check in `EvalEngine` sits between two
  evaluation steps -- so a loop inside one built-in never reached one: the caller got its
  `$Aborted` on time while the thread ran on at full speed. `GoldbachList`'s search and `EulerE`'s
  table of Euler numbers check for interruption as `VisitorCollectionBoolean` already did.
  `GoldbachList` also refuses a number whose half does not fit a Java `int` when no pair limit is
  given -- the guard was written and its result dropped -- and `EulerE(n, z)` built twice the table
  it reads, each unused entry an integer with thousands of digits.

- A symbol's value is read once where it used to be read twice. `Symbol#evaluate` asked
  `hasAssignedSymbolValue()` and then `assignedValue()`, and symbols are global while the engines
  that evaluate them are not -- so a `Clear` in another thread landed between the two reads and
  `ISymbol#evalAssignedValue` was handed a `null`. `BuiltInSymbol#evaluate`, both
  `reassignSymbolValue` overloads and the `$IterationLimit`, `$RecursionLimit` and
  `$OutputSizeLimit` readers had the same pair.

- A file name the file system cannot spell is a message and not an exception. `Path.of` throws
  `InvalidPathException` on a NUL character -- and on more than that under Windows -- and
  `ExpandFileName`, `DirectoryName` and `ParentDirectory` built the path themselves, so a fuzzed
  name came out of the engine as a stack trace. `FileSandbox` refused such a name only inside a
  sandbox root; without one -- the consoles, the JUnit suites, every embedding -- the throw reached
  every built-in that opens a file the user named. Both paths now report `General::fname` and
  return what the built-in returns for a name it cannot use.

- The JUnit suite is split into three tiers, so an ordinary edit/test cycle no longer waits for
  the slow symbolic tests. A test picks its tier with a JUnit 5 `@Tag`.

  ```
  mvn verify                                    fast tier only, what CI runs
  mvn verify -Pall-tests                        fast + slow, run this before pushing
  mvn test -Pslow-tests -pl matheclipse-core    only the slow tier
  mvn -pl matheclipse-io test -Prubi-corpus     the Rubi scoring corpus
  ```

  66 test methods -- 1.3% of them -- took 82% of `matheclipse-core`'s runtime, so tagging those
  `@Tag(TestTags.SLOW)` cuts the default run from about 340s to about 50s while still running
  every test class. `.github/scripts/check-test-budget.py` fails a pull-request build over an
  untagged test that outgrows the fast tier, so this stays true.

  The Rubi corpus in `matheclipse-io` used to be excluded by nothing more than its file names not
  matching the surefire `<includes>`. It is now `@Tag("corpus")` and can be run on purpose, with
  `testFailureIgnore` because it is a scoreboard whose expected values are Rubi's reference output
  rather than a gate. That required migrating it off JUnit 3, so `matheclipse-io` and
  `matheclipse-discord` are now JUnit 5 throughout and `junit-vintage-engine` is gone.

- The wall-clock budgets which bound `Integrate`, `DSolve` and a few other functions can be
  adapted to the machine they run on.

  Several algorithms give themselves a deadline in seconds rather than in work: the Rubi rules
  get 45 seconds inside `Integrate`, one step of the symmetry search for `DSolve` gets 3, and
  so on. Those numbers were measured on one machine, and on a slower one they cut off
  evaluations which would have finished. That is not merely a longer wait: a budget which runs
  out looks exactly like a method which does not apply, so the cascade moves on and answers
  something worse, or nothing at all.

  One factor now scales all of them at once. It is `1.0` on the machine they were tuned on, so
  nothing changes there, and a machine which needs twice as long is given twice as long
  everywhere:

  ```
  java -Dsymja.timeScale=2.5 ...
  java -Dsymja.machineProfile=slow ...      # fast, normal, slow or auto
  SYMJA_TIME_SCALE=2.5 java ...
  ```

  ```java
  MachineProfile.setScale(2.5);
  Config.autoCalibrateTimeScale();          // measure this machine instead of assuming it
  ```

  The `auto` profile measures the machine with `Config.calibrateTimeScale()`, which times a
  fixed piece of big-integer arithmetic and hash-table traffic against the same measurement on
  the machine the budgets were tuned on. It has to be asked for, because a measurement taken on
  a shared or thermally throttled host describes that moment rather than the machine.

  What the factor deliberately does not touch is a limit which says how long somebody is
  willing to wait rather than how fast the machine is: a `TimeConstrained` written by the user,
  the timeout of a server request, and the timeouts of the consoles.

  Set the factor before the first `Integrate`. The limit the Rubi rules use internally is bound
  to a symbol when those rules are loaded, and a factor which arrives afterwards reaches every
  budget except that one.

  The unused `Config.INTEGRATE_RADICAL_TIMELIMIT_MILLIS` was removed: it documented a limit
  which the radical substitution stage never had.

  Two pieces of tidying came with it. `Integrate` carried its own copy of the watchdog which
  bounds the Rubi rules, with a second thread pool of its own, and now uses the one in
  `IntegrateTimeBudget` that the rational stage already used. And
  `Config.INTEGRATE_RUBI_TIMELIMIT` is now `Config.INTEGRATE_RUBI_RULE_TIMELIMIT_SECONDS`,
  because it stood next to `INTEGRATE_RUBI_TIMELIMIT_MILLIS` while bounding something else
  entirely: one `TimeConstrained` inside a rule, rather than the whole run of the rules.

## [v3.2.0](https://github.com/axkr/symja_android_library/compare/v3.1.1...v3.2.0) - 2026-04-30

- Internal improvements, bug fixes and performance improvements.

## [v3.1.1](https://github.com/axkr/symja_android_library/compare/v3.0.0...v3.1.1) - 2026-02-26

- Java 11 required
  
- Maven modules matheclipse-parser, matheclipse-logging, matheclipse-core are LGPL licensed
  
- Maven modules matheclipse-gpl and dependents are GPL licensed
  
- function documentation: [symja_android_library/doc/function](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions)
  
- migrate JUnit 3 to JUnit 4 (#861)
  
- using apfloat 1.13.0, 1.14.0 [Changelog](http://www.apfloat.org/apfloat_java/history.html)
  
- improved `Position` (#859)
  
- improved `FullSimplify, Simplify, Together` (#856)
  
- improved `LeviCivitaTensor, Hypergeometric0F1Regularized, Hypergeometric1F1Regularized, Hypergeometric2F1Regularized`
  
- Fix bug in `ComplexSym#powPositive)` for n==0
  
- fix some bugs in [Limit](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/Limit.md)
  
- fix bugs in [Minors](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/Minors.md) (#766)
  
- improve `Definition` add [FullDefinition](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/FullDefinition.md) [Save](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/Save.md) function  (#972)
  
- add `SameTest` option for `FixedPoint*, Contains*` and set functions `Union, Intersectioin, Complement`
  
- very basic Matlab read file support(#982)
  
- [FindRoot](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/FindRoot.md): use Newton method as default instead of Brent (#974)
  
- Search keywords in web interface without * at the end (#971)
  
- [FindMinimum](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/FindMinimum.md), FindMaximum add method "SequentialQuadratic" 87857a8d29b721cb9879c7188f46f0071e9962b3
  
- improve, fix bugs in [Merge](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/Merge.md)
  
- added [SubsetCases](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/SubsetCases.md), [SubsetReplace](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/SubsetReplace.md)
  
- improved [N](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/N.md) evaluation (#937, #942)
  
- improved [TrigExpand](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/TrigExpand.md) (#930)
  
- use rational gcd and lcm in [PolynomialGCD](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/PolynomialGCD.md), [PolynomialLCM](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions/PolynomialLCM.md)
  

## [v3.0.0](https://github.com/axkr/symja_android_library/compare/v3.0.0) - 2023-11-11

- Java 11 required
  
- Maven modules matheclipse-parser, matheclipse-logging, matheclipse-core are LGPL licensed
  
- Maven modules matheclipse-gpl and dependents are GPL licensed
  
- function documentation: [symja_android_library/doc/function](https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions)
  
- new function `DedekindNumber` for first 0..9 Dedekind numbers
  
- Renamed ISignedNumber (Symja 2.x) interface to IReal (Symja 3.x) interface
  
- Renamed methods `evalComplex->evalfc` and `evalDouble->evalf`
  
- improved NIntegrate with `GaussKronrod` method (Maven dependency `de.labathome` `AdaptiveQuadrature`
  
- `Hypergeometric2F1` uses apfloat algorithm for `double` and `Complex` values
  
- `EvalEngine#evalDouble()` returns `POSITIVE_INFINITY, NEGATIVE_INFINITY` for `Infinity, -Infinity`
  
- use eclipse-temurin:21_35-jre in JIB Docker script
  
- improve `PolynomialHomogenization` with a Cos/Sin transform to find more solutions
  
- implement SawtoothWave (#783)
  
- improved `Arg, ApplySides, Association, Assumptions, Bessel..., Binomial, BooleanFunction, Cancel, Carlson..., Catalan, CatalanNumber, CholeskyDecomposition, Chop, Complement, ComplexExpand, Count, CorrelationDistance, CosineDistance, D, Derivative, Drop, EigenValues, Eigenvectors, EllipticF, EllipticPi, FactorTerms, FindLinearRecurrence, Function, FunctionExpand, FullSimplify, GCD, Glaisher,Grad, HankelH1, HankelH2, HarmonicNumber, HermiteMatrix, HurwitzZeta, Hypergeometric..., Identity, IdentityMatrix, Import, ImportString, IntegerDigits, Intersection, Interval..., Khinchin, Limit, LinearRecurrence, ListConvolve, ListCorrelate, LogisticSigmoid, MantissaExponent, MapIndexed, MathMLForm, Max, Min, Minors, NMinimize, NestList, NestWhile, NestWhileList, NMaximize, NSolve, OrderedQ, Orthogonalize, PiecewiseExpand, PolyGamma, PowerExpand, PossibleZeroQ, Product, Projection, Quantity, RandomVariate, Range, ReleaseHold, Round, SatisfiabilityInstances, Simplify, Sign, Solve, SphericalHankelH1, SphericalHankelH2, StieltjesGamma, StringSplit, Subfactorial, Sum, Surd, TagSet, TagSetDelayed, Take, TakeLargestBy, TakeSmallestBy, TeXForm, TimeConstrained, Together, Unitize, Union, Zeta` function
  
- new functions: `Adjugate, DeleteMissing, Eigensystem, FromSphericalCoordinates, HermiteH, JacobiP, NumericalOrder, NumericalSort, PrincipalComponents, RealValuedNumericQ, ReIm, SawtoothWave, StringForm, ToSphericalCoordinates` with status `PARTIAL` support
  
- new functions: `ClebschGordan, ThreeJSymbol` with status `EXPERIMENTAL` support
  
- use hipparchus `PowellOptimizer` for non-linear functions in `NMinimize,NMaximize`
  
- new function `PearsonCorrelationTest` for two vectors
  
- icu4j library moved from Maven module `matheclipse-io` to new module `matheclipse-nlp`
  
- implemented `CompleteBipartiteGraphGenerator` in function `CompleteGraph`
  
- improved `N` function for `Rule, RuleDelayed` and `Association` arguments (#824)
  
- define parser input `\[ExponentialE]` as `E`
  
- improved `TeXForm` for `EulerE`
  
- decoupled rules creation from symbol creation in `RulePreprocessor` implementation
  
- improved `LinearModelFit` for vector inputs
  
- improved performance for `Nest, NestWhileList`
  
- built-in functions can have an `ImplementationStatus`
  
- fix bug in `Refine` for `Sin, Csc`
  
- improved `LaplaceTransform` and `InverseLaplaceTransform` with numerical calculations
  
- define new `TeXParser` class based on SnuggleTeX implementation
  
- use choco solver for solving `Integers` domain equations in `Solve`
  
- added `GenerateConditions->True` option to Solve (especially for trigonometric functions)
  
- new class MD2Symja - render Markdown to HTML
  
- implemented PrecedenceForm, Infix, Prefix, Postfix
  
- new opened/closed ends interval data object `IntervalData({min-value, Less/LessEqual, Less/LessEqual, max-value})`
  
- implemented `NormalMatrixQ, FourierDCTMAtrix, FourierDSTMatrix`
  
- implemented `AASTriangle, ASATriangle, SASTriangle,SSSTriangle`
  
- new `ArcLength, Area, Perimeter, Volume` functions
  
- use [github.com/jsxgraph/json2D_JSXGrap](https://github.com/jsxgraph/json2D_JSXGraph) for 2D `Graphics, DiscretePlot, ListLinePlot, ListPlot, LogPlot, LogLogPlot, LogLinearPlot, ListLogPlot, ListLogLinearPlot, ListLogLogPlot` objects
  
- new `TrigSimplifyFu` function (#498)
  
- new `TransformationFunction, RotationTransform, ScalingTranform, ShearingTransform, TranslationTransform` (#583)
  
- new `SequenceCases, SequenceReplace, SequenceSplit`
  
- SymjaBot - Discord bot
  
- new `KroneckerProduct, Hyperfactorial, LowerTriangularMatrixQ, UnitaryMatrixQ, UpperTriangularMatrixQ`
  
- new `CoordinateBounds, ArrayFlatten, HessenbergDecomposition, SchurDecomposition`
  
- implemented multivariate Newton’s method in the `FindRoot` function (#566)
  
- new `ModularInverse, CompositeQ, ConvexHullMesh, CoordinateBoundingBox, QuantityUnit, PadeApproximant, FactorTermsList`
  
- new `BooleanFunction` (#527)
  
- jbang enablement (#515)
  
- new `FactorialPower, LerchPhi, HurwitzLerchPhi, Hypergeometric1F1, HypergeometricPFQ`
  
- implemented Kryo serializer and deserializer (#514)
  
- new `InverseJacobi...` functions (#501)
  

## [v2.0.0](https://github.com/axkr/symja_android_library/releases/tag/v2.0.0) - 2022-03-12

- Java 11 required
- first Maven Central release (contributed by [@HannesWell](https://github.com/HannesWell))
- Maven modules matheclipse-parser, matheclipse-logging, matheclipse-core are LGPL licensed
- Maven modules matheclipse-gpl and dependents are GPL licensed
- Symja script engine moved to Maven module matheclipse-script
- unified/refactored logging - moved basics into new matheclipse-logging Maven module (contributed by [@HannesWell](https://github.com/HannesWell))
- new matheclipse-jar Maven module to create docker container: https://hub.docker.com/r/symja/symja-2.0
- new matheclipse-discord Maven module for a discord bot based on `Discord4J`
- improved graphical output in browser apps: https://github.com/axkr/symja_android_library/wiki/Browser-apps
- browser app Javascript graphics now contains a `jsfiddle` button to analyze the generated `jsxgraph`, `mathcell` and `plotly` iframe sources.
- improved JSON API: https://github.com/axkr/symja_android_library/wiki/API
- pattern-matching made more compatible with Mathematica pattern-matching
- improved rationalization of Java `double` numbers in `Rationalize`
- improved `FindMinimum, FindMaximum, FindRoot`
- new `IAST` interface implementation `ASTRRBTree` uses "structural sharing" similar like collections in Scala or Clojure for example for improved `Expand` performance for very large expressions.
- new functions:  `BioSequence, BioSequenceQ`
- new functions: `ApplySides, AddSides, DivideSides, MultiplySides, SubtractSides`
- improvements for expressions objects: `Association, Dataset, Graph, Quantity, SparseArray`
- improvements in `PiecewiseExpand, PossibleZeroQ, Solve, Eliminate, FunctionExpand, FullSimplify` functions
- new `CarlsonRD, CarlsonRF, CarlsonRG, CarlsonRJ,...` functions
- uses mathics-threejs-backend for `Graphics3D, ListLinePlot3D, ListPointPlot3D, ListPlot3D` functions: https://github.com/Mathics3/mathics-threejs-backend  (contributed by [@TiagoCavalcante](https://github.com/TiagoCavalcante))
- improved JSON for `ImportString, ExportString`
- improved String Regex functions for example in `StringSplit, StringReplace` functions
- uses Janino compiler for `Compile` function: https://github.com/janino-compiler/janino; improved `CompilePrint` function

### Contributers

- [@axkr](https://github.com/axkr)
- [@HannesWell](https://github.com/HannesWell)
- [@shaunlebron](https://github.com/shaunlebron)
- [@TiagoCavalcante](https://github.com/TiagoCavalcante)
- [@tranleduy2000](https://github.com/tranleduy2000)
