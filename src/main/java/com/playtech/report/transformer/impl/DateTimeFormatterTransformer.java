package com.playtech.report.transformer.impl;

import com.playtech.report.Report;
import com.playtech.report.column.Column;
import com.playtech.report.transformer.Transformer;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import static com.playtech.ReportGenerator.ERROR;
import static com.playtech.ReportGenerator.RESET;

public class DateTimeFormatterTransformer implements Transformer {
    public static final String NAME = "DateTimeFormatter";
    private final Column input;
    private final String format;
    private final Column output;
    private final DateTimeFormatter dateTimeFormatter;

    public DateTimeFormatterTransformer(Column input, String format, Column output) {
        this.input = input;
        this.format = format;
        this.output = output;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(format);
    }

    @Override
    public void transform(Report report, List<Map<String, Object>> rows) {
        for (Map<String, Object> row : rows) {
            Object value = row.get(input.getName());

            if (value instanceof String) {
                String dateString = (String) value;
                String formattedDate = formatDate(dateString);
                row.put(output.getName(), formattedDate);

            }
        }
    }

    private String formatDate(String dateString) {
        try {
            if (dateString.contains("T") && dateString.contains("Z")) {
                // Date-Time string (e.g., "2024-09-28T18:05:15Z")
                Instant instant = Instant.parse(dateString); // Parse the date-time string
                return instant.atZone(ZoneId.of("UTC")).format(dateTimeFormatter); // Format using UTC time zone
            } else {
                // Date-only string (e.g., "2024-09-28")
                DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE; // Date formatter
                LocalDate localDate = LocalDate.parse(dateString, dateFormatter); // Parse as LocalDate
                return localDate.format(dateTimeFormatter); // Format the LocalDate
            }
        } catch (DateTimeParseException e) {
            System.err.println(ERROR + "Error formatting date: " + dateString + RESET);
            return dateString; // Return the original string if formatting fails
        }
    }
}
