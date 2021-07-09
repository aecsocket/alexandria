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
     * @param name The variable name to set.
     * @param value The value to set variable nodes of the corresponding name to.
     */
    record Variable(String name, double value) implements MathVisitor {
        /**
         * Creates an instance.
         * @param name  The name of the variable.
         * @param value The value to set to.
         */
        public Variable {
            Validation.notNull("name", name);
        }

        @Override
        public void visit(MathNode node) {
            if (node instanceof MathNode.Variable varNode && varNode.name().equals(name)) {
                varNode.value(value);
            }
        }
    }
}
