package de.gwdg.metadataqa.marc.definition.tags.tags5xx;

import de.gwdg.metadataqa.marc.definition.Cardinality;
import de.gwdg.metadataqa.marc.definition.DataFieldDefinition;
import de.gwdg.metadataqa.marc.definition.Indicator;
import de.gwdg.metadataqa.marc.definition.general.parser.LinkageParser;
import de.gwdg.metadataqa.marc.definition.general.validator.ISBNValidator;
import static de.gwdg.metadataqa.marc.definition.FRBRFunction.*;

/**
 * Information About Documentation Note
 * http://www.loc.gov/marc/bibliographic/bd556.html
 */
public class Tag556 extends DataFieldDefinition {

  private static Tag556 uniqueInstance;

  private Tag556() {
    initialize();
    postCreation();
  }

  public static Tag556 getInstance() {
    if (uniqueInstance == null)
      uniqueInstance = new Tag556();
    return uniqueInstance;
  }

  private void initialize() {

    tag = "556";
    label = "Information About Documentation Note";
    mqTag = "Documentation";
    cardinality = Cardinality.Repeatable;
    descriptionUrl = "https://www.loc.gov/marc/bibliographic/bd556.html";

    ind1 = new Indicator("Display constant controller")
      .setCodes(
        " ", "Documentation",
        "8", "No display constant generated"
      )
      .setMqTag("displayConstant")
      .setFrbrFunctions(ManagementDisplay);

    ind2 = new Indicator();

    setSubfieldsWithCardinality(
      "a", "Information about documentation note", "NR",
      "z", "International Standard Book Number", "R",
      "6", "Linkage", "NR",
      "8", "Field link and sequence number", "R"
    );

    getSubfield("z").setValidator(ISBNValidator.getInstance());
    getSubfield("6").setContentParser(LinkageParser.getInstance());

    getSubfield("a")
      .setBibframeTag("rdfs:label").setMqTag("rdf:value")
      .setFrbrFunctions(DiscoveryIdentify, DiscoveryObtain)
      .setLevels("M");

    getSubfield("z")
      .setMqTag("isbn")
      .setFrbrFunctions(DiscoveryIdentify, DiscoveryObtain)
      .setLevels("A");

    getSubfield("6")
      .setBibframeTag("linkage")
      .setFrbrFunctions(ManagementIdentify, ManagementProcess)
      .setLevels("A");

    getSubfield("8")
      .setMqTag("fieldLink")
      .setFrbrFunctions(ManagementIdentify, ManagementProcess)
      .setLevels("O");

  }
}
