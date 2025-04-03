package com.playtech.report.transformer.impl;

import com.playtech.report.Report;
import com.playtech.report.column.Column;
import com.playtech.report.transformer.Transformer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static com.playtech.ReportGenerator.ERROR;
import static com.playtech.ReportGenerator.RESET;

public class OrderingTransformer implements Transformer {
    public final static String NAME = "Ordering";

    private final Column input;
    private final Order order;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;

    public OrderingTransformer(Column input, Order order) {
        this.input = input;
        this.order = order;
    }

    @Override
    public void transform(Report report, List<Map<String, Object>> rows) {

        Comparator<Map<String, Object>> comparator = getComparator();

        if (order == Order.ASC) {
            Collections.sort(rows, comparator);
        } else {
            Collections.sort(rows, comparator.reversed());
        }

    }

    private Comparator<Map<String, Object>> getComparator() {
        return (row1, row2) -> {
            Object value1 = row1.get(input.getName());
            Object value2 = row2.get(input.getName());

            if (value1 == null || value2 == null) {
                System.out.println("Value 0");
                return 0;
            }

            if (value1 instanceof String) {
                try {
                    LocalDate date1 = LocalDate.parse((String) value1, dateFormatter);
                    LocalDate date2 = LocalDate.parse((String) value2, dateFormatter);
                    return date1.compareTo(date2);
                } catch (DateTimeParseException e) {
                    return (((String) value1).compareTo((String) value2));
                }
            } else if (value1 instanceof LocalDate) {
                return ((LocalDate) value1).compareTo((LocalDate) value2);
            } else if (value1 instanceof LocalDateTime) {
                return ((LocalDateTime) value1).compareTo((LocalDateTime) value2);
            } else {
                throw new IllegalArgumentException(ERROR + "Unsupported column type for ordering" + RESET);
            }
        };
    }


    public enum Order {
        ASC,
        DESC
    }
}
