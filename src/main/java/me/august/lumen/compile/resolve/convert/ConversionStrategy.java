package me.august.lumen.compile.resolve.convert;

import me.august.lumen.compile.resolve.convert.types.Conversion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConversionStrategy {

    private List<Conversion> steps;

    public ConversionStrategy(Conversion... steps) {
        this.steps = new ArrayList<>(Arrays.asList(steps));
    }

    public ConversionStrategy(List<Conversion> steps) {
        this.steps = steps;
    }

    public ConversionStrategy addStep(Conversion conversion) {
        steps.add(conversion);

        return this;
    }

    public List<Conversion> getSteps() {
        return steps;
    }

    @Override
    public String toString() {
        return "ConversionStrategy{" +
            "steps=" + steps +
            '}';
    }
}
