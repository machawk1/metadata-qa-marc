package de.gwdg.metadataqa.marc.utils.pica;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import de.gwdg.metadataqa.api.util.FileUtils;
import de.gwdg.metadataqa.marc.MarcFactory;
import de.gwdg.metadataqa.marc.Utils;
import de.gwdg.metadataqa.marc.dao.MarcRecord;
import de.gwdg.metadataqa.marc.definition.Cardinality;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.marc4j.MarcReader;
import org.marc4j.marc.Record;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class PicaReaderTest {

  public static final Pattern SET = Pattern.compile("^SET: ");
  public static final Pattern EINGABE = Pattern.compile("^Eingabe: ");
  public static final Pattern WARNUNG = Pattern.compile("^Warnung: ");
  public static final Pattern PPN = Pattern.compile("PPN: ([^ ]+) ");

  @Test
  public void readTags() {
    CSVReader reader = null;
    try {
      Path tagsFile = FileUtils.getPath("pica/pica-tags-2013.csv");
      Reader fileReader = new FileReader(tagsFile.toString());
      CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
      reader = new CSVReaderBuilder(fileReader).withCSVParser(parser).build();
      List<String[]> myEntries = reader.readAll();
      Map<String, List<PicaTagDefinition>> map = new HashMap<>();
      assertEquals(431, myEntries.size());
      for (int i = 0; i < myEntries.size(); i++) {
        String[] line = myEntries.get(i);
        if (line.length != 5) {
          System.err.println(StringUtils.join(line, ", "));
        } else {
          PicaTagDefinition tag = new PicaTagDefinition(line);
          addTag(map, tag);
        }
      }

      Map<String, Integer> counter = new HashMap<>();
      Path recordsFile = FileUtils.getPath("pica/picaplus-sample.txt");
      try (BufferedReader br = new BufferedReader(new FileReader(recordsFile.toString()))) {
        String line;
        while ((line = br.readLine()) != null) {
          if (SET.matcher(line).find()) {
            // System.err.println("----");
          } else if (!line.equals("")
              && !EINGABE.matcher(line).find()
              && !WARNUNG.matcher(line).find()) {
            PicaLine pl = new PicaLine(line);
            if (map.containsKey(pl.getTag())) {
              // System.err.println(map.get(pl.getTag()).getDescription() + ": " + pl.formatSubfields());
            } else {
              Utils.count(pl.getTag(), counter);
              // System.err.printf("unknown %s: %s\n", pl.getTag(), pl.formatSubfields());
            }
          }
        }
      }

      List<String> known_problems = Arrays.asList(
        "201U/001", "202D/001", "101U", "102D", "209A/001"
      );
      counter.entrySet()
        .stream()
        .sorted((e1, e2) -> {
            return e2.getValue().compareTo(e1.getValue());
          }
        )
        .forEach(
          entry -> {
            if (!known_problems.contains(entry.getKey()))
              System.err.printf("%s: %d%n", entry.getKey(), entry.getValue());
          }
        );
    } catch(FileNotFoundException e){
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    } catch (CsvException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void readAvramSchema() {
    try {
      Map<String, PicaFieldDefinition> schemaDirectory = new HashMap<>();
      schemaDirectory.putAll(PicaSchemaReader.create(getPath("pica/pica-schema.json")));
      schemaDirectory.putAll(PicaSchemaReader.create(getPath("pica/pica-schema-extra.json")));

      Map<String, Integer> counter = new HashMap<>();
      Map<String, List<String>> ppns = new HashMap<>();
      Path recordsFile = FileUtils.getPath("pica/picaplus-sample.txt");
      try (BufferedReader br = new BufferedReader(new FileReader(recordsFile.toString()))) {
        String line;
        String ppn = null;
        while ((line = br.readLine()) != null) {
          if (SET.matcher(line).find()) {
            Matcher m = PPN.matcher(line);
            if (m.find()) {
              ppn = m.group(1);
            }
          } else if (!line.equals("")
            && !EINGABE.matcher(line).find()
            && !WARNUNG.matcher(line).find()) {
            PicaLine pl = new PicaLine(line);
            if (!directoryContains(schemaDirectory, pl)) {
              ppns.computeIfAbsent(pl.getQualifiedTag(), s -> new ArrayList<>());
              ppns.get(pl.getQualifiedTag()).add(ppn);
              Utils.count(pl.getQualifiedTag(), counter);
            }
          }
        }
      }
      List<String> known_problems = Arrays.asList(
        "036D", // only 036D/00
        "045H/01", // only 045H/00
        "091O/05",
        "101U", "102D",
        "201A/001", "201A/002", // only 201A
        "201U/001", "201U/002", "201U/003",
        "202D/001", "202D/002", "202D/003",
        "206X/001", "206X/002", // only 206X
        "209A/001", "209A/002", "209A/003", // only 209A $x0-9
        "209B/001", "209B/002", // only 209B/$x01
        "209C/001", // only 209C
        "209O/001", "209O/003", // only 209O
        "209R/001", // only 209R
        "220B/001", "220B/002", "220B/003", // only 220B
        "231@/001", "231@/002", // only 231@
        "231B/001", "231B/002", // only 231B
        "237A/001", "237A/002", // only 237A
        "245Z/001" // only 245Z $x00-99"
      );

      System.err.println("number of unhandled tags: " + counter.size());
      counter.entrySet()
        .stream()
        .sorted((e1, e2) -> {
            return e2.getValue().compareTo(e1.getValue());
          }
        )
        .forEach(
          entry -> {
            //if (!known_problems.contains(entry.getKey()))
              System.err.printf("%s: %d (%s)%n", entry.getKey(), entry.getValue(), ppns.get(entry.getKey()).get(0));
          }
        );

    } catch(FileNotFoundException e){
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void picaReader() throws IOException, URISyntaxException {
    Map<String, PicaFieldDefinition> schema = PicaSchemaReader.create(getPath("pica/k10plus.json"));
    String recordFile = FileUtils.getPath("pica/picaplus-sample.txt").toAbsolutePath().toString();
    MarcReader reader = new PicaReader(recordFile);
    int i = 0;
    MarcRecord marcRecord = null;
    while (reader.hasNext()) {
      Record record = reader.next();
      marcRecord = MarcFactory.createPicaFromMarc4j(record, schema);
      System.err.println(marcRecord.getId());
      i++;
    }
    System.err.printf("processed %d records%n", i);
    System.err.println(marcRecord.format());
  }

  private boolean directoryContains(Map<String, PicaFieldDefinition> schemaDirectory, PicaLine pl) {
    if (schemaDirectory.containsKey(pl.getTag())) {
      PicaFieldDefinition definitions = schemaDirectory.get(pl.getTag());
        // if (definition.getPicaplusTag().validateOccurrence(pl.getOccurrence()))
          return true;
    }
    return false;
  }

  @Test
  public void readASchema() {
    Map<String, PicaFieldDefinition> schema = PicaSchemaReader.create(getPath("pica/k10plus.json"));
    assertEquals(400, schema.size());
    PicaFieldDefinition definition = schema.get("048H");
    assertEquals("048H", definition.getTag());
    assertEquals("Systemvoraussetzungen für elektronische Ressourcen", definition.getLabel());
    assertEquals(Cardinality.Repeatable, definition.getCardinality());
    assertEquals(1, definition.getSubfields().size());
    assertEquals("a", definition.getSubfields().get(0).getCode());
    assertEquals("Systemvoraussetzungen für elektronische Ressourcen", definition.getSubfields().get(0).getLabel());
    assertEquals(Cardinality.Nonrepeatable, definition.getSubfields().get(0).getCardinality());
  }

  private void addTag(Map<String, List<PicaTagDefinition>> map, PicaTagDefinition definition) {
    String tag = definition.getPicaplusTag().getTag();
    if (!map.containsKey(tag)) {
      map.put(tag, new ArrayList<>());
    } else {
      System.err.println("Tag is already defined! " + definition.getPicaplusTag().getRaw() + " "
        + map.get(tag).stream().map(a -> a.getPicaplusTag().getRaw()).collect(Collectors.toSet()));
    }
    map.get(tag).add(definition);
  }

  private String getPath(String fileName) {
    return Paths.get("src/test/resources/" + fileName).toAbsolutePath().toString();
  }
}
