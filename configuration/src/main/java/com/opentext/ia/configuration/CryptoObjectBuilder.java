/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


/**
 * Build a crypto object.
 * @author Ray Sinnema
 * @since 9.6.0
 *
 * @param <C> The type of configuration to build
 */
public class CryptoObjectBuilder<C extends Configuration<?>>
    extends NamedObjectBuilder<ConfigurationBuilder<C>, CryptoObjectBuilder<C>, C> {

  protected CryptoObjectBuilder(ConfigurationBuilder<C> parent) {
    super(parent, "cryptoObject");
    setSecurityProvider("Bouncy Castle");
    setProperty("encryptionAlgorithm", "AES");
    setKeySize(256);
    setEncryptionMode("CBC");
    setPaddingScheme("PKCS5PADDING");
  }

  private void setSecurityProvider(String securityProvider) {
    setProperty("securityProvider", securityProvider);
  }

  /**
   * Set the security provider.
   * @param securityProvider The security provider to set
   * @return This builder
   */
  public CryptoObjectBuilder<C> providedBy(String securityProvider) {
    setSecurityProvider(securityProvider);
    return this;
  }

  private void setKeySize(int keySize) {
    setProperty("keySize", Integer.toString(keySize));
  }

  /**
   * Set the key size.
   * @param keySize The key size to set
   * @return This builder
   */
  public CryptoObjectBuilder<C> withKeysOfSize(int keySize) {
    setKeySize(keySize);
    return this;
  }

  private void setEncryptionMode(String encryptionMode) {
    setProperty("encryptionMode", encryptionMode);
  }

  /**
   * Set the encryption mode.
   * @param encryptionMode The encryption mode to set
   * @return This builder
   */
  public CryptoObjectBuilder<C> combiningBlocksUsing(String encryptionMode) {
    setEncryptionMode(encryptionMode);
    return this;
  }

  private void setPaddingScheme(String paddingScheme) {
    setProperty("paddingScheme", paddingScheme);
  }

  /**
   * Set the padding scheme.
   * @param paddingScheme The padding scheme to set
   * @return This builder
   */
  public CryptoObjectBuilder<C> paddedBy(String paddingScheme) {
    setPaddingScheme(paddingScheme);
    return this;
  }

}
