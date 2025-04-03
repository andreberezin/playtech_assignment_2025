package com.playtech.report.transformer.impl;

import com.playtech.report.Report;
import com.playtech.report.column.Column;
import com.playtech.report.transformer.Transformer;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlIDREF;

import java.util.*;
import java.util.stream.Collectors;

import static com.playtech.ReportGenerator.ERROR;
import static com.playtech.ReportGenerator.RESET;

public class AggregatorTransformer implements Transformer {
    public static final String NAME = "Aggregator";
    private final Column groupByColumn;
    private final List<AggregateBy> aggregateColumns;

    public AggregatorTransformer(Column groupByColumn, List<AggregateBy> aggregateColumns) {
        this.groupByColumn = groupByColumn;
        this.aggregateColumns = aggregateColumns;
    }

    @Override
    public void transform(Report report, List<Map<String, Object>> rows) {

        // group rows by the groupByColumn value
        Map<Object, List<Map<String, Object>>> groupedRows = rows.stream()
                .filter(row -> row.get(groupByColumn.getName()) != null)
                .collect(Collectors.groupingBy(row -> row.get(groupByColumn.getName())));

        List<Map<String, Object>> aggregatedRows = new ArrayList<>();

        // process each group
        for (Map.Entry<Object, List<Map<String, Object>>> entry : groupedRows.entrySet()) {
            Object groupKey = entry.getKey();
            List<Map<String, Object>> groupRows = entry.getValue();

            Map<String, Object> aggregatedRow = new HashMap<>();
            aggregatedRow.put(groupByColumn.getName(), groupKey);

            // compute aggregates for each specified column
            for (AggregateBy aggregate : aggregateColumns) {
                Column input = aggregate.getInput();
                Column output = aggregate.getOutput();
                Method method = aggregate.getMethod();

                List<Number> values = groupRows.stream()
                        .map(row -> row.get(input.getName()))
                        .filter(Objects::nonNull)
                        .map(val -> {
                            if (val instanceof Number) {
                                return (Number) val;
                            } else if (val instanceof String strVal) {
                                try {
                                    return Double.parseDouble(strVal);
                                } catch (NumberFormatException e) {
                                    System.err.println(ERROR + "Invalid number format for column " + input.getName() + ": " + strVal + RESET);
                                    return null;
                                }
                            } else {
                                System.err.println(ERROR + "Unexpected type for column " + input.getName() + ": " + val.getClass().getName() + RESET);
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

//                System.out.println("Extracted numerical values: " + values);

                double aggregatedValue = 0;

                if (method == Method.SUM) {
                    aggregatedValue = values.stream().mapToDouble(Number::doubleValue).sum();
                } else if (method == Method.AVG) {
                    aggregatedValue = values.isEmpty() ? 0 : values.stream().mapToDouble(Number::doubleValue).average().orElse(0);
                }

                aggregatedRow.put(output.getName(), aggregatedValue);
            }

            aggregatedRows.add(aggregatedRow);
        }

        // clear original rows and replace with aggregated rows
        rows.clear();
        rows.addAll(aggregatedRows);
    }


    @XmlAccessorType(XmlAccessType.FIELD)
    public static class AggregateBy {
        @XmlIDREF
        private Column input;
        private Method method;
        @XmlIDREF
        private Column output;

        public Column getInput() {
            return input;
        }

        public Column getOutput() {
            return output;
        }

        public Method getMethod() {
            return method;
        }
    }

    public enum Method {
        SUM,
        AVG
    }

}
