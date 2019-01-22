<?xml version="1.0"?>
<!--

This is a slightly experimental attempt at down-converting MathML islands
to plain old XHTML if it is deemed "safe". Islands which can't be
converted are left as-is.

$Id: mathml-to-xhtml.xsl 516 2009-11-26 15:13:21Z davemckain $

-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:m="http://www.w3.org/1998/Math/MathML"
  xmlns:s="http://www.ph.ed.ac.uk/snuggletex"
  exclude-result-prefixes="m s">

  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
  <xsl:variable name="properties-document" select="document('urn:snuggletex-css-properties')"/>
  <xsl:key name="property-key" match="s:property" use="@name"/>

  <xsl:template match="m:math[not(.//m:*[not(
      self::m:semantics or self::m:annotation
      or self::m:mi or self::m:mo or self::m:mn or self::m:mrow
      or self::m:msup or self::m:msub or self::m:mfenced
      or self::m:mspace or self::m:mtext
    )])]">
    <xsl:apply-templates select="." mode="to-html"/>
  </xsl:template>

  <xsl:template match="m:math[@display='block']" mode="to-html">
    <div>
      <xsl:call-template name="add-css-class">
        <xsl:with-param name="element-name" select="'div'"/>
        <xsl:with-param name="class-name" select="'mathml-math'"/>
      </xsl:call-template>
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="m:math" mode="to-html">
    <span>
      <xsl:call-template name="add-css-class">
        <xsl:with-param name="element-name" select="'span'"/>
        <xsl:with-param name="class-name" select="'mathml-math'"/>
      </xsl:call-template>
      <xsl:apply-templates/>
    </span>
  </xsl:template>

  <xsl:template match="m:semantics">
    <!-- Ignore annotations -->
    <xsl:apply-templates select="*[1]"/>
  </xsl:template>

  <xsl:template match="m:mi">
    <xsl:choose>
      <xsl:when test="string-length(.)=1">
        <!-- Single character identifier -->
        <span>
          <xsl:call-template name="add-css-class">
            <xsl:with-param name="element-name" select="'span'"/>
            <xsl:with-param name="class-name" select="'mi-single'"/>
          </xsl:call-template>
          <xsl:value-of select="."/>
        </span>
      </xsl:when>
      <xsl:otherwise>
        <!-- Multiple character identifier -->
        <span>
          <xsl:call-template name="add-css-class">
            <xsl:with-param name="element-name" select="'span'"/>
            <xsl:with-param name="class-name" select="'mi-multiple'"/>
          </xsl:call-template>
          <xsl:value-of select="."/>
        </span>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="m:mn">
    <span>
      <xsl:call-template name="add-css-class">
        <xsl:with-param name="element-name" select="'span'"/>
        <xsl:with-param name="class-name" select="'mn'"/>
      </xsl:call-template>
      <xsl:value-of select="."/>
    </span>
  </xsl:template>

  <xsl:template match="m:mo">
    <span>
      <xsl:call-template name="add-css-class">
        <xsl:with-param name="element-name" select="'span'"/>
        <xsl:with-param name="class-name" select="'mo'"/>
      </xsl:call-template>
      <xsl:value-of select="."/>
    </span>
  </xsl:template>

  <xsl:template match="m:mo[.='&#x2062;' or .='&#x2061;']">
    <!-- Strip out invisible times and apply function operators -->
  </xsl:template>

  <xsl:template match="m:mtext">
    <span>
      <xsl:call-template name="add-css-class">
        <xsl:with-param name="element-name" select="'span'"/>
        <xsl:with-param name="class-name" select="'mtext'"/>
      </xsl:call-template>
      <xsl:value-of select="."/>
    </span>
  </xsl:template>

  <xsl:template match="m:mrow">
    <span>
      <xsl:call-template name="add-css-class">
        <xsl:with-param name="element-name" select="'span'"/>
        <xsl:with-param name="class-name" select="'mrow'"/>
      </xsl:call-template>
      <xsl:apply-templates/>
    </span>
  </xsl:template>

  <xsl:template match="m:msup">
    <span>
      <xsl:call-template name="add-css-class">
        <xsl:with-param name="element-name" select="'span'"/>
        <xsl:with-param name="class-name" select="'msup'"/>
      </xsl:call-template>
      <xsl:apply-templates select="*[1]"/>
      <sup>
        <xsl:apply-templates select="*[2]"/>
      </sup>
    </span>
  </xsl:template>

  <xsl:template match="m:msub">
    <span>
      <xsl:call-template name="add-css-class">
        <xsl:with-param name="element-name" select="'span'"/>
        <xsl:with-param name="class-name" select="'msub'"/>
      </xsl:call-template>
      <xsl:apply-templates select="*[1]"/>
      <sub>
        <xsl:apply-templates select="*[2]"/>
      </sub>
    </span>
  </xsl:template>

  <xsl:template match="m:mfenced">
    <span>
      <xsl:call-template name="add-css-class">
        <xsl:with-param name="element-name" select="'span'"/>
        <xsl:with-param name="class-name" select="'mo'"/>
      </xsl:call-template>
      <xsl:value-of select="@open"/>
    </span>
    <xsl:for-each select="*">
      <xsl:apply-templates select="."/>
      <xsl:if test="position()!=last()">
        <span>
          <xsl:call-template name="add-css-class">
            <xsl:with-param name="element-name" select="'span'"/>
            <xsl:with-param name="class-name" select="'mo'"/>
          </xsl:call-template>
          <xsl:choose>
            <xsl:when test="@separator">
              <xsl:value-of select="@separator"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>,</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </span>
      </xsl:if>
    </xsl:for-each>
    <span>
      <xsl:call-template name="add-css-class">
        <xsl:with-param name="element-name" select="'span'"/>
        <xsl:with-param name="class-name" select="'mo'"/>
      </xsl:call-template>
      <xsl:value-of select="@close"/>
    </span>
  </xsl:template>

  <xsl:template match="m:mspace">
    <span class="mspace" style="width:{@width}">&#xa0;</span>
  </xsl:template>

  <!-- ************************************************************ -->

  <xsl:template name="add-css-class">
    <xsl:param name="element-name"/>
    <xsl:param name="class-name"/>
    <!-- Add usual CSS 'class' attribute -->
    <xsl:attribute name="class"><xsl:value-of select="$class-name"/></xsl:attribute>
    <!-- (Change context to the CSS Properties document) -->
    <xsl:for-each select="$properties-document">
      <!-- Now see if we can inline this style declaration as well, trying element.className then .className -->
      <xsl:variable name="key1" select="concat($element-name, '.', $class-name)"/>
      <xsl:variable name="value1" select="key('property-key', $key1)/@value"/>
      <xsl:choose>
        <xsl:when test="$value1!=''">
          <xsl:attribute name="style"><xsl:value-of select="$value1"/></xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:variable name="key2" select="concat('.', $class-name)"/>
          <xsl:variable name="value2" select="key('property-key', $key2)/@value"/>
          <xsl:if test="$value2!=''">
            <xsl:attribute name="style"><xsl:value-of select="$value2"/></xsl:attribute>
          </xsl:if>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
  </xsl:template>

  <!-- ************************************************************ -->

  <xsl:template match="m:math">
    <xsl:copy-of select="."/>
  </xsl:template>

  <xsl:template match="*">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
