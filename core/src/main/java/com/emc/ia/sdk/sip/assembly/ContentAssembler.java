/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.assembly;

import java.io.IOException;
import java.util.Map;

import com.emc.ia.sdk.support.io.HashAssembler;
import com.emc.ia.sdk.support.io.NoHashAssembler;
import com.emc.ia.sdk.support.io.ZipAssembler;

/**
 * Assemble the digital objects (content) into the sip.
 *
 * @param <D> The type of domain objects to assemble the SIP from
 */
public interface ContentAssembler<D> {

  /**
   * Start the assembly process.
   * @param zip Container to add the digital objects to.
   * @param metrics Metrics for keeping track of total number of digital objects and their size.
   */
  void begin(ZipAssembler zip, Counters metrics);

  /**
   * Extracts content from a domain object and adds it to the SIP.
   * @param domainObject The domain object for which to extract content and add it to the SIP.
   * @return The mapping between reference information and content info for all digital objects extracted from the
   *         domain object
   * @throws IOException If an exception occurs during the addition of the content
   */
  Map<String, ContentInfo> addContentsOf(D domainObject) throws IOException;

  /**
   * Do not deduplicate the digital objects but perform the specified hash calculations.
   * @param contentsExtraction Extraction of content from domain objects added to the SIP
   * @param contentHashAssembler Assembler that builds up an encoded hash for the extracted content
   * @param <D> The type of domain objects to assemble the SIP from 
   * @return The newly created content assembler
   */
  static <D> ContentAssembler<D> noDedup(DigitalObjectsExtraction<D> contentsExtraction,
      HashAssembler contentHashAssembler) {
    return new ContentAssemblerDefault<>(contentsExtraction, contentHashAssembler);
  }

  /**
   * Do not deduplicate the digital objects and do not perform any hash calculations.
   * @param contentsExtraction Extraction of content from domain objects added to the SIP
   * @param <D> The type of domain objects to assemble the SIP from 
   * @return The newly created content assembler
   */
  static <D> ContentAssembler<D> noDedup(DigitalObjectsExtraction<D> contentsExtraction) {
    return new ContentAssemblerDefault<>(contentsExtraction, new NoHashAssembler());
  }

  /**
   * Ignore all digital objects, the SIP will contain structured data only.
   * @param <D> The type of domain objects to assemble the SIP from 
   * @return The newly created content assembler
   */
  static <D> ContentAssembler<D> ignoreContent() {
    return new ContentAssemblerIgnore<>();
  }

  /**
   * Deduplicate digital objects based on the reference information but do not perform any hash calculation.
   * @param contentsExtraction Extraction of content from domain objects added to the SIP
   * @param <D> The type of domain objects to assemble the SIP from 
   * @return The newly created content assembler
   */
  static <D> ContentAssembler<D> withDedupOnRi(DigitalObjectsExtraction<D> contentsExtraction) {
    return new ContentAssemblerWithDedupOnRi<>(contentsExtraction, new NoHashAssembler(), false, false, 64000);
  }

  /**
   * Deduplicate digital objects based on the reference information and perform the specified hash calculations.
   * @param contentsExtraction Extraction of content from domain objects added to the SIP
   * @param contentHashAssembler Assembler that builds up an encoded hash for the extracted content
   * @param <D> The type of domain objects to assemble the SIP from 
   * @return The newly created content assembler
   */
  static <D> ContentAssembler<D> withDedupOnRi(DigitalObjectsExtraction<D> contentsExtraction,
      HashAssembler contentHashAssembler) {
    return new ContentAssemblerWithDedupOnRi<>(contentsExtraction, contentHashAssembler, false, false, 64000);
  }

  /**
   * Deduplicate digital objects based on the reference information and perform the specified hash calculations.
   * @param contentsExtraction Extraction of content from domain objects added to the SIP
   * @param contentHashAssembler Assembler that builds up an encoded hash for the extracted content
   * @param estimatedMaxDigitalObjects a hint which will initialize the internal buffers to handle the specified number
   *          of digital objects without reallocation
   * @param <D> The type of domain objects to assemble the SIP from 
   * @return The newly created content assembler
   */
  static <D> ContentAssembler<D> withDedupOnRi(DigitalObjectsExtraction<D> contentsExtraction,
      HashAssembler contentHashAssembler, int estimatedMaxDigitalObjects) {
    return new ContentAssemblerWithDedupOnRi<>(contentsExtraction, contentHashAssembler, false, false,
        estimatedMaxDigitalObjects);
  }

