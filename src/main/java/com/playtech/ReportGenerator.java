package com.playtech;

import com.playtech.report.Report;
import com.playtech.util.xml.XmlParser;
import jakarta.xml.bind.JAXBException;

public class ReportGenerator {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Application should have 3 paths as arguments: csv file path, xml file path and output directory");
            System.exit(1);
        }
        String csvDataFilePath = args[0], reportXmlFilePath = args[1], outputDirectoryPath = args[2];
        try {
            Report report = XmlParser.parseReport(reportXmlFilePath);
        } catch (JAXBException e) {
            System.err.println("Parsing of the xml file failed:");
            throw new RuntimeException(e);
        }
        // TODO: Implement logic
    }
}
