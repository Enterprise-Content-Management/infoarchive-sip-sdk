/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;


public class XForm extends NamedLinkContainer {
  private String form;
  private String searchName;
  private String compositionName;

  public XForm() {
    //Default form value is taken from a sample IA search form created. This is a valid and meaningful value 
    setForm("<xhtml:html xmlns:xhtml=\"http://www.w3.org/1999/xhtml\" xmlns:xforms=\"http://www.w3.org/2002/xforms\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n<xhtml:head>\n    <xforms:model>\n        <xforms:instance xmlns=\"\">\n            \n        <data>\n            <From/><To/><Date><from/><to/></Date></data></xforms:instance>\n        <xforms:instance xmlns=\"\" id=\"labels\">\n            \n        <labels><From>From</From><To>To</To><Date>Date</Date></labels></xforms:instance>\n        <xforms:instance xmlns=\"\" id=\"hints\">\n            \n        <hints><From/><To/><Date/></hints></xforms:instance>\n        <xforms:instance xmlns=\"\" id=\"prompts\">\n            \n        <prompts><From/><To/></prompts></xforms:instance>\n        <xforms:instance xmlns=\"\" id=\"alerts\">\n            \n        <alerts><From/><To/><Date/></alerts></xforms:instance>\n        <xforms:instance xmlns=\"\" id=\"range-messages\">\n            \n        <rangemessages><From/><To/></rangemessages></xforms:instance>\n        <xforms:instance xmlns=\"\" id=\"pattern-messages\">\n            \n        <patternmessages><From/><To/></patternmessages></xforms:instance>\n        <xforms:submission id=\"submit01\" method=\"post\" serialization=\"application/xml\"/>\n        <xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xforms=\"http://www.w3.org/2002/xforms\" targetNamespace=\"http://www.w3.org/2002/xforms\" elementFormDefault=\"qualified\">\n        </xsd:schema>\n    <bind xmlns=\"http://www.w3.org/2002/xforms\" ref=\"/data/From\"/><bind xmlns=\"http://www.w3.org/2002/xforms\" ref=\"/data/To\"/><bind xmlns=\"http://www.w3.org/2002/xforms\" ref=\"/data/Date/from\" type=\"xforms:date\" constraint=\"(string-length(.) = 0 or (days-from-date(.) &lt;= days-from-date(/data/Date/to) and string-length(/data/Date/to) &gt; 0))\" required=\"true()\"/><bind xmlns=\"http://www.w3.org/2002/xforms\" ref=\"/data/Date/to\" type=\"xforms:date\" constraint=\"(string-length(.) = 0 or (days-from-date(.) &gt;= days-from-date(/data/Date/from) and string-length(/data/Date/from) &gt; 0))\" required=\"true()\"/></xforms:model>\n</xhtml:head>\n<xhtml:body>\n<input xmlns=\"http://www.w3.org/2002/xforms\" ref=\"From\"><label ref=\"instance('labels')/From\"/><hint ref=\"instance('hints')/From\"/><hint appearance=\"minimal\" ref=\"instance('prompts')/From\"/><alert ref=\"instance('alerts')/From\"/><message class=\"range\" ref=\"instance('range-messages')/From\"/><message class=\"pattern\" ref=\"instance('pattern-messages')/From\"/></input><input xmlns=\"http://www.w3.org/2002/xforms\" ref=\"To\"><label ref=\"instance('labels')/To\"/><hint ref=\"instance('hints')/To\"/><hint appearance=\"minimal\" ref=\"instance('prompts')/To\"/><alert ref=\"instance('alerts')/To\"/><message class=\"range\" ref=\"instance('range-messages')/To\"/><message class=\"pattern\" ref=\"instance('pattern-messages')/To\"/></input><input xmlns=\"http://www.w3.org/2002/xforms\" ref=\"Date/from\"><label ref=\"instance('labels')/Date\"/><hint ref=\"instance('hints')/Date\"/><alert ref=\"instance('alerts')/Date\"/></input><input xmlns=\"http://www.w3.org/2002/xforms\" ref=\"Date/to\"/></xhtml:body>\n</xhtml:html>");
    setSearchName("Default emails search");
    setCompositionName("Encrypted First Name");
  }

  public String getForm() {
    return form;
  }

  public final void setForm(String form) {
    this.form = form;
  }

  public String getSearchName() {
    return searchName;
  }

  public final void setSearchName(String searchName) {
    this.searchName = searchName;
  }

  public String getCompositionName() {
    return compositionName;
  }

  public final void setCompositionName(String compositionName) {
    this.compositionName = compositionName;
  }

}
