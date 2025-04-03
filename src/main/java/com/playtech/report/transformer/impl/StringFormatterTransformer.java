package com.playtech.report.transformer.impl;

import com.playtech.report.Report;
import com.playtech.report.column.Column;
import com.playtech.report.transformer.Transformer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.playtech.ReportGenerator.ERROR;
import static com.playtech.ReportGenerator.RESET;

public class StringFormatterTransformer implements Transformer {
    public final static String NAME = "StringFormatter";

    private final List<Column> inputs;
    private final String format;
    private final Column output;

    // TODO: Implement transformer logic
    public StringFormatterTransformer(List<Column> inputs, String format, Column output) {
        this.inputs = inputs;
        this.format = format;
        this.output = output;
    }

    @Override
    public void transform(Report report, List<Map<String, Object>> rows) {
        for (Map<String, Object> row : rows) {
            // collect values from input columns
            List<Object> values = inputs.stream()
                    .map(input -> row.get(input.getName()))
                    .collect(Collectors.toList());

            try {
                String formattedValue = String.format(format, values.toArray());
                row.put(output.getName(), formattedValue);
            } catch (Exception e) {
                throw new IllegalArgumentException(ERROR + "Invalid format or input types" + RESET);
            }
        }
    }
}
