package org.matheclipse.core.graphics;

/**
 * Corpus of 2D <code>Graphics</code> expressions used to exercise {@link SVGGraphics}.
 *
 * 
 */
public final class SVGTestCorpus {

  private SVGTestCorpus() {}

  /** Graphics primitives, in all documented argument forms. */
  public static final String[][] PRIMITIVES = { //
      {"point-single", "Graphics[Point[{0, 0}]]"}, //
      {"point-multi", "Graphics[Point[{{0, 0}, {1, 1}, {2, 0}}]]"}, //
      {"point-table", "Graphics[Point[Table[{t, Cos[t]}, {t, -Pi, Pi, 0.2}]]]"}, //
      {"line-simple", "Graphics[Line[{{0, 0}, {1, 1}, {2, 0}}]]"}, //
      {"line-multi", "Graphics[Line[{{{0, 0}, {1, 1}}, {{1, 0}, {2, 1}}}]]"}, //
      {"rectangle-0arg", "Graphics[Rectangle[]]"}, //
      {"rectangle-1arg", "Graphics[Rectangle[{1, 1}]]"}, //
      {"rectangle-2arg", "Graphics[Rectangle[{0, 0}, {2, 1}]]"}, //
      {"rectangle-rounding", "Graphics[Rectangle[{0, 0}, {2, 1}, RoundingRadius -> 0.2]]"}, //
      {"polygon-simple", "Graphics[Polygon[{{1, -1}, {0, 2}, {-1, -1}}]]"}, //
      {"polygon-multi", "Graphics[Polygon[{{{0, 0}, {1, 0}, {1, 1}}, {{2, 2}, {3, 2}, {3, 3}}}]]"}, //
      {"circle-0arg", "Graphics[Circle[]]"}, //
      {"circle-1arg", "Graphics[Circle[{1, 1}]]"}, //
      {"circle-radius", "Graphics[Circle[{0, 0}, 2]]"}, //
      {"circle-ellipse", "Graphics[Circle[{0, 0}, {2, 1}]]"}, //
      {"circle-arc", "Graphics[Circle[{0, 0}, 1, {0, Pi/2}]]"}, //
      {"circle-ellipse-arc", "Graphics[Circle[{0, 0}, {2, 1}, {0, Pi}]]"}, //
      {"disk-0arg", "Graphics[Disk[]]"}, //
      {"disk-radius", "Graphics[Disk[{0, 0}, 2]]"}, //
      {"disk-ellipse", "Graphics[Disk[{0, 0}, {2, 1}]]"}, //
      {"disk-sector", "Graphics[Disk[{0, 0}, 1, {0, Pi/2}]]"}, //
      {"disk-ellipse-sector", "Graphics[Disk[{0, 0}, {2, 1}, {0, Pi}]]"}, //
      {"annulus", "Graphics[Annulus[{0, 0}, {1, 2}]]"}, //
      {"annulus-sector", "Graphics[Annulus[{0, 0}, {1, 2}, {0, Pi/2}]]"}, //
      {"regular-polygon-n", "Graphics[RegularPolygon[5]]"}, //
      {"regular-polygon-r-n", "Graphics[RegularPolygon[2, 6]]"}, //
      {"regular-polygon-full", "Graphics[RegularPolygon[{0, 0}, 1, 8]]"}, //
      {"stadium-shape", "Graphics[StadiumShape[{{0, 0}, {2, 0}}, 0.5]]"}, //
      {"triangle-points", "Graphics[Triangle[{{0, 0}, {1, 0}, {0, 1}}]]"}, //
      {"sss-triangle", "Graphics[SSSTriangle[3, 4, 5]]"}, //
      {"sas-triangle", "Graphics[SASTriangle[3, Pi/3, 4]]"}, //
      {"asa-triangle", "Graphics[ASATriangle[Pi/4, 3, Pi/4]]"}, //
      {"aas-triangle", "Graphics[AASTriangle[Pi/4, Pi/4, 3]]"}, //
      {"parallelogram", "Graphics[Parallelogram[{0, 0}, {{1, 0}, {0.5, 1}}]]"}, //
      {"text-basic", "Graphics[Text[\"hello\", {0, 0}]]"}, //
      {"text-offset", "Graphics[Text[\"hi\", {0, 0}, {-1, 0}]]"}, //
      {"text-direction", "Graphics[Text[\"rot\", {0, 0}, {0, 0}, {0, 1}]]"}, //
      {"text-expr", "Graphics[Text[x^2 + 1, {0, 0}]]"}, //
      {"arrow-simple", "Graphics[Arrow[{{0, 0}, {1, 1}}]]"}, //
      {"arrow-setback", "Graphics[Arrow[{{0, 0}, {1, 1}}, 0.1]]"}, //
      {"arrow-multi", "Graphics[Arrow[{{{0, 0}, {1, 1}}, {{1, 0}, {2, 1}}}]]"}, //
      {"arrowheads-named", "Graphics[{Arrowheads[Large], Arrow[{{0, 0}, {1, 1}}]}]"}, //
      {"arrowheads-both", "Graphics[{Arrowheads[{-0.06, 0.06}], Arrow[{{0, 0}, {1, 1}}]}]"}, //
      {"bezier-curve", "Graphics[BezierCurve[{{0, 0}, {1, 2}, {2, 0}, {3, 2}}]]"}, //
      {"bspline-curve", "Graphics[BSplineCurve[{{0, 0}, {1, 2}, {2, 0}, {3, 2}}]]"}, //
      {"bspline-closed", "Graphics[BSplineCurve[{{0, 0}, {1, 2}, {2, 0}}, SplineClosed -> True]]"}, //
      {"joined-curve", "Graphics[JoinedCurve[{Line[{{0, 0}, {1, 1}}], Line[{{1, 1}, {2, 0}}]}]]"}, //
      {"filled-curve", "Graphics[FilledCurve[{Line[{{0, 0}, {1, 1}, {2, 0}, {0, 0}}]}]]"}, //
      {"half-plane", "Graphics[HalfPlane[{{0, 0}, {1, 1}}, {1, -1}]]"}, //
      {"infinite-line", "Graphics[InfiniteLine[{{0, 0}, {1, 1}}]]"}, //
      {"infinite-plane", "Graphics[InfinitePlane[{{0, 0}, {1, 0}, {0, 1}}]]"}, //
      {"graphics-complex", //
          "Graphics[GraphicsComplex[{{0, 0}, {2, 0}, {2, 2}, {0, 2}}, Table[Circle[i], {i, 4}]]]"}, //
      {"graphics-complex-line", //
          "Graphics[GraphicsComplex[Table[15 {Cos[t], Sin[t]}, {t, 0, 4 Pi, 4 Pi/5}], "
              + "{Green, Line[{1, 2, 3, 4, 5, 6}], Red, Point[{1, 2, 3, 4, 5}]}]]"}, //
      {"graphics-group", "Graphics[GraphicsGroup[{Red, Disk[{0, 0}, 1]}]]"}, //
      {"raster-gray", "Graphics[Raster[{{0, 0.25}, {0.5, 1}}]]"}, //
      {"raster-rgb", "Graphics[Raster[{{{1, 0, 0}, {0, 1, 0}}, {{0, 0, 1}, {1, 1, 0}}}]]"}, //
      {"inset-graphics", "Graphics[Inset[Graphics[Disk[]], {0, 0}]]"}, //
      {"inset-text", "Graphics[Inset[\"label\", {0, 0}]]"}, //
      {"tooltip", "Graphics[Tooltip[Disk[{0, 0}, 1], \"a disk\"]]"}, //
      {"tooltip-self", "Graphics[Tooltip[Disk[{0, 0}, 1]]]"}, //
      {"tooltip-point", //
          "ListPlot[{{1, 1}, Tooltip[{2, 4}, \"four\"], {3, 9}}]"}, //
      {"tooltip-curve", "Plot[Tooltip[Sin[x], \"sine\"], {x, 0, 6}]"}, //
      {"tooltip-bar", "BarChart[{1, Tooltip[2, \"two\"], 3}]"}, //
  };

