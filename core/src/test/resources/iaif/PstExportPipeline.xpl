<p:declare-step version="1.0" xmlns:p="http://www.w3.org/ns/xproc" xmlns:ia="http://infoarchive.emc.com/xproc" xmlns:iaif="http://infoarchive.emc.com/iaif/xproc">
  <p:input port="source" sequence="true"/>
  <iaif:email-search-results-pst/>
  <ia:zip/>
  <ia:store-export-result format="pst"/>
</p:declare-step>