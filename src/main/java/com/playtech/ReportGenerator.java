package com.playtech;

import com.playtech.report.Report;
import com.playtech.report.column.Column;
import com.playtech.report.transformer.Transformer;
import com.playtech.report.transformer.impl.*;
import com.playtech.util.xml.XmlParser;
import com.sun.net.httpserver.Authenticator;
import jakarta.xml.bind.JAXBException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ReportGenerator {
    public static final String ERROR = "\u001B[31m";
    public static final String SUCCESS = "\u001B[32m";
    public static final String RESET = "\u001B[0m";

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println(ERROR + "Application should have 3 paths as arguments: csv file path, xml file path and output directory" + ERROR);
            System.exit(1);
        }
        String csvDataFilePath = args[0], reportXmlFilePath = args[1], outputDirectoryPath = args[2];
        try {
            Report report = XmlParser.parseReport(reportXmlFilePath);

            readAndTransformCsv(csvDataFilePath, outputDirectoryPath, report);

        } catch (JAXBException e) {
            System.err.println(ERROR + "Parsing of the xml file failed:" + RESET);
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.err.println(ERROR + "Failed to read CSV file:" + RESET);
            throw new RuntimeException(e);
        }
    }

    // parse the CSV file, transform the data and output it line by line
    public static void readAndTransformCsv(String filePath, String outputFilePath, Report report) throws IOException {
        String reportName = report.getReportName();
        Report.FileFormat outputFormat = report.getOutputFormat();

        List<Transformer> transformers = report.getTransformers();
        final List<Column> outputColumns = report.getOutputs();
        List<Map<String, Object>> rows = new ArrayList<>();

        // Create a set of output column names for filtering
        Set<String> outputColumnNames = new HashSet<>();
        for (Column column : outputColumns) {
            outputColumnNames.add(column.getName());
        }


        // parse the csv
        int skippedLines;
        try (Scanner scanner = new Scanner(new File(filePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath + reportName + '.' + outputFormat))) {

            if (!scanner.hasNextLine()) {
                throw new IOException(ERROR + "Empty CSV file" + RESET);
            }

            String[] headers = scanner.nextLine().split(",");

            int count = 0;
            skippedLines = 0;

            while (scanner.hasNextLine()) {
                count++;
                String[] values = scanner.nextLine().split(",");

                if (values.length != headers.length) {
                    System.err.println(ERROR + "Skipping line " + count + ": Incorrect column count (" + values.length + " instead of " + headers.length + ")" + RESET);
                    skippedLines++;
                    continue;
                }

                Map<String, Object> row = new HashMap<>();

                // Iterate over the CSV headers and collect data
                for (int i = 0; i < headers.length; i++) {
                    row.put(headers[i], values[i]);
                }

                // Apply the transformers (e.g., DateTimeFormatterTransformer)
                for (Transformer transformer : transformers) {
                    if (transformer instanceof DateTimeFormatterTransformer dateTimeFormatterTransformer) {
                        dateTimeFormatterTransformer.transform(report, Collections.singletonList(row));
//                        System.out.println("Datetiming row!");
                    }

                    // store each row
                    rows.add(row);
                }
            }

            for (Transformer transformer : transformers) {
                // use the aggregator transformer
                if (transformer instanceof AggregatorTransformer aggregatorTransformer) {
                    aggregatorTransformer.transform(report, rows);  // Sort the rows
                    System.out.println("Aggregating rows!");
                }
                // use the math transformer
                if (transformer instanceof MathOperationTransformer mathOperationTransformer) {
                    mathOperationTransformer.transform(report, rows);
                    System.out.println("Mathing rows!");
                }
                // use the ordering transformer
                if (transformer instanceof OrderingTransformer orderingTransformer) {
                    orderingTransformer.transform(report, rows);  // Sort the rows
                    System.out.println("Sorting rows!");
                }
                // use the string formatter transformer
                if (transformer instanceof StringFormatterTransformer stringFormatterTransformer) {
                    stringFormatterTransformer.transform(report, rows);  // Sort the rows
                    System.out.println("Stringing rows!");
                }
            }

            // Write the transformed rows to the output
            for (Map<String, Object> row : rows) {
                String jsonRow = createJsonObject(row);
                writer.write(jsonRow);
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println(ERROR + "Error processing data: " + e.getMessage() + RESET);
            throw e;
        }

        System.out.println("\n\n" + SUCCESS + "Finished transforming data! " + RESET + "\n\n");

        System.out.println("Skipped lines: " + skippedLines);
    }

    // method to convert objects to json
    private static String createJsonObject(Map<String, Object> row) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            jsonBuilder.append("\"")
                    .append(entry.getKey())
                    .append("\":\"")
                    .append(entry.getValue())
                    .append("\",");
        }
        // Remove the last comma
        if (jsonBuilder.length() > 1) {
            jsonBuilder.setLength(jsonBuilder.length() - 1);
        }
        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }
}



