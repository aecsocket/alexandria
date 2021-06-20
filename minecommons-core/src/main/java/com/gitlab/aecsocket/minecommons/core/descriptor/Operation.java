package com.gitlab.aecsocket.minecommons.core.descriptor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public interface Operation {
    Operation SET = new BasicOperation("=", false, false) {
        @Override
        public double apply(double base, double mod, boolean percent) {
            return mod;
        }
    };
    Operation ADD = new BasicOperation("+", true, true) {
        @Override
        public double apply(double base, double mod, boolean percent) {
            return base + (percent ? (base * mod) : mod);
        }
    };
    Operation SUBTRACT = new BasicOperation("-", true, true) {
        @Override
        public double apply(double base, double mod, boolean percent) {
            return base - (percent ? (base * mod) : mod);
        }
    };
    Operation MULTIPLY = new BasicOperation("×", true, true, "*") {
        @Override
        public double apply(double base, double mod, boolean percent) {
            return base * (percent ? (base * mod) : mod);
        }
    };
    Operation DIVIDE = new BasicOperation("÷", true, true, "/") {
        @Override
        public double apply(double base, double mod, boolean percent) {
            return base / (percent ? (base * mod) : mod);
        }
    };

    Collection<Operation> OPERATIONS = Collections.unmodifiableCollection(Arrays.asList(
            SET, ADD, SUBTRACT, MULTIPLY, DIVIDE
    ));

    String symbol();
    boolean showSymbol();
    boolean supportsPercent();
    String[] aliases();

    double apply(double base, double mod, boolean percent);
}
