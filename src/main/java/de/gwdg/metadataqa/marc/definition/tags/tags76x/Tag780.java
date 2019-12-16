package de.gwdg.metadataqa.marc.definition.tags.tags76x;

import de.gwdg.metadataqa.marc.definition.Cardinality;
import de.gwdg.metadataqa.marc.definition.DataFieldDefinition;
import de.gwdg.metadataqa.marc.definition.Indicator;
import de.gwdg.metadataqa.marc.definition.general.Tag76xSubfield7PositionsGenerator;
import de.gwdg.metadataqa.marc.definition.general.codelist.RelatorCodes;
import de.gwdg.metadataqa.marc.definition.general.parser.LinkageParser;
import de.gwdg.metadataqa.marc.definition.general.parser.RecordControlNumberParser;
import de.gwdg.metadataqa.marc.definition.general.validator.ISBNValidator;
import de.gwdg.metadataqa.marc.definition.general.validator.ISSNValidator;
import static de.gwdg.metadataqa.marc.definition.FRBRFunction.*;

/**
 * Preceding Entry
 * http://www.loc.gov/marc/bibliographic/bd780.html
 */
public class Tag780 extends DataFieldDefinition {

  private static Tag780 uniqueInstance;

  private Tag780() {
    initialize();
    postCreation();
  }

  public static Tag780 getInstance() {
    if (uniqueInstance == null)
      uniqueInstance = new Tag780();
    return uniqueInstance;
  }

