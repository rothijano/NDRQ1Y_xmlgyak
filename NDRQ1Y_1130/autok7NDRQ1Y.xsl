<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" />
    <xsl:template match="/">
        <autok>
            <xsl:for-each select="//auto">
                <xsl:sort select="ar" data-type="number" order="ascending" />
                <auto>
                    <xsl:attribute name="rsz">
                        <xsl:value-of select="@rsz" />
                    </xsl:attribute>
                    <ar>
                        <xsl:value-of select="ar" />
                    </ar>
                </auto>
            </xsl:for-each>
        </autok>
    </xsl:template>
</xsl:stylesheet>
