<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" omit-xml-declaration="yes" indent="yes"/>


    <!-- Main Template -->
    <xsl:template match="/">
        <html>
            <head>
                <title>Failing Steps Report</title>
                <script type="text/javascript"
                        src="https://ajax.googleapis.com/ajax/libs/jquery/1.4.4/jquery.min.js"></script>
                <style type="text/css">

                    channel step {
                    font-size:12;
                    font-weight:bold;
                    }
                    channel storyName {
                    font-size:10;
                    font-weight:bold;
                    }
                    channel scenario {
                    font-size:10;
                    font-weight:normal;
                    }
                    channel cause {
                    font-size:10;
                    font-weight:normal;
                    font-color:red;
                    }
                    #failureCauseDiv {
                    background-color: #DEF0F0;
                    }
                    #stepNameDiv a:hover{
                        background-color:#F6AE42;
                    }
                </style>

                <script language="javascript">
                    function toggle(ele, text) {
                    <!--var ele = document.getElementById(showHideDiv);-->
                    <!--var text = document.getElementById(switchTextDiv);-->
                    if(ele.style.display == "block") {
                    ele.style.display = "none";
                    text.innerHTML = "+ " + text.text;
                    }
                    else {
                    ele.style.display = "block";
                    text.innerHTML = "- " + text.text }
                    }

                    function toggleDiv(divId) {
                    $("#"+divId).toggle();
                    }

                </script>
            </head>

            <body>
                <h1>Failing Steps Report</h1>

                <!-- Main table -->
                <table border="0" width="800" cellpadding="2" cellspacing="0" align="left">

                    <thead>
                        <tr bgcolor="#9acd32">
                            <th style="text-align:left">Story Name</th>

                            <th style="text-align:right">Failures</th>
                        </tr>
                    </thead>
                    <tbody>
                        <xsl:for-each select="/steps/step">
                            <xsl:sort select="count(./failures/failure)" order="descending" data-type="number"/>
                            <xsl:call-template name="displayStep">
                                <xsl:with-param name="position" select="position()"/>
                            </xsl:call-template>

                        </xsl:for-each>

                    </tbody>

                    <tr>
                        <td colspan="2">&#160;</td>
                    </tr>

                </table>
            </body>
        </html>
    </xsl:template>

    <xsl:template name="displayStep">
        <xsl:param name="position" select="0"/>
        <tr>
            <td width="750">

                <div id="stepNameDiv">
                    <a>
                        <xsl:attribute name="href">javascript:toggleDiv('failureCause<xsl:value-of select="$position"/>');
                        </xsl:attribute>
                        <xsl:attribute name="id">stepNameLink<xsl:value-of select="$position"/>
                        </xsl:attribute>
                        <xsl:value-of select="name"/>
                    </a>
                </div>
                <div id="failureCauseDiv">
                    <div style="display: none;">
                        <xsl:attribute name="id">failureCause<xsl:value-of select="$position"/>
                        </xsl:attribute>
                        <xsl:for-each select="failures/failure">
                            <xsl:call-template name="createFailureContent">
                                <xsl:with-param name="failure"/>
                                <xsl:with-param name="position" select="position()"/>
                            </xsl:call-template>
                        </xsl:for-each>
                    </div>
                </div>

            </td>
            <td width="50" style="text-align:right">
                <xsl:value-of select="count(failures/failure)"/>
            </td>

    </tr>
    </xsl:template>

    <xsl:template name="createFailureContent">
        <xsl:param name="failure"/>
        <xsl:param name="position"/>
        <div id="singleStoryFailure">
            <span style="font-weight:bold;"><xsl:value-of select="$position"/> - Story Name: </span>
            <xsl:value-of select="storyName"/>
            <br/>
            <span style="font-weight:bold;">Scenario: </span>
            <xsl:value-of select="scenario"/>
            <br/>
            <span style="font-weight:bold;">Cause: </span>
            <span style="font-color:#B60505"><xsl:value-of select="cause"/></span> 
            <hr/>
            <br/>
        </div>
    </xsl:template>

</xsl:stylesheet>


