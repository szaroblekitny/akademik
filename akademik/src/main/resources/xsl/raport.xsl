<?xml version="1.0" encoding="UTF-8"?>
<!--
  Raport kwaterunku jako strona HTML (krok 2.5 planu naprawczego).

  Wejście: XML w formacie /xsd/raport.xsd (krok 2.4).
  Wyjście: samodzielna strona HTML z tabelą pokoi; style są wpisane w stronę,
  więc plik działa także po otwarciu z dysku.

  XSLT 1.0, bo tę wersję obsługuje procesor wbudowany w JDK (XSLTC).
  Elementy raportu są w przestrzeni nazw, dlatego w ścieżkach XPath trzeba
  pisać przedrostek r: (tak samo jak w xs:unique w akademik.xsd).
  Tekst z danych (imiona, nazwiska) wstawia xsl:value-of, który sam zamienia
  znaki < > & na &lt; &gt; &amp;, więc dane nie mogą stać się kodem HTML.
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:r="urn:wojtekz:akademik:raport:1"
                exclude-result-prefixes="r">

  <!--
    method="xml", a nie "html": wbudowany serializer HTML zamienia część polskich
    liter na encje (ó → &oacute;, ale ś zostawia) i dopisuje własny <META>.
    W trybie xml litery zostają w UTF-8, a znaczniki puste mają postać <br/>,
    którą HTML5 też przyjmuje. Deklarację <?xml ...?> pomijamy, bo to strona HTML.
    Uwaga: pusty element zapisałby się jako <td/>, czego HTML nie rozumie,
    dlatego komórka bez mieszkańców dostaje napis "pusty".
  -->
  <xsl:output method="xml" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>

  <xsl:template match="/r:raport">
    <!-- XSLT 1.0 nie umie zapisać doctype HTML5, więc wstawiamy go jako gotowy tekst -->
    <xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;&#10;</xsl:text>
    <html lang="pl">
      <head>
        <meta charset="UTF-8"/>
        <title>Raport kwaterunku</title>
        <style>
          body { font-family: Lato, Arial, sans-serif; margin: 2em; color: #222; }
          table { border-collapse: collapse; margin: 1em 0; }
          th, td { border: 1px solid #999; padding: 0.3em 0.8em; text-align: left; vertical-align: top; }
          th { background: #e8eef6; }
          td.liczba { text-align: right; }
          .pusty { color: #888; }
          .podsumowanie { font-weight: bold; }
        </style>
      </head>
      <body>
        <h1>Raport kwaterunku</h1>
        <!-- 2026-11-10T20:15:00 → 2026-11-10 20:15:00 -->
        <p>Stan na <xsl:value-of select="translate(@utworzono, 'T', ' ')"/></p>

        <xsl:choose>
          <!-- bez pokoi pusta tabela zapisałaby się jako <tbody/>; zamiast niej zdanie -->
          <xsl:when test="not(r:pokoj)"><p>W bazie nie ma pokoi.</p></xsl:when>
          <xsl:otherwise>
        <table>
          <thead>
            <tr><th>Pokój</th><th>Miejsca</th><th>Wolne</th><th>Mieszkańcy</th></tr>
          </thead>
          <tbody>
            <xsl:for-each select="r:pokoj">
              <tr>
                <td><xsl:value-of select="@numer"/></td>
                <td class="liczba"><xsl:value-of select="@miejsca"/></td>
                <td class="liczba"><xsl:value-of select="@wolne"/></td>
                <td>
                  <xsl:choose>
                    <xsl:when test="r:student">
                      <xsl:for-each select="r:student">
                        <xsl:apply-templates select="."/>
                        <xsl:if test="position() != last()"><br/></xsl:if>
                      </xsl:for-each>
                    </xsl:when>
                    <xsl:otherwise><span class="pusty">pusty</span></xsl:otherwise>
                  </xsl:choose>
                </td>
              </tr>
            </xsl:for-each>
          </tbody>
        </table>
          </xsl:otherwise>
        </xsl:choose>

        <xsl:if test="r:niezakwaterowani">
          <h2>Bez pokoju</h2>
          <ul>
            <xsl:for-each select="r:niezakwaterowani/r:student">
              <li><xsl:apply-templates select="."/></li>
            </xsl:for-each>
          </ul>
        </xsl:if>

        <xsl:apply-templates select="r:podsumowanie"/>
      </body>
    </html>
  </xsl:template>

  <!-- student jako "Imię Nazwisko" -->
  <xsl:template match="r:student">
    <xsl:value-of select="r:imie"/>
    <xsl:text> </xsl:text>
    <xsl:value-of select="r:nazwisko"/>
  </xsl:template>

  <!-- jedno zdanie zamiast dawnego tekstowego meldunku -->
  <xsl:template match="r:podsumowanie">
    <p class="podsumowanie">
      <xsl:choose>
        <xsl:when test="@studentow = 0">W bazie nie ma studentów.</xsl:when>
        <xsl:when test="@zakwaterowanych = @studentow">
          <xsl:text>Wszyscy studenci są zakwaterowani (</xsl:text>
          <xsl:value-of select="@studentow"/>
          <xsl:text>).</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>Zakwaterowano </xsl:text>
          <xsl:value-of select="@zakwaterowanych"/>
          <xsl:text> z </xsl:text>
          <xsl:value-of select="@studentow"/>
          <xsl:text> studentów.</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </p>
    <p>
      <xsl:text>Pokoi: </xsl:text><xsl:value-of select="@pokoi"/>
      <xsl:text>, miejsc: </xsl:text><xsl:value-of select="@miejsc"/>
      <xsl:text>.</xsl:text>
    </p>
  </xsl:template>

</xsl:stylesheet>
