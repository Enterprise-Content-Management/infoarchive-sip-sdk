/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import java.util.ArrayList;
import java.util.List;


public class HoldingCrypto extends NamedLinkContainer {

  private String application;
  private String holding;
  private List<PdiCryptoConfig> pdis;
  private String cryptoEncoding;
  private ObjectCryptoConfig sip;
  private ObjectCryptoConfig pdi;
  private ObjectCryptoConfig ci;

  public String getApplication() {
    return application;
  }

  public void setApplication(String application) {
    this.application = application;
  }

  public String getHolding() {
    return holding;
  }

  public void setHolding(String holding) {
    this.holding = holding;
  }

  public List<PdiCryptoConfig> getPdis() {
    return pdis;
  }

  public void setPdis(List<PdiCryptoConfig> pdis) {
    this.pdis = new ArrayList<>(pdis.size());
    this.pdis.addAll(pdis);
  }

  public String getCryptoEncoding() {
    return cryptoEncoding;
  }

  public void setCryptoEncoding(String cryptoEncoding) {
    this.cryptoEncoding = cryptoEncoding;
  }

  public ObjectCryptoConfig getSip() {
    return sip;
  }

  public void setSip(ObjectCryptoConfig sip) {
    this.sip = sip;
  }

  public ObjectCryptoConfig getPdi() {
    return pdi;
  }

  public void setPdi(ObjectCryptoConfig pdi) {
    this.pdi = pdi;
  }

  public ObjectCryptoConfig getCi() {
    return ci;
  }

  public void setCi(ObjectCryptoConfig ci) {
    this.ci = ci;
  }

  public static class PdiCryptoConfig {
    private String schema;
    private String pdiCrypto;

    public String getSchema() {
      return schema;
    }

    public void setSchema(String schema) {
      this.schema = schema;
    }

    public String getPdiCrypto() {
      return pdiCrypto;
    }

    public void setPdiCrypto(String pdiCrypto) {
      this.pdiCrypto = pdiCrypto;
    }
  }

  public static class ObjectCryptoConfig {
    private boolean cryptoEnabled;
    private String cryptoObject;

    public boolean isCryptoEnabled() {
      return cryptoEnabled;
    }

    public void setCryptoEnabled(boolean cryptoEnabled) {
      this.cryptoEnabled = cryptoEnabled;
    }

    public String getCryptoObject() {
      return cryptoObject;
    }

    public void setCryptoObject(String cryptoObject) {
      this.cryptoObject = cryptoObject;
    }
  }

}