  /** Style directives. */
  public static final String[][] DIRECTIVES = { //
      {"color-named", "Graphics[{Red, Disk[{0, 0}, 1], Blue, Disk[{2, 0}, 1]}]"}, //
      {"color-light-named", "Graphics[{LightBlue, Disk[{0, 0}, 1], LightGreen, Disk[{2, 0}, 1]}]"}, //
      {"color-orange-pink", "Graphics[{Orange, Disk[{0, 0}, 1], Pink, Disk[{2, 0}, 1]}]"}, //
      {"color-rgb", "Graphics[{RGBColor[1, 0, 0], Disk[]}]"}, //
      {"color-rgb-alpha", "Graphics[{RGBColor[1, 0, 0, 0.5], Disk[]}]"}, //
      {"color-rgb-list", "Graphics[{RGBColor[{1, 0, 0}], Disk[]}]"}, //
      {"color-hue", "Graphics[{Hue[0.3], Disk[]}]"}, //
      {"color-hue-4arg", "Graphics[{Hue[0.3, 1, 1, 0.5], Disk[]}]"}, //
      {"color-graylevel", "Graphics[{GrayLevel[0.5], Disk[]}]"}, //
      {"color-graylevel-alpha", "Graphics[{GrayLevel[0.5, 0.5], Disk[]}]"}, //
      {"color-cmyk", "Graphics[{CMYKColor[0, 1, 1, 0], Disk[]}]"}, //
      {"color-lighter", "Graphics[{Lighter[Red], Disk[]}]"}, //
      {"color-darker", "Graphics[{Darker[Red, 0.5], Disk[]}]"}, //
      {"color-blend", "Graphics[{Blend[{Red, Blue}, 0.5], Disk[]}]"}, //
      {"color-transparent", "Graphics[{Transparent, EdgeForm[Black], Disk[]}]"}, //
      {"opacity", "Graphics[{Opacity[0.5], Red, Disk[]}]"}, //
      {"opacity-2arg", "Graphics[{Opacity[0.5, Red], Disk[]}]"}, //
      {"thickness", "Graphics[{Thickness[0.02], Line[{{0, 0}, {1, 1}}]}]"}, //
      {"absolute-thickness", //
          "Graphics[Table[{AbsoluteThickness[t], Line[{{20 t, 10}, {20 t, 80}}]}, {t, 1, 10}]]"}, //
      {"thick-thin", //
          "Graphics[{Thick, Line[{{0, 0}, {1, 1}}], Thin, Line[{{0, 1}, {1, 0}}]}]"}, //
      {"dashing", "Graphics[{Dashing[{0.05, 0.02}], Line[{{0, 0}, {1, 1}}]}]"}, //
      {"dashed-dotted", //
          "Graphics[{Dashed, Line[{{0, 0}, {1, 1}}], Dotted, Line[{{0, 1}, {1, 0}}]}]"}, //
      {"dot-dashed", "Graphics[{DotDashed, Line[{{0, 0}, {1, 1}}]}]"}, //
      {"absolute-dashing", "Graphics[{AbsoluteDashing[{5, 3}], Line[{{0, 0}, {1, 1}}]}]"}, //
      {"point-size", "Graphics[{PointSize[Large], Point[{{0, 0}, {1, 1}}]}]"}, //
      {"absolute-point-size", "Graphics[{AbsolutePointSize[10], Point[{{0, 0}, {1, 1}}]}]"}, //
      {"edge-form", "Graphics[{EdgeForm[Thick], Red, Rectangle[]}]"}, //
      {"edge-form-list", "Graphics[{EdgeForm[{GrayLevel[0, 0.5]}], Hue[0.5], Disk[]}]"}, //
      {"edge-form-none", "Graphics[{EdgeForm[None], Red, Rectangle[]}]"}, //
      {"face-form", "Graphics[{FaceForm[Yellow], EdgeForm[Black], Rectangle[]}]"}, //
      {"face-form-none", "Graphics[{FaceForm[None], EdgeForm[Black], Rectangle[]}]"}, //
      {"cap-form", "Graphics[{CapForm[\"Round\"], Thickness[0.05], Line[{{0, 0}, {1, 1}}]}]"}, //
      {"join-form", //
          "Graphics[{JoinForm[\"Round\"], Thickness[0.05], Line[{{0, 0}, {1, 1}, {2, 0}}]}]"}, //
      {"directive", "Graphics[{Directive[Red, Thick, Dashed], Line[{{0, 0}, {1, 1}}]}]"}, //
      {"style-wrapper", "Graphics[Style[Disk[], Red]]"}, //
      {"style-nested-scope", //
          "Graphics[{Red, {Blue, Disk[{0, 0}, 1]}, Disk[{3, 0}, 1]}]"}, //
      {"font-directives", //
          "Graphics[Text[Style[\"txt\", Bold, Italic, FontSize -> 20], {0, 0}]]"}, //
      {"font-rules", //
          "Graphics[Text[Style[\"txt\", FontFamily -> \"Courier\", FontWeight -> Bold, "
              + "FontSlant -> Italic, FontColor -> Red], {0, 0}]]"}, //
      {"hue-table", //
          "Graphics[Table[{Hue[h, s], Disk[{12 h, 8 s}]}, {h, 0, 1, 1/6}, {s, 0, 1, 1/4}]]"}, //
      {"edgeform-hue-table", //
          "Graphics[Table[{EdgeForm[{GrayLevel[0, 0.5]}], Hue[(-11 + q + 10 r)/72, 1, 1, 0.6], "
              + "Disk[(8 - r) {Cos[2 Pi q/12], Sin[2 Pi q/12]}, (8 - r)/3]}, {r, 6}, {q, 12}]]"}, //
  };

