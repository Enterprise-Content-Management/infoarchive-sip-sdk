package com.emc.ia.sdk.configuration;

/**
 * Class that is able to extract installable object from the Map/List/String representation that is fetched with
 * SnakeYaml.
 *
 * Example of the String representation is:
 *
 * tenant: INFOARCHIVE
 *
 * The String representation is available to the objects, that have only name (tenant in this case has only name)
 *
 * Example of the Map representation is:
 *
 * application:
 *   name: PhoneCalls
 *   type: ACTIVE_ARCHIVING
 *   archiveType: SIP
 *
 * This is the most common representation of the objects in configuration.
 *
 * Example of the List representation is:
 *
 * fileSystemFolders:
 *   - PhoneCalls-folder:
 *       subPath: "stores/file_store_01"
 *   - PhoneCalls-result-folder:
 *       subPath: "stores/result_store"
 *   - PhoneCalls-confirmation-folder:
 *       subPath: "stores/confirmation_store_01"
 *
 * Each of the List items is a different object with the same alias type.
 *
 */
public interface Extractor {
  /**
   * Extracts installable artifact from the configuration representation.
   * @param representation the String/Map/List representation of the installable artifact in configuration.
   * @return Configured installable artifact.
   */
  Installable extract(Object representation);

  /**
   * Gets the key under which the representation can be found in the root of the configuration
   * @return key of the representation.
   */
  String getFieldName();
}
