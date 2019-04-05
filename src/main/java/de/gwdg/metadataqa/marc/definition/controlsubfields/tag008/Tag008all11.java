package de.gwdg.metadataqa.marc.definition.controlsubfields.tag008;

import de.gwdg.metadataqa.marc.definition.ControlSubfieldDefinition;
import static de.gwdg.metadataqa.marc.definition.FRBRFunction.*;

import java.util.Arrays;

/**
 * Date 2
 * https://www.loc.gov/marc/bibliographic/bd008a.html
 */
public class Tag008all11 extends ControlSubfieldDefinition {
  private static Tag008all11 uniqueInstance;

  private Tag008all11() {
    initialize();
    extractValidCodes();
  }

  public static Tag008all11 getInstance() {
    if (uniqueInstance == null)
      uniqueInstance = new Tag008all11();
    return uniqueInstance;
  }

  private void initialize() {
    label = "Date 2";
    id = "tag008all11";
    mqTag = "date2";
    positionStart = 11;
    positionEnd = 15;
    descriptionUrl = "https://www.loc.gov/marc/bibliographic/bd008a.html";
    functions = Arrays.asList(DiscoveryIdentify, DiscoverySelect, DiscoveryObtain);
    // TODO: pattern?
  }
}