  /** Coordinate transformations. */
  public static final String[][] TRANSFORMS = { //
      {"rotate-default", "Graphics[Rotate[Rectangle[{0, 0}, {2, 1}], Pi/4]]"}, //
      {"rotate-about", "Graphics[Rotate[Rectangle[{0, 0}, {2, 1}], Pi/4, {0, 0}]]"}, //
      {"translate-single", "Graphics[Translate[Disk[{0, 0}, 1], {2, 2}]]"}, //
      {"translate-multi", "Graphics[Translate[Disk[{0, 0}, 1], {{0, 0}, {3, 0}, {6, 0}}]]"}, //
      {"scale-uniform", "Graphics[Scale[Rectangle[{0, 0}, {1, 1}], 2]]"}, //
      {"scale-xy", "Graphics[Scale[Rectangle[{0, 0}, {1, 1}], {2, 0.5}]]"}, //
      {"geometric-transformation", //
          "Graphics[GeometricTransformation[Rectangle[{0, 0}, {1, 1}], {{{0, 1}, {1, 0}}, {0, 0}}]]"}, //
      {"rotation-transform", //
          "Graphics[GeometricTransformation[Rectangle[], RotationTransform[Pi/6]]]"}, //
  };

  /** Graphics options. */
  public static final String[][] OPTIONS = { //
      {"axes-true", "Graphics[Disk[], Axes -> True]"}, //
      {"axes-xy", "Graphics[Disk[], Axes -> {True, False}]"}, //
      {"axes-origin", "Graphics[Disk[], Axes -> True, AxesOrigin -> {0, 0}]"}, //
      {"axes-label", "Graphics[Disk[], Axes -> True, AxesLabel -> {\"x\", \"y\"}]"}, //
      {"axes-style", "Graphics[Disk[], Axes -> True, AxesStyle -> Red]"}, //
      {"frame-true", "Graphics[Disk[], Frame -> True]"}, //
      {"frame-label", "Graphics[Disk[], Frame -> True, FrameLabel -> {\"x\", \"y\"}]"}, //
      {"frame-style", "Graphics[Disk[], Frame -> True, FrameStyle -> Blue]"}, //
      {"frame-ticks-none", "Graphics[Disk[], Frame -> True, FrameTicks -> None]"}, //
      {"frame-per-edge", "Graphics[Disk[], Frame -> {{True, False}, {True, False}}]"}, //
      {"grid-lines-auto", "Graphics[Disk[], GridLines -> Automatic]"}, //
      {"grid-lines-explicit", "Graphics[Disk[], GridLines -> {{-1, 0, 1}, {-1, 0, 1}}]"}, //
      {"grid-lines-style", //
          "Graphics[Disk[], GridLines -> Automatic, GridLinesStyle -> Directive[Red, Dashed]]"}, //
      {"image-size-number", "Graphics[Disk[], ImageSize -> 200]"}, //
      {"image-size-pair", "Graphics[Disk[], ImageSize -> {300, 150}]"}, //
      {"image-size-named", "Graphics[Disk[], ImageSize -> Small]"}, //
      {"plot-range-pair", "Graphics[Disk[], PlotRange -> {{-2, 2}, {-2, 2}}]"}, //
      {"plot-range-all", "Graphics[Disk[], PlotRange -> All]"}, //
      {"plot-range-number", "Graphics[Disk[], PlotRange -> 3]"}, //
      {"plot-range-clipping", //
          "Graphics[Disk[{0, 0}, 3], PlotRange -> {{-1, 1}, {-1, 1}}, PlotRangeClipping -> True]"}, //
      {"plot-range-padding", "Graphics[Disk[], PlotRangePadding -> 0.5]"}, //
      {"plot-range-padding-scaled", "Graphics[Disk[], PlotRangePadding -> Scaled[0.2]]"}, //
      {"plot-range-padding-none", "Graphics[Disk[], PlotRangePadding -> None]"}, //
      {"plot-range-padding-automatic", "Graphics[Disk[], PlotRangePadding -> Automatic]"}, //
      {"plot-range-padding-per-side",
          "Graphics[Disk[], PlotRangePadding -> {{0.5, 0.1}, {0.2, 0.3}}]"}, //
      {"plot-range-padding-mixed", "Graphics[Disk[], PlotRangePadding -> {Scaled[0.1], 0.5}]"}, //
      {"image-padding", "Graphics[Disk[], Frame -> True, ImagePadding -> 40]"}, //
      {"aspect-ratio-number", "Graphics[Rectangle[{0, 0}, {2, 1}], AspectRatio -> 1]"}, //
      {"aspect-ratio-auto", "Graphics[Rectangle[{0, 0}, {2, 1}], AspectRatio -> Automatic]"}, //
      {"background", "Graphics[Disk[], Background -> LightYellow]"}, //
      {"plot-label", "Graphics[Disk[], PlotLabel -> \"a disk\"]"}, //
      {"ticks-explicit", //
          "Graphics[Disk[], Axes -> True, Ticks -> {{{-1, \"lo\"}, {1, \"hi\"}}, Automatic}]"}, //
      {"ticks-none", "Graphics[Disk[], Axes -> True, Ticks -> None]"}, //
      {"prolog", "Graphics[Disk[], Prolog -> {Red, Rectangle[{-2, -2}, {2, 2}]}]"}, //
      {"epilog", "Graphics[Disk[], Epilog -> {Blue, Line[{{-1, -1}, {1, 1}}]}]"}, //
      {"base-style", "Graphics[Disk[], BaseStyle -> Red]"}, //
      {"inert-options", "Graphics[Disk[], ContentSelectable -> True, Antialiasing -> True]"}, //
      {"graphics-row", "GraphicsRow[{Graphics[Disk[]], Graphics[Rectangle[]]}]"}, //
      {"graphics-column", "GraphicsColumn[{Graphics[Disk[]], Graphics[Rectangle[]]}]"}, //
      {"graphics-grid", //
          "GraphicsGrid[{{Graphics[Disk[]], Graphics[Rectangle[]]}, "
              + "{Graphics[Circle[]], Graphics[Line[{{0, 0}, {1, 1}}]]}}]"}, //
      {"graphics-grid-frame-all", //
          "GraphicsGrid[{{Graphics[Disk[]], Graphics[Rectangle[]]}, "
              + "{Graphics[Circle[]], Graphics[Disk[]]}}, Frame -> All]"}, //
      {"graphics-grid-dividers", //
          "GraphicsGrid[{{Graphics[Disk[]], Graphics[Rectangle[]]}, "
              + "{Graphics[Circle[]], Graphics[Disk[]]}}, Dividers -> {{2 -> Red}, Center}]"}, //
      {"graphics-grid-spacings", //
          "GraphicsGrid[{{Graphics[Disk[]], Graphics[Rectangle[]]}}, Spacings -> 20]"}, //
      {"graphics-grid-background", //
          "GraphicsGrid[{{Graphics[Disk[]], Graphics[Rectangle[]]}, "
              + "{Graphics[Circle[]], Graphics[Disk[]]}}, "
              + "Background -> {None, {{LightGray, White}}}]"}, //
      {"graphics-grid-span", //
          "GraphicsGrid[{{Graphics[Disk[]], SpanFromLeft}, "
              + "{Graphics[Circle[]], Graphics[Rectangle[]]}}, Frame -> All]"}, //
      {"graphics-grid-item", //
          "GraphicsGrid[{{Item[Graphics[Disk[]], Background -> LightYellow, Frame -> True], "
              + "Graphics[Rectangle[]]}}]"}, //
      {"graphics-grid-text-cell", //
          "GraphicsGrid[{{Graphics[Disk[]], Graphics[Rectangle[]]}, "
              + "{Style[\"a disk\", Bold], \"a square\"}}]"}, //
      {"graphics-grid-3d-cell", //
          "GraphicsRow[{Graphics[Disk[]], Graphics3D[Sphere[]]}]"}, //
      {"graphics-grid-aspect", //
          "GraphicsGrid[{{Graphics[Rectangle[{0,0},{4,1}]], Graphics[Disk[]]}}, "
              + "ItemAspectRatio -> 1, ImageMargins -> 8]"}, //
      {"graphics-row-spacing", //
          "GraphicsRow[{Graphics[Disk[]], Graphics[Rectangle[]]}, 30]"}, //
      {"graphics-column-align", //
          "GraphicsColumn[{Graphics[Rectangle[{0,0},{4,1}]], Graphics[Disk[]]}, Left, 10]"}, //
      {"overlay-two", //
          "Overlay[{Plot[Sin[x], {x, 0, 6}], Plot[Cos[x], {x, 0, 6}]}]"}, //
      {"overlay-sizes", //
          "Overlay[{Graphics[Disk[], ImageSize -> 200], "
              + "Graphics[{Red, Rectangle[]}, ImageSize -> 80]}]"}, //
      {"overlay-indices", //
          "Overlay[{Graphics[{Red, Disk[]}], Graphics[{Green, Rectangle[]}], "
              + "Graphics[{Blue, Circle[]}]}, {2, 3, 1}]"}, //
      {"overlay-alignment", //
          "Overlay[{Graphics[Disk[], ImageSize -> 300], "
              + "Graphics[Rectangle[], ImageSize -> 120]}, Alignment -> {Left, Top}]"}, //
      {"overlay-imagesize-all", //
          "Overlay[{Graphics[Disk[], ImageSize -> 100], "
              + "Graphics[Rectangle[], ImageSize -> 300]}, {1}, None, ImageSize -> All]"}, //
      {"tooltip-whole-picture", "Tooltip[Graphics[Disk[]], \"a disk\"]"}, //
      {"tooltip-wrapped-layout", //
          "Tooltip[GraphicsRow[{Graphics[Disk[]], Graphics[Rectangle[]]}], \"row\"]"}, //
      {"axes-label-automatic", "Plot[Sin[x], {x, 0, 6}, AxesLabel -> Automatic]"}, //
      {"overlay-background", //
          "Overlay[{Graphics[Disk[]], Graphics[Rectangle[], "
              + "Background -> Directive[{Opacity[0.5], Orange}]]}]"}, //
  };

