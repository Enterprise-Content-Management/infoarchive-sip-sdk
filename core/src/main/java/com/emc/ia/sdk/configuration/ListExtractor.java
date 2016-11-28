package com.emc.ia.sdk.configuration;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This implementation of Extractor extracts ArtifactGroup from the List representation in configuration file.
 * It should be supplied with original extractor to extract individual entities and key for the List in the root of
 * the configuration
 */
public final class ListExtractor implements Extractor {

  private final ArtifactExtractor original;
  private final String fieldName;

  public ListExtractor(ArtifactExtractor original, String fieldName) {
    this.original = original;
    this.fieldName = fieldName;
  }

  /**
   * Extracts the list and delegates the extraction of each individual item to the original ArtifactExtractor.
   * @param representation the String/Map/List representation of the installable artifact in configuration.
   * @return ArifactGroup that contains all the artifacts from the list
   */
  @Override
  public ArtifactGroup extract(Object representation) {
    List listRepresentation = (List) representation;
    List<BaseIAArtifact> baseArtifacts = new ArrayList<BaseIAArtifact>();
    for (Object artifact : listRepresentation) {
      Map artifactRepresentation = transformToArtifact(artifact);
      baseArtifacts.add(original.extract(artifactRepresentation));
    }
    return new ArtifactGroup(baseArtifacts);
  }

  @Override
  public String getFieldName() {
    return fieldName;
  }

  private Map transformToArtifact(Object artifactEntity) {
    Set entrySet = ((Map) artifactEntity).entrySet();
    if (entrySet.size() == 1) {
      for (Object entryObject : entrySet) {
        Map.Entry entry = (Map.Entry) entryObject;
        String name = (String) entry.getKey();
        Map artifactRepresentation = (Map) entry.getValue();
        artifactRepresentation.put("name", name);
        return artifactRepresentation;
      }
    } else {
      throw new IllegalStateException();
    }
    throw new IllegalStateException();
  }
}
