/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip.client.dto;


public class CryptoObject extends NamedLinkContainer {

  private String securityProvider;
  private int keySize;
  private boolean inUse;
  private String encryptionMode;
  private String paddingScheme;
  private String encryptionAlgorithm;

  public String getSecurityProvider() {
    return securityProvider;
  }

  public void setSecurityProvider(String securityProvider) {
    this.securityProvider = securityProvider;
  }

  public int getKeySize() {
    return keySize;
  }

  public void setKeySize(int keySize) {
    this.keySize = keySize;
  }

  public boolean isInUse() {
    return inUse;
  }

  public void setInUse(boolean inUse) {
    this.inUse = inUse;
  }

  public String getEncryptionMode() {
    return encryptionMode;
  }

  public void setEncryptionMode(String encryptionMode) {
    this.encryptionMode = encryptionMode;
  }

  public String getPaddingScheme() {
    return paddingScheme;
  }

  public void setPaddingScheme(String paddingScheme) {
    this.paddingScheme = paddingScheme;
  }

  public String getEncryptionAlgorithm() {
    return encryptionAlgorithm;
  }

  public void setEncryptionAlgorithm(String encryptionAlgorithm) {
    this.encryptionAlgorithm = encryptionAlgorithm;
  }

}
