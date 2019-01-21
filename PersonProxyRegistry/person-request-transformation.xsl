<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!-- Test manually with https://www.freeformatter.com/xsl-transformer.html -->

    <xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="address">
        <address><xsl:value-of select="firstline"/>, <xsl:value-of select="postcode"/></address>
    </xsl:template>

</xsl:stylesheet>
