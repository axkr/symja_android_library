<?xml version="1.0"?>
<!--

Trivial stylesheet to convert XHTML to HTML - i.e. move all XHTML elements
into no namespace. It is safer to do all in one go here as the original tree
construction is complex enough as it is!

$Id: xhtml-to-html.xsl 516 2009-11-26 15:13:21Z davemckain $

-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:h="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="h">

  <xsl:template match="h:html">
    <html>
      <!-- Keep top-level MathML declaration, if made -->
      <xsl:copy-of select="namespace::*[string(.)='http://www.w3.org/1998/Math/MathML']"/>
      <!-- Keep non-XML attributes -->
      <xsl:copy-of select="@*[not(starts-with(name(),'xml:'))]"/>
      <!-- Descend -->
      <xsl:apply-templates/>
    </html>
  </xsl:template>

  <!-- Replace XHTML elements with corresponding variants in no namespace -->
  <xsl:template match="h:*">
    <xsl:element name="{local-name()}">
      <xsl:copy-of select="@*[not(starts-with(name(),'xml:'))]"/>
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>

  <!-- Usual identity transform for everything else -->
  <xsl:template match="*">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>

  <!-- Keep text, comments and any PIs (since they are required by MathPlayer) -->
  <xsl:template match="text()|comment()|processing-instruction()">
    <xsl:copy-of select="."/>
  </xsl:template>

</xsl:stylesheet>
