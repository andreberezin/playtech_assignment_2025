package com.playtech.report.transformer.impl;

import com.playtech.report.Report;
import com.playtech.report.column.Column;
import com.playtech.report.transformer.Transformer;

import java.util.List;
import java.util.Map;

public class OrderingTransformer implements Transformer {
    public final static String NAME = "Ordering";

    // TODO: Implement transformer logic
    public OrderingTransformer(Column input, Order order) {}

    @Override
    public void transform(Report report, List<Map<String, Object>> rows) {}

    public enum Order {
        ASC,
        DESC
    }
}