  /**
   * Deduplicate digital objects based on the reference information and perform the specified hash calculations and
   * optionally validate that the same reference information is always used to refer to the same content.
   * <p>
   * <b>Note</b> that setting errorWhenEqualRiAndNotEqualHash to true will force a read of the content when the same
   * reference information is used twice.
   * </p>
   * <p>
   * <b>Note</b> that setting either validations to true requires at least one hash.
   * </p>
   * @param contentsExtraction Extraction of content from domain objects added to the SIP
   * @param contentHashAssembler Assembler that builds up an encoded hash for the extracted content
   * @param errorWhenEqualRiAndNotEqualHash Throw an exception when the same reference information is used but the
   *          actual content is different.
   * @param errorWhenEqualHashAndNotEqualRI Throw an exception when the same content is included twice but using
   *          different reference informations.
   * @param <D> The type of domain objects to assemble the SIP from 
   * @return The newly created content assembler
   */
  static <D> ContentAssembler<D> withDedupOnRiAndValidation(DigitalObjectsExtraction<D> contentsExtraction,
      HashAssembler contentHashAssembler, boolean errorWhenEqualRiAndNotEqualHash,
      boolean errorWhenEqualHashAndNotEqualRI) {
    return new ContentAssemblerWithDedupOnRi<>(contentsExtraction, contentHashAssembler,
        errorWhenEqualRiAndNotEqualHash, errorWhenEqualHashAndNotEqualRI, 64000);
  }

  /**
   * Deduplicate digital objects based on the reference information and perform the specified hash calculations and
   * optionally validate that the same reference information is always used to refer to the same content.
   * <p>
   * <b>Note</b> that setting errorWhenEqualRiAndNotEqualHash to true will force a read of the content when the same
   * reference information is used twice.
   * </p>
   * <p>
   * <b>Note</b> that setting either validations to true requires at least one hash.
   * </p>
   * information is used twice.
   * @param contentsExtraction Extraction of content from domain objects added to the SIP
   * @param contentHashAssembler Assembler that builds up an encoded hash for the extracted content
   * @param errorWhenEqualRiAndNotEqualHash Throw an exception when the same reference information is used but the
   *          actual content is different.
   * @param errorWhenEqualHashAndNotEqualRI Throw an exception when the same content is included twice but using
   *          different reference informations.
   * @param estimatedMaxDigitalObjects a hint which will initialize the internal buffers to handle the specified number
   *          of digital objects without reallocation
   * @param <D> The type of domain objects to assemble the SIP from 
   * @return The newly created content assembler
   */
  static <D> ContentAssembler<D> withDedupOnRiAndValidation(DigitalObjectsExtraction<D> contentsExtraction,
      HashAssembler contentHashAssembler, boolean errorWhenEqualRiAndNotEqualHash,
      boolean errorWhenEqualHashAndNotEqualRI, int estimatedMaxDigitalObjects) {
    return new ContentAssemblerWithDedupOnRi<>(contentsExtraction, contentHashAssembler,
        errorWhenEqualRiAndNotEqualHash, errorWhenEqualHashAndNotEqualRI, estimatedMaxDigitalObjects);
  }

  /**
   * Deduplicate digital objects based on their hash value.
   * @param contentsExtraction Extraction of content from domain objects added to the SIP
   * @param contentHashAssembler Assembler that builds up an encoded hash for the extracted content
   * @param <D> The type of domain objects to assemble the SIP from 
   * @return The newly created content assembler
   */
  static <D> ContentAssembler<D> withDedupOnHash(DigitalObjectsExtraction<D> contentsExtraction,
      HashAssembler contentHashAssembler) {
    return new ContentAssemblerWithDedupOnHash<>(contentsExtraction, contentHashAssembler, 64000);
  }

  /**
   * Deduplicate digital objects based on their hash value.
   * @param contentsExtraction Extraction of content from domain objects added to the SIP
   * @param contentHashAssembler Assembler that builds up an encoded hash for the extracted content
   * @param estimatedMaxDigitalObjects a hint which will initialize the internal buffers to handle the specified number
   *          of digital objects without reallocation
   * @param <D> The type of domain objects to assemble the SIP from 
   * @return The newly created content assembler
   */
  static <D> ContentAssembler<D> withDedupOnHash(DigitalObjectsExtraction<D> contentsExtraction,
      HashAssembler contentHashAssembler, int estimatedMaxDigitalObjects) {
    return new ContentAssemblerWithDedupOnHash<>(contentsExtraction, contentHashAssembler, estimatedMaxDigitalObjects);
  }
}
