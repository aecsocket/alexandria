package com.gitlab.aecsocket.minecommons.core.expressions.math;

import com.gitlab.aecsocket.minecommons.core.Validation;
import com.gitlab.aecsocket.minecommons.core.expressions.node.Visitor;

import java.lang.annotation.Target;

/**
 * Generic visitor for {@link MathNode}s.
 */
public interface MathVisitor extends Visitor<MathNode> {
    /**
     * A visitor which sets the value of {@link MathNode.Variable}s.
     */
    class Variable implements MathVisitor {
        private final String name;
        private final double value;

        public Variable(String name, double value) {
            Validation.notNull(name, "name");
            this.name = name;
            this.value = value;
        }

        public String name() { return name; }
        public double value() { return value; }

        @Override
        public void visit(MathNode node) {
            if (node instanceof MathNode.Variable varNode && varNode.name().equals(name)) {
                varNode.value(value);
            }
        }
    }
}
