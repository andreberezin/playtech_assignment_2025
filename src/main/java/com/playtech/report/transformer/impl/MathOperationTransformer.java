package com.playtech.report.transformer.impl;

import com.playtech.report.Report;
import com.playtech.report.column.Column;
import com.playtech.report.transformer.Transformer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import static com.playtech.ReportGenerator.ERROR;
import static com.playtech.ReportGenerator.RESET;
import static java.lang.Math.round;

public class MathOperationTransformer implements Transformer {
    public final static String NAME = "MathOperation";
    private final List<Column> inputs;
    private final MathOperation operation;
    private final Column output;

    public MathOperationTransformer(List<Column> inputs, MathOperation operation, Column output) {
        this.inputs = inputs;
        this.operation = operation;
        this.output = output;
    }

    @Override
    public void transform(Report report, List<Map<String, Object>> rows) {

        for (Map<String, Object> row : rows) {

            double result = 0; // (operation == MathOperation.SUBTRACT) ? 0 : 0;
            boolean firstIteration = true;

            for (Column input : inputs) {
                Object inputValue = row.get(input.getName());

                if (!(inputValue instanceof Number)) {
                    throw new IllegalArgumentException(ERROR + "Column " + input.getName() + " is not a number." + RESET);
                }

                double value = ((Number) inputValue).doubleValue();

                if (operation == MathOperation.ADD) {
                    result += value;
                } else if (operation == MathOperation.SUBTRACT) {
                    result = firstIteration ? value : result - value;
                    firstIteration = false;
                }
            }

            row.put(output.getName(), result);
        }
    }

    public enum MathOperation {
        ADD,
        SUBTRACT,
    }
}
