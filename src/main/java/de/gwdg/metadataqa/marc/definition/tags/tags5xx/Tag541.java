package de.gwdg.metadataqa.marc.definition.tags.tags5xx;

import de.gwdg.metadataqa.marc.definition.Cardinality;
import de.gwdg.metadataqa.marc.definition.DataFieldDefinition;
import de.gwdg.metadataqa.marc.definition.Indicator;
import de.gwdg.metadataqa.marc.definition.general.parser.LinkageParser;
import static de.gwdg.metadataqa.marc.definition.FRBRFunction.*;

/**
 * Immediate Source of Acquisition Note
 * http://www.loc.gov/marc/bibliographic/bd541.html
 */
public class Tag541 extends DataFieldDefinition {

  private static Tag541 uniqueInstance;

  private Tag541() {
    initialize();
    postCreation();
  }

  public static Tag541 getInstance() {
    if (uniqueInstance == null)
      uniqueInstance = new Tag541();
    return uniqueInstance;
  }

  private void initialize() {

    tag = "541";
    label = "Immediate Source of Acquisition Note";
    bibframeTag = "ImmediateAcquisition";
    cardinality = Cardinality.Repeatable;
    descriptionUrl = "https://www.loc.gov/marc/bibliographic/bd541.html";

    ind1 = new Indicator("Privacy")
      .setCodes(
        " ", "No information provided",
        "0", "Private",
        "1", "Not private"
      )
      .setMqTag("privacy");

    ind2 = new Indicator();

    setSubfieldsWithCardinality(
      "a", "Source of acquisition", "NR",
      "b", "Address", "NR",
      "c", "Method of acquisition", "NR",
      "d", "Date of acquisition", "NR",
      "e", "Accession number", "NR",
      "f", "Owner", "NR",
      "h", "Purchase price", "NR",
      "n", "Extent", "R",
      "o", "Type of unit", "R",
      "3", "Materials specified", "NR",
      "5", "Institution to which field applies", "NR",
      "6", "Linkage", "NR",
      "8", "Field link and sequence number", "R"
    );

    getSubfield("6").setContentParser(LinkageParser.getInstance());

    getSubfield("a")
      .setBibframeTag("rdfs:label").setMqTag("rdf:value")
      .setFrbrFunctions(UseManage)
      .setLevels("A");

    getSubfield("b")
      .setMqTag("address")
      .setFrbrFunctions(UseManage)
      .setLevels("A");

    getSubfield("c")
      .setMqTag("method")
      .setFrbrFunctions(UseManage)
      .setLevels("A");

    getSubfield("d")
      .setMqTag("date")
      .setFrbrFunctions(UseManage)
      .setLevels("A");

    getSubfield("e")
      .setMqTag("accessionNumber")
      .setFrbrFunctions(DiscoveryIdentify, UseManage)
      .setLevels("A");

    getSubfield("f")
      .setMqTag("owner")
      .setFrbrFunctions(UseManage)
      .setLevels("A");

    getSubfield("h")
      .setMqTag("price")
      .setFrbrFunctions(UseManage)
      .setLevels("O");

    getSubfield("n")
      .setMqTag("extent")
      .setFrbrFunctions(UseManage)
      .setLevels("A");

    getSubfield("o")
      .setMqTag("typeOfUnit")
      .setFrbrFunctions(UseManage)
      .setLevels("A");

    getSubfield("3")
      .setMqTag("materialsSpecified")
      .setFrbrFunctions(DiscoveryIdentify)
      .setLevels("O");

    getSubfield("5")
      .setMqTag("institutionToWhichFieldApplies")
      .setFrbrFunctions(ManagementProcess, ManagementDisplay)
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
