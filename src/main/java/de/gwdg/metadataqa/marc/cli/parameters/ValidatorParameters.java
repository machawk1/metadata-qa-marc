package de.gwdg.metadataqa.marc.cli.parameters;

import de.gwdg.metadataqa.marc.model.validation.ValidationErrorFormat;
import org.apache.commons.cli.*;

public class ValidatorParameters extends CommonParameters {
	public static final String DEFAULT_FILE_NAME = "validation-report.txt";

	private String fileName = DEFAULT_FILE_NAME;
	private boolean doSummary;
	private ValidationErrorFormat format = ValidationErrorFormat.TEXT;

	private String[] args;
	private boolean useStandardOutput = false;
	private boolean isOptionSet;

	protected void setOptions() {
		if (!isOptionSet) {
			super.setOptions();
			options.addOption("s", "summary", false, "show summary instead of record level display");
			options.addOption("f", "fileName", true,
				String.format("the report file name (default is '%s')", ValidatorParameters.DEFAULT_FILE_NAME));
			options.addOption("r", "format", true, "specify a format");
			isOptionSet = true;
		}
	}

	public ValidatorParameters(String[] arguments) throws ParseException {
		super(arguments);

		if (cmd.hasOption("fileName"))
			fileName = cmd.getOptionValue("fileName");

		if (fileName.equals("stdout"))
			useStandardOutput = true;

		if (cmd.hasOption("format"))
			for (ValidationErrorFormat registeredFormat : ValidationErrorFormat.values()) {
				if (registeredFormat.getName().equals(cmd.getOptionValue("format"))) {
					format = registeredFormat;
					break;
				}
			}

		doSummary = cmd.hasOption("summary");
	}

	public String getFileName() {
		return fileName;
	}

	public boolean doSummary() {
		return doSummary;
	}

	public boolean useStandardOutput() {
		return useStandardOutput;
	}

	public ValidationErrorFormat getFormat() {
		return format;
	}
}
