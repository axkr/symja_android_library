<?xml version="1.0"?>
<!--

This is a trivial serializer that pulls in the module mapping MathML
symbols into named entities.

$Id: serialize-with-named-entities.xsl 516 2009-11-26 15:13:21Z davemckain $

-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:import href="mathml-entities-map.xsl"/>

  <!-- NB: Identity transform may be applied to a particular Node here -->
  <xsl:template match="node()">
    <xsl:copy-of select="."/>
  </xsl:template>

</xsl:stylesheet>