  private void initialize() {

    tag = "780";
    label = "Preceding Entry";
    bibframeTag = "PrecededBy";
    cardinality = Cardinality.Repeatable;
    descriptionUrl = "https://www.loc.gov/marc/bibliographic/bd780.html";

    ind1 = new Indicator("Note controller")
      .setCodes(
        "0", "Display note",
        "1", "Do not display note"
      )
      .setMqTag("noteController")
      .setFrbrFunctions(ManagementProcess, ManagementDisplay);

    ind2 = new Indicator("Type of relationship")
      .setCodes(
        "0", "Continues",
        "1", "Continues in part",
        "2", "Supersedes",
        "3", "Supersedes in part",
        "4", "Formed by the union of ... and ...",
        "5", "Absorbed",
        "6", "Absorbed in part",
        "7", "Separated from"
      )
      .setMqTag("typeOfRelationship")
      .setFrbrFunctions(ManagementDisplay);

    setSubfieldsWithCardinality(
      "a", "Main entry heading", "NR",
      "b", "Edition", "NR",
      "c", "Qualifying information", "NR",
      "d", "Place, publisher, and date of publication", "NR",
      "g", "Related parts", "R",
      "h", "Physical description", "NR",
      "i", "Relationship information", "R",
      "k", "Series data for related item", "R",
      "m", "Material-specific details", "NR",
      "n", "Note", "R",
      "o", "Other item identifier", "R",
      "r", "Report number", "R",
      "s", "Uniform title", "NR",
      "t", "Title", "NR",
      "u", "Standard Technical Report Number", "NR",
      "w", "Record control number", "R",
      "x", "International Standard Serial Number", "NR",
      "y", "CODEN designation", "NR",
      "z", "International Standard Book Number", "R",
      "4", "Relationship", "R",
      "6", "Linkage", "NR",
      "7", "Control subfield", "NR",
      "8", "Field link and sequence number", "R"
    );

    getSubfield("4").setCodeList(RelatorCodes.getInstance());
    // TODO: this requires position parser!
    // see http://www.loc.gov/marc/bibliographic/bd76x78x.html
    getSubfield("7").setPositions(Tag76xSubfield7PositionsGenerator.getPositions());

    getSubfield("z").setValidator(ISBNValidator.getInstance());
    getSubfield("x").setValidator(ISSNValidator.getInstance());

    getSubfield("w").setContentParser(RecordControlNumberParser.getInstance());
    getSubfield("6").setContentParser(LinkageParser.getInstance());

    getSubfield("a")
      .setBibframeTag("rdfs:label").setMqTag("rdf:value")
      .setLevels("A", "A");

    getSubfield("b")
      .setBibframeTag("editionStatement")
      .setFrbrFunctions(DiscoveryIdentify, DiscoverySelect, DiscoveryObtain)
      .setLevels("A", "A");

    getSubfield("c")
      .setBibframeTag("qualifier")
      .setLevels("A");

    getSubfield("d")
      .setBibframeTag("provisionActivityStatement")
      .setFrbrFunctions(DiscoveryIdentify, DiscoverySelect, DiscoveryObtain)
      .setLevels("O");

    getSubfield("g")
      .setBibframeTag("part")
      .setLevels("A");

    getSubfield("h")
      .setBibframeTag("extent")
      .setFrbrFunctions(DiscoveryIdentify, DiscoverySelect, DiscoveryObtain)
      .setLevels("O");

    getSubfield("i")
      .setBibframeTag("relation")
      .setFrbrFunctions(DiscoveryIdentify)
      .setLevels("O");

    getSubfield("k")
      .setBibframeTag("seriesStatement")
      .setFrbrFunctions(DiscoveryIdentify, DiscoverySelect, DiscoveryObtain)
      .setLevels("O");

    getSubfield("m")
      .setBibframeTag("note").setMqTag("materialSpecificDetails")
      .setLevels("O");

    getSubfield("n")
      .setBibframeTag("note")
      .setLevels("O");

    getSubfield("o")
      .setMqTag("otherItemIdentifier")
      .setFrbrFunctions(DiscoveryIdentify, DiscoveryObtain)
      .setLevels("O");

    getSubfield("r")
      .setMqTag("reportNumber")
      .setFrbrFunctions(DiscoveryIdentify, DiscoveryObtain)
      .setLevels("A");

    getSubfield("s")
      .setBibframeTag("title").setMqTag("uniformTitle")
      .setFrbrFunctions(DiscoveryIdentify)
      .setLevels("A", "A");

    getSubfield("t")
      .setBibframeTag("title")
      .setFrbrFunctions(DiscoveryIdentify, DiscoveryObtain)
      .setLevels("A", "A");

    getSubfield("u")
      .setBibframeTag("strn")
      .setFrbrFunctions(DiscoveryIdentify, DiscoveryObtain)
      .setLevels("A");

    getSubfield("w")
      .setMqTag("recordControlNumber")
      .setFrbrFunctions(ManagementIdentify)
      .setLevels("A");

    getSubfield("x")
      .setBibframeTag("issn")
      .setFrbrFunctions(DiscoveryIdentify, DiscoveryObtain)
      .setLevels("A", "A");

    getSubfield("y")
      .setBibframeTag("coden")
      .setFrbrFunctions(DiscoveryIdentify, DiscoveryObtain)
      .setLevels("O");

    getSubfield("z")
      .setBibframeTag("isbn")
      .setFrbrFunctions(DiscoveryIdentify, DiscoveryObtain)
      .setLevels("A");

    getSubfield("4")
      .setMqTag("relationship")
      .setLevels("O");

    getSubfield("6")
      .setBibframeTag("linkage")
      .setFrbrFunctions(ManagementIdentify, ManagementProcess)
      .setLevels("A", "A");

    /** TODO
     *  7/00  .setFrbrFunctions(ManagementIdentify, ManagementProcess, ManagementSort)
     *  7/01  .setFrbrFunctions(ManagementIdentify, ManagementProcess, ManagementSort)
     *  7/02  .setFrbrFunctions(ManagementProcess)
     *  7/03  .setFrbrFunctions(ManagementProcess)
     */
    getSubfield("7")
      .setMqTag("controlSubfield")
      .setLevels("O");

    getSubfield("8")
      .setMqTag("fieldLink")
      .setFrbrFunctions(ManagementIdentify, ManagementProcess)
      .setLevels("O");
  }
}