  /**
   * Inputs that historically produced a blank image or an exception. Every one of these must render
   * something well formed rather than vanish.
   */
  public static final String[][] REGRESSIONS = { //
      {"reg-text-1arg", "Graphics[Text[\"only\"]]"}, //
      {"reg-edgeform-0arg", "Graphics[{EdgeForm[], Rectangle[]}]"}, //
      {"reg-faceform-0arg", "Graphics[{FaceForm[], Rectangle[]}]"}, //
      {"reg-empty-graphics", "Graphics[{}]"}, //
      {"reg-empty-line", "Graphics[Line[{}]]"}, //
      {"reg-empty-point", "Graphics[Point[{}]]"}, //
      {"reg-empty-polygon", "Graphics[Polygon[{}]]"}, //
      {"reg-single-point-range", "Graphics[Point[{1, 1}]]"}, //
      {"reg-symbolic-coord", "Graphics[Line[{{0, 0}, {a, b}}]]"}, //
      {"reg-infinite-coord", "Graphics[Line[{{0, 0}, {1, Infinity}}]]"}, //
      {"reg-nan-coord", "Graphics[Line[{{0, 0}, {1, 0/0}}]]"}, //
      {"reg-rgbcolor-0arg", "Graphics[{RGBColor[], Rectangle[]}]"}, //
      {"reg-hue-0arg", "Graphics[{Hue[], Rectangle[]}]"}, //
      {"reg-circle-0radius", "Graphics[Circle[{0, 0}, 0]]"}, //
      {"reg-disk-negative-radius", "Graphics[Disk[{0, 0}, -1]]"}, //
      {"reg-text-amp", "Graphics[Text[\"a & b < c > d\", {0, 0}]]"}, //
      {"reg-text-quote", "Graphics[Text[\"say \\\"hi\\\"\", {0, 0}]]"}, //
      {"reg-row-nongraphics", "GraphicsRow[{Graphics[Disk[]], 42}]"}, //
      {"reg-grid-nongraphics", "GraphicsGrid[{{Graphics[Disk[]], Null}}]"}, //
      {"reg-grid-empty", "GraphicsGrid[{}]"}, //
      {"reg-grid-all-none", "GraphicsGrid[{{None, None}}]"}, //
      {"reg-grid-span-edge", "GraphicsGrid[{{SpanFromLeft, Graphics[Disk[]]}}]"}, //
      {"reg-grid-bad-dividers", "GraphicsGrid[{{Graphics[Disk[]]}}, Dividers -> foo]"}, //
      {"reg-grid-negative-spacing", //
          "GraphicsRow[{Graphics[Disk[]], Graphics[Rectangle[]]}, Spacings -> -5]"}, //
      {"reg-grid-ragged", //
          "GraphicsGrid[{{Graphics[Disk[]]}, {Graphics[Circle[]], Graphics[Rectangle[]]}}]"}, //
      {"reg-grid-text-markup", "GraphicsRow[{Graphics[Disk[]], \"a & b < c\"}]"}, //
      {"reg-plotrange-reversed", "Graphics[Disk[], PlotRange -> {{2, -2}, {2, -2}}]"}, //
      {"reg-imagesize-zero", "Graphics[Disk[], ImageSize -> 0]"}, //
      {"reg-aspectratio-zero", "Graphics[Disk[], AspectRatio -> 0]"}, //
      {"reg-deep-nesting", "Graphics[{{{{{Red, Disk[]}}}}}]"}, //
      {"reg-log-nonpositive", "ListLogPlot[{-1, 0, 1, 10, 100}]"}, //
      {"reg-overlay-empty", "Overlay[{}]"}, //
      {"reg-overlay-nongraphics", "Overlay[{Graphics[Disk[]], 42}]"}, //
      {"reg-overlay-out-of-range", "Overlay[{Graphics[Disk[]]}, {7}]"}, //
      {"reg-overlay-selectable", "Overlay[{Graphics[Disk[]], Graphics[Circle[]]}, All, 2]"}, //
      {"reg-overlay-in-row", //
          "GraphicsRow[{Overlay[{Graphics[Disk[]], Graphics[Circle[]]}], "
              + "Graphics[Rectangle[]]}]"}, //
      {"reg-tooltip-nongraphic", "Tooltip[1, \"a\"]"}, //
      {"reg-tooltip-in-grid", "GraphicsGrid[{{Tooltip[Graphics[Disk[]], \"cell\"]}}]"}, //
      {"reg-axeslabel-automatic-plain", "Graphics[Disk[], Axes -> True, AxesLabel -> Automatic]"}, //
      {"reg-overlay-nested", //
          "Overlay[{Overlay[{Graphics[Disk[]]}], Graphics[Circle[]]}]"}, //
  };

