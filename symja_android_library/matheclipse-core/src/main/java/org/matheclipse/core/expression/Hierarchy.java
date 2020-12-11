package org.matheclipse.core.expression;

public enum Hierarchy {
  DOUBLEID(2), //

  DOUBLECOMPLEXID(4), //

  INTEGERID(8), //

  FRACTIONID(16), //

  COMPLEXID(32), //

  SERIESID(64), //

  QUANTITYID(128), //

  STRINGID(256), //

  SYMBOLID(512), //

  ASTID(1024), //

  PATTERNID(2048), //

  BLANKID(4096), //

  DATASETID(16384), //

  METHODSYMBOLID(8192), //

  DATAID(32786), //

  BYTEARRAYID(32786 + 1), //

  COMPILEFUNCTONID(32786 + 2), //

  GEOPOSITIONID(32786 + 3), //

  GRAPHEXPRID(32786 + 4); //

  private int hierarchy;

  private Hierarchy(int hierarchy) {
    this.hierarchy = hierarchy;
  }
}
