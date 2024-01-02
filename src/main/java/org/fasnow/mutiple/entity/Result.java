package org.fasnow.mutiple.entity;

public class Result{
    boolean passed;
    String featureField;

    public Result(boolean passed, String featureField) {
        this.passed = passed;
        this.featureField = featureField;
    }

    public boolean isPassed() {
        return passed;
    }

    public String getFeatureField() {
        return featureField;
    }
}