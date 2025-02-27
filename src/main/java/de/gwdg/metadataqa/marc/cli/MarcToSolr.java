package de.gwdg.metadataqa.marc.cli;

import de.gwdg.metadataqa.marc.dao.MarcRecord;
import de.gwdg.metadataqa.marc.cli.parameters.CommonParameters;
import de.gwdg.metadataqa.marc.cli.parameters.MarcToSolrParameters;
import de.gwdg.metadataqa.marc.cli.processor.MarcFileProcessor;
import de.gwdg.metadataqa.marc.cli.utils.RecordIterator;
import de.gwdg.metadataqa.marc.datastore.MarcSolrClient;
import de.gwdg.metadataqa.marc.definition.MarcVersion;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.marc4j.marc.Record;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * usage:
 * java -cp target/metadata-qa-marc-0.1-SNAPSHOT-jar-with-dependencies.jar de.gwdg.metadataqa.marc.cli.SolrKeyGenerator http://localhost:8983/solr/tardit 0001.0000000.formatted.json
 *
 * @author Péter Király <peter.kiraly at gwdg.de>
 */
public class MarcToSolr implements MarcFileProcessor, Serializable {

  private static final Logger logger = Logger.getLogger(
    MarcToSolr.class.getCanonicalName()
  );
  private final Options options;
  private final MarcVersion version;
  private MarcToSolrParameters parameters;
  private MarcSolrClient client;
  private Path currentFile;
  private boolean readyToProcess;
  private DecimalFormat decimalFormat = new DecimalFormat();

  public MarcToSolr(String[] args) throws ParseException {
    parameters = new MarcToSolrParameters(args);
    options = parameters.getOptions();
    client = new MarcSolrClient(parameters.getSolrUrl());
    client.setTrimId(parameters.getTrimId());
    readyToProcess = true;
    version = parameters.getMarcVersion();
  }

  public static void main(String[] args) throws ParseException {
    MarcToSolr processor = new MarcToSolr(args);
    processor.options.toString();
    if (StringUtils.isBlank(
        ((MarcToSolrParameters) processor.getParameters()).getSolrUrl()
    )) {
      System.err.println("Please provide a Solr URL and file name!");
      System.exit(0);
    }

    RecordIterator iterator = new RecordIterator(processor);
    iterator.start();
  }

  @Override
  public CommonParameters getParameters() {
    return parameters;
  }

  @Override
  public void processRecord(Record marc4jRecord, int recordNumber) throws IOException {

  }

  @Override
  public void processRecord(MarcRecord marcRecord, int recordNumber) throws IOException {
    if (parameters.getIgnorableRecords().isIgnorable(marcRecord))
      return;

    try {
      Map<String, List<String>> map = marcRecord.getKeyValuePairs(
        parameters.getSolrFieldType(), true, parameters.getMarcVersion()
      );
      map.put("record_sni", Arrays.asList(marcRecord.asJson()));
      client.indexMap(marcRecord.getId(), map);
    } catch (SolrServerException e) {
      if (e.getMessage().contains("Server refused connection at")) {
        // end process;
        readyToProcess = false;
      }
      logger.log(Level.SEVERE, "processRecord", e);
    }
    if (recordNumber % 5000 == 0) {
      if (parameters.doCommit())
        client.commit();
      logger.info(
        String.format(
          "%s/%s (%s)",
          currentFile.getFileName().toString(),
          decimalFormat.format(recordNumber),
          marcRecord.getId()
        )
      );
    }
  }

  @Override
  public void beforeIteration() {
    logger.info(parameters.formatParameters());
  }

  @Override
  public void fileOpened(Path path) {
    currentFile = path;
  }

  @Override
  public void fileProcessed() {

  }

  @Override
  public void afterIteration(int numberOfprocessedRecords) {
    client.commit();
  }

  @Override
  public void printHelp(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    String message = String.format("java -cp metadata-qa-marc.jar %s [options] [file]", this.getClass().getCanonicalName());
    formatter.printHelp(message, options);
  }

  @Override
  public boolean readyToProcess() {
    return readyToProcess;
  }
}