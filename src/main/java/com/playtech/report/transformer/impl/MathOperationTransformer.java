package com.playtech.report.transformer.impl;

import com.playtech.report.Report;
import com.playtech.report.column.Column;
import com.playtech.report.transformer.Transformer;

import java.util.List;
import java.util.Map;

public class MathOperationTransformer implements Transformer {
    public final static String NAME = "MathOperation";

    // TODO: Implement transformer logic
    public MathOperationTransformer(List<Column> inputs, MathOperation operation, Column output) {}

    @Override
    public void transform(Report report, List<Map<String, Object>> rows) {}

    public enum MathOperation {
        ADD,
        SUBTRACT,
    }
}
