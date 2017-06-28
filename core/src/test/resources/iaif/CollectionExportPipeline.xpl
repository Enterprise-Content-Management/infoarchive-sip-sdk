<p:declare-step version="1.0" xmlns:p="http://www.w3.org/ns/xproc" xmlns:ia="http://infoarchive.emc.com/xproc" xmlns:iaif="http://infoarchive.emc.com/iaif/xproc" name="main">

  <p:input port="source" sequence="true"/>
  <p:input port="collection"/>
  <p:input port="parameters" kind="parameter"/>
  <p:input port="stylesheet"/>

  <p:option name="sftpUser" required="false"/>
  <p:option name="sftpPassword" required="false"/>
  <p:option name="sftpHost" required="true"/>
  <p:option name="sftpPort" required="false"/>
  <p:option name="sftpPath" required="true"/>

  <ia:pdf name="manifest">
    <p:input port="source">
      <p:pipe step="main" port="collection"/>
    </p:input>
    <p:input port="stylesheet">
      <p:pipe step="main" port="stylesheet"/>
    </p:input>
  </ia:pdf>

  <iaif:email-search-results-pst name="pst">
    <p:input port="source">
      <p:pipe step="main" port="source"/>
    </p:input>
  </iaif:email-search-results-pst>

  <ia:zip name="zip">
    <p:input port="source">
      <p:pipe step="manifest" port="result"/>
      <p:pipe step="pst" port="result"/>
    </p:input>
  </ia:zip>

  <ia:sftp-upload>
    <p:input port="source">
      <p:pipe step="zip" port="result"/>
    </p:input>
    <p:with-option name="user" select="$sftpUser"/>
    <p:with-option name="password" select="$sftpPassword"/>
    <p:with-option name="host" select="$sftpHost"/>
    <p:with-option name="port" select="$sftpPort"/>
    <p:with-option name="path" select="$sftpPath"/>
  </ia:sftp-upload>

  <ia:store-export-result format="pdf">
    <p:input port="source">
      <p:pipe step="manifest" port="result"/>
    </p:input>
  </ia:store-export-result>

</p:declare-step>