  /** Plot builtins, mirroring the md2html gallery. */
  public static final String[][] PLOTS = { //
      {"plot-sin", "Plot[Sin[x], {x, -Pi, Pi}]"}, //
      {"plot-tan-range", "Plot[Tan[x], {x, -Pi, Pi}, PlotRange -> {-10, 10}]"}, //
      {"plot-multi", "Plot[{Sin[x], Cos[x]}, {x, -Pi, Pi}]"}, //
      {"plot-nested-exp", "Plot[Sin[E^x], {x, -2, 6}, PlotRange -> {-3, 3}]"}, //
      {"plot-legends", "Plot[{Sin[x], Cos[x]}, {x, 0, 2 Pi}, PlotLegends -> {\"sin\", \"cos\"}]"}, //
      {"parametric-plot", "ParametricPlot[{Cos[u], Cos[2 u]}, {u, 0, 2 Pi}]"}, //
      {"polar-plot", "PolarPlot[1 + Cos[t], {t, 0, 2 Pi}]"}, //
      {"log-plot", "LogPlot[{x^x, Exp[x]}, {x, 1, 5}]"}, //
      {"log-log-plot", "LogLogPlot[{Log[x]^x, x^x}, {x, 0.1, 10}]"}, //
      {"log-linear-plot", "LogLinearPlot[{Erf[x], Erfc[x]}, {x, 0.01, 10}]"}, //
      {"list-plot", "ListPlot[Prime[Range[25]]]"}, //
      {"list-plot-joined", "ListPlot[Prime[Range[25]], Joined -> True]"}, //
      {"list-line-plot", "ListLinePlot[Table[Sin[n], {n, 20}]]"}, //
      {"list-polar-plot", "ListPolarPlot[Table[{n, Log[n]}, {n, 100}]]"}, //
      {"list-log-log-plot", "ListLogLogPlot[Range[20]^3]"}, //
      {"list-log-linear-plot", "ListLogLinearPlot[Table[{n, Log[n]}, {n, 1, 20}]]"}, //
      {"discrete-plot", "DiscretePlot[MoebiusMu[k], {k, 1, 50}]"}, //
      {"number-line-plot", "NumberLinePlot[Prime[Range[20]]]"}, //
      {"contour-plot", "ContourPlot[Sin[x] + Sin[y], {x, 0, 4 Pi}, {y, 0, 4 Pi}]"}, //
      {"list-contour-plot", //
          "ListContourPlot[Table[Sin[x]*Sin[y], {x, -2, 2, 0.2}, {y, -2, 2, 0.2}]]"}, //
      {"list-contour-plot-opts", //
          "ListContourPlot[Table[x*y, {x, 1, 5}, {y, 1, 5}], Contours -> 5]"}, //
      {"density-plot", "DensityPlot[Cos[x] Cos[y], {x, -6.5, 6.5}, {y, -6.5, 6.5}]"}, //
      {"list-density-plot", //
          "ListDensityPlot[Table[Sin[x]*Sin[y], {x, -2, 2, 0.4}, {y, -2, 2, 0.4}]]"}, //
      {"list-density-plot-scattered", //
          "ListDensityPlot[{{0, 0, 1}, {1, 0, 2}, {0, 1, 3}, {1, 1, 4}}]"}, //
      {"matrix-plot", "MatrixPlot[Table[Binomial[n, k], {n, 0, 25}, {k, 0, n}]]"}, //
      {"array-plot", "ArrayPlot[Table[Mod[i + j, 2], {i, 10}, {j, 10}]]"}, //
      {"bar-chart", "BarChart[{1, 4, 2, 5, 3}]"}, //
      {"pie-chart", "PieChart[{1, 4, 2, 5, 3}]"}, //
      {"histogram", "Histogram[Table[Mod[n^2, 17], {n, 100}]]"}, //
      {"box-whisker-chart", "BoxWhiskerChart[Table[Mod[n^2, 17], {n, 100}]]"}, //
      {"cf-curve-gradient", "Plot[Sin[x], {x, 0, 6.28}, ColorFunction -> \"Rainbow\"]"}, //
      {"cf-curve-named", //
          "Plot[Sin[x], {x, 0, 6.28}, ColorFunction -> (If[#2 > 0, Red, Blue]&), "
              + "ColorFunctionScaling -> False]"}, //
      {"cf-curve-directive", //
          "Plot[Sin[x], {x, 0, 6}, ColorFunction -> (Directive[Opacity[0.4], Red]&)]"}, //
      {"cf-parametric", //
          "ParametricPlot[{Cos[t], Sin[t]}, {t, 0, 6.28}, PlotPoints -> 40, "
              + "ColorFunction -> (Hue[#3]&)]"}, //
      {"cf-polar", "PolarPlot[1 + Cos[t], {t, 0, 6.28}, ColorFunction -> (Hue[#4]&)]"}, //
      {"cf-histogram", "Histogram[{1,1,2,2,2,3,4,4,4,4,5}, ColorFunction -> \"Rainbow\"]"}, //
      {"cf-barchart", "BarChart[{3,1,4,1,5,9}, ColorFunction -> \"Rainbow\"]"}, //
      {"cf-piechart", "PieChart[{1,2,3,4}, ColorFunction -> \"Rainbow\"]"}, //
      {"cf-boxwhisker", //
          "BoxWhiskerChart[{{1,2,3,4,5},{10,11,12,13,14},{5,6,7,8,9}}, "
              + "ColorFunction -> \"Rainbow\"]"}, //
      {"cf-arrayplot-unscaled", //
          "ArrayPlot[{{0.25, 0.75, 3}}, ColorFunction -> (GrayLevel[#]&), "
              + "ColorFunctionScaling -> False]"}, //
      {"complex-plot", "ComplexPlot[(z^2 + 1)/(z^2 - 1), {z, -2 - 2 I, 2 + 2 I}]"}, //
      {"complex-plot-none", //
          "ComplexPlot[(z^2 + 1)/(z^2 - 1), {z, -2 - 2 I, 2 + 2 I}, ColorFunction -> \"None\"]"}, //
      {"complex-plot-global-abs", //
          "ComplexPlot[(z^2 + 1)/(z^2 - 1), {z, -2 - 2 I, 2 + 2 I}, ColorFunction -> \"GlobalAbs\"]"}, //
      {"complex-plot-max-abs", //
          "ComplexPlot[(z^2 + 1)/(z^2 - 1), {z, -2 - 2 I, 2 + 2 I}, ColorFunction -> \"MaxAbs\"]"}, //
      {"complex-plot-local-max-abs", //
          "ComplexPlot[(z^2 + 1)/(z^2 - 1), {z, -2 - 2 I, 2 + 2 I}, ColorFunction -> \"LocalMaxAbs\"]"}, //
      {"complex-plot-quantile-abs", //
          "ComplexPlot[(z^2 + 1)/(z^2 - 1), {z, -2 - 2 I, 2 + 2 I}, ColorFunction -> \"QuantileAbs\"]"}, //
      {"complex-plot-cyclic-log-abs", //
          "ComplexPlot[(z^2 + 1)/(z^2 - 1), {z, -2 - 2 I, 2 + 2 I}, ColorFunction -> \"CyclicLogAbs\"]"}, //
      {"complex-plot-cyclic-arg", //
          "ComplexPlot[(z^2 + 1)/(z^2 - 1), {z, -2 - 2 I, 2 + 2 I}, ColorFunction -> \"CyclicArg\"]"}, //
      {"complex-plot-cyclic-log-abs-arg", //
          "ComplexPlot[(z^2 + 1)/(z^2 - 1), {z, -2 - 2 I, 2 + 2 I}, ColorFunction -> \"CyclicLogAbsArg\"]"}, //
      {"complex-plot-cyclic-re-im-log-abs", //
          "ComplexPlot[(z^2 + 1)/(z^2 - 1), {z, -2 - 2 I, 2 + 2 I}, ColorFunction -> \"CyclicReImLogAbs\"]"}, //
      {"complex-plot-shifted-cyclic-log-abs", //
          "ComplexPlot[(z^2 + 1)/(z^2 - 1), {z, -2 - 2 I, 2 + 2 I}, ColorFunction -> \"ShiftedCyclicLogAbs\"]"}, //
      {"complex-plot-hue-shift", //
          "ComplexPlot[(z^2 + 1)/(z^2 - 1), {z, -2 - 2 I, 2 + 2 I}, ColorFunction -> {Hue[#8] &, None}]"}, //
      {"complex-plot-gradient-shaded", //
          "ComplexPlot[(z^2 + 1)/(z^2 - 1), {z, -2 - 2 I, 2 + 2 I}, ColorFunction -> {\"Rainbow\", \"CyclicLogAbsArg\"}]"}, //
  };

  /** Every group, concatenated. */
  public static String[][] all() {
    java.util.List<String[]> out = new java.util.ArrayList<>();
    for (String[][] group : new String[][][] {PRIMITIVES, DIRECTIVES, TRANSFORMS, OPTIONS,
        REGRESSIONS, PLOTS}) {
      java.util.Collections.addAll(out, group);
    }
    return out.toArray(new String[0][]);
  }
}
