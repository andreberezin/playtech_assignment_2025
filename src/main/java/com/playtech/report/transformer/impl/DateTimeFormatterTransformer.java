package com.playtech.report.transformer.impl;

import com.playtech.report.Report;
import com.playtech.report.column.Column;
import com.playtech.report.transformer.Transformer;

import java.util.List;
import java.util.Map;

public class DateTimeFormatterTransformer implements Transformer {
    public static final String NAME = "DateTimeFormatter";

    // TODO: Implement transformer logic
    public DateTimeFormatterTransformer(Column input, String format, Column output) {}

    @Override
    public void transform(Report report, List<Map<String, Object>> rows) {}
}
