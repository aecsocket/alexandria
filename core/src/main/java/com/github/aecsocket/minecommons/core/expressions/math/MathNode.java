package com.github.aecsocket.minecommons.core.expressions.math;

import java.util.*;

import com.github.aecsocket.minecommons.core.Validation;
import com.github.aecsocket.minecommons.core.expressions.node.EvaluationException;
import com.github.aecsocket.minecommons.core.expressions.node.Node;
import com.github.aecsocket.minecommons.core.expressions.parsing.NodeException;
import com.google.common.collect.ImmutableMap;

/**
 * A math expression node, which can be evaluated.
 */
public interface MathNode extends Node<MathVisitor> {
    /**
     * Evaluates this node, and children nodes if any, to get the value it holds.
     * @return The value.
     * @throws EvaluationException If there was an error when evaluating.
     */
    double eval() throws EvaluationException;

    @Override
    default MathNode accept(MathVisitor visitor) {
        visitor.visit(this);
        return this;
    }

    /**
     * Sets a variable in this node tree, using the {@link MathVisitor.Variable}.
     * @param name The name of the variable.
     * @param value The value to set to.
     * @return This instance.
     */
    default MathNode set(String name, double value) {
        accept(new MathVisitor.Variable(name, value));
        return this;
    }

    /**
     * Creates a constant value node.
     * @param value The value of the constant.
     * @return The node.
     */
    static Constant constant(double value) {
        return new Constant(value);
    }

    /**
     * Creates a constant value node from text.
     * @param text The text to parse.
     * @return The node.
     * @throws NodeException If the text could not be parsed.
     */
    static Constant constant(String text) throws NodeException {
        return Constant.of(text);
    }

    /**
     * Creates a variable node.
     * @param name The name of the variable.
     * @return The node.
     */
    static Variable variable(String name){
        return Variable.of(name);
    }

    /**
     * Creates a sum node.
     * @return The node.
     */
    static Sum sum() {
        return new Sum();
    }

    /**
     * Creates a product node.
     * @return The node.
     */
    static Product product() {
        return new Product();
    }

    /**
     * Creates an exponent node.
     * @param base The base.
     * @param exponent The exponent.
     * @return The node.
     */
    static Exponent exponent(MathNode base, MathNode exponent) {
        return new Exponent(base, exponent);
    }

    /**
     * Creates a math function node.
     * @param function The function.
     * @param term The term applied to the function.
     * @return The node.
     */
    static MathFunction mathFunction(MathFunction.Function function, MathNode term) {
        return new MathFunction(function, term);
    }

    /**
     * Creates a math function node.
     * @param name The name of the function.
     * @param term The term applied to the function.
     * @return The node.
     * @throws NodeException If the function could not be created from the name.
     */
    static MathFunction mathFunction(String name, MathNode term) throws NodeException {
        return MathFunction.of(name, term);
    }

    /**
     * Implementation of {@link Node.Sequence} for {@link MathNode}.
     */
    abstract class Sequence<T extends Sequence.Term> extends Node.Sequence<T, MathNode, MathVisitor> implements MathNode {
        /**
         * A term in a sequence, which has an operation.
         */
        static interface Term extends Node.Sequence.Term<MathNode, MathVisitor> {
            /**
             * The operation that this term applies.
             * @return The operation.
             */
            String op();
            default double eval() throws EvaluationException { return node().eval(); }
        }

        @Override protected MathNode self() { return this; }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < terms.size(); i++) {
                T term = terms.get(i);
                result
                        .append(term.op())
                        .append(" (")
                        .append(term.node())
                        .append(")");
                if (i < terms.size() - 1) {
                    result.append(" ");
                }
            }
            return "[%s]".formatted(result.toString());
        }
    }

    /**
     * A node which holds a constant value.
     */
    final class Constant implements MathNode {
        /** The node type. */
        public static final int TYPE = 1;

        private final double value;

        /**
         * Creates an instance.
         * @param value The constant value.
         */
        public Constant(double value) {
            this.value = value;
        }

        /**
         * Creates a constant node from a parsed double value.
         * @param text The double as a string.
         * @return The node.
         * @throws NodeException If the number could not be parsed.
         */
        public static Constant of(String text) throws NodeException {
            try {
                return new Constant(Double.parseDouble(text));
            } catch (NumberFormatException e) {
                throw new NodeException("Invalid number `%s`".formatted(text));
            }
        }

        @Override public int type() { return TYPE; }

        @Override public double eval() { return value; }

        @Override public String toString() { return Double.toString(value); }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Constant constant = (Constant) o;
            return Double.compare(constant.value, value) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    /**
     * A node which holds a variable with a name and value.
     * <p>
     * If this node is part of a tree, the {@link MathVisitor.Variable} or
     * {@link #set(String, double)} can be used to set the value.
     */
    final class Variable implements MathNode {
        /** The node type. */
        public static final int TYPE = 2;

        /**
         * The default variable values that are used on variable nodes.
         */
        public static final Map<String, Double> DEFAULT_VARIABLES = ImmutableMap.<String, Double>builder()
            .put("PI", Math.PI)
            .put("E", Math.E)
            .build();

        private final String name;
        private Double value;

        /**
         * Creates an instance.
         * @param name The variable name.
         * @param value The variable value.
         */
        public Variable(String name, Double value) {
            this.name = name;
            this.value = value;
        }

        /**
         * Creates an instance.
         * @param name The variable name.
         */
        public Variable(String name) {
            this.name = name;
        }

        /**
         * Creates a variable node, setting its value to a default value from {@link #DEFAULT_VARIABLES} if known.
         * @param name The variable name.
         * @return The node.
         */
        public static Variable of(String name) {
            return new Variable(name, DEFAULT_VARIABLES.get(name));
        }

        @Override public int type() { return TYPE; }

        /**
         * Gets the variable name.
         * @return The name.
         */
        public String name() { return name; }

        /**
         * Gets the variable value.
         * @return The value.
         */
        public Double value() { return value; }

        /**
         * Sets the variable value.
         * @param value The value.
         * @return This instance.
         */
        public Variable value(Double value) { this.value = value; return this; }

        @Override
        public double eval() throws EvaluationException {
            if (value == null)
                throw new EvaluationException("Variable %s was not initialized".formatted(name));
            return value;
        }

        @Override public String toString() { return "<" + name + "> " + value; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Variable variable = (Variable) o;
            return name.equals(variable.name) && Objects.equals(value, variable.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, value);
        }
    }

    /**
     * A node which sums or subtracts multiple terms.
     */
    final class Sum extends Sequence<Sum.Term> {
        /** The node type. */
        public static final int TYPE = 3;

        /**
         * A term in a sequence.
         * @param node The node.
         * @param positive If the term is positive.
         */
        record Term(MathNode node, boolean positive) implements MathNode.Sequence.Term {
            @Override public String op() { return positive ? "+" : "-"; }
        }

        @Override public int type() { return TYPE; }

        @Override
        public Sum add(Term term) {
            super.add(term);
            return this;
        }

        /**
         * Adds a positive term from a node.
         * @param node The node.
         * @return This instance.
         */
        public Sum add(MathNode node) { return add(node, true); }

        /**
         * Adds a term from a node.
         * @param node The node.
         * @param positive If this term is positive or negative.
         * @return This instance.
         */
        public Sum add(MathNode node, boolean positive) { return add(new Term(node, positive)); }

        @Override
        public double eval() throws EvaluationException {
            double result = 0;
            for (Term term : terms) {
                if (term.positive)
                    result += term.eval();
                else
                    result -= term.eval();
            }
            return result;
        }
    }

    /**
     * A node which multiplies or divides multiple terms.
     */
    final class Product extends Sequence<Product.Term> {
        /** The node type. */
        public static final int TYPE = 4;

        /**
         * A term in a sequence.
         * @param node The node.
         * @param multiply If the term is multiplicative.
         */
        record Term(MathNode node, boolean multiply) implements MathNode.Sequence.Term {
            @Override public String op() { return multiply ? "*" : "/"; }
        }

        @Override public int type() { return TYPE; }

        @Override
        public Product add(Product.Term term) {
            super.add(term);
            return this;
        }

        /**
         * Adds a multiplicative term from a node.
         * @param node The node.
         * @return This instance.
         */
        public Product add(MathNode node) { return add(node, true); }

        /**
         * Adds a term from a node.
         * @param node The node.
         * @param multiply If this term is multiplies or divides.
         * @return This instance.
         */
        public Product add(MathNode node, boolean multiply) { return add(new Product.Term(node, multiply)); }

        @Override
        public double eval() throws EvaluationException {
            double result = 1;
            for (Product.Term term : terms) {
                if (term.multiply)
                    result *= term.eval();
                else
                    result /= term.eval();
            }
            return result;
        }
    }

    /**
     * A node which raises a base to an exponent.
     */
    final class Exponent implements MathNode {
        /** The node type. */
        public static final int TYPE = 5;

        private final MathNode base;
        private final MathNode exponent;

        /**
         * Creates an instance.
         * @param base The base.
         * @param exponent The exponent.
         */
        public Exponent(MathNode base, MathNode exponent) {
            Validation.notNull("base", base);
            Validation.notNull("exponent", exponent);
            this.base = base;
            this.exponent = exponent;
        }

        @Override public int type() { return TYPE; }

        /**
         * Gets the base.
         * @return The base.
         */
        public MathNode base() { return base; }

        /**
         * Gets the exponent.
         * @return The exponent.
         */
        public MathNode exponent() { return exponent; }

        @Override
        public MathNode accept(MathVisitor visitor) {
            visitor.visit(this);
            base.accept(visitor);
            exponent.accept(visitor);
            return this;
        }

        @Override
        public double eval() throws EvaluationException {
            return Math.pow(base.eval(), exponent.eval());
        }

        @Override public String toString() { return "%s ^ %s".formatted(base, exponent); }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Exponent exponent1 = (Exponent) o;
            return base.equals(exponent1.base) && exponent.equals(exponent1.exponent);
        }

        @Override
        public int hashCode() {
            return Objects.hash(base, exponent);
        }
    }

    /**
     * A node which applies a mathematical function to a term, e.g. sine and cosine.
     */
    final class MathFunction implements MathNode {
        /** The node type. */
        public static final int TYPE = 6;

        /**
         * A function that can map one number to another.
         * @param name The name of the function.
         * @param function The mapper function.
         */
        public record Function(String name, java.util.function.Function<Double, Double> function) {
            /**
             * Applies this function to a number.
             * @param value The number value.
             * @return The mapped number.
             */
            public double apply(double value) { return function.apply(value); }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Function function = (Function) o;
                return name.equals(function.name);
            }

            @Override
            public int hashCode() {
                return Objects.hash(name);
            }
        }

        /** {@link Math#floor(double)}. */
        public static final Function FLOOR = new Function("floor", Math::floor);
        /** {@link Math#ceil(double)}. */
        public static final Function CEIL = new Function("ceil", Math::ceil);
        /** {@link Math#round(double)}. */
        public static final Function ROUND = new Function("round", x -> (double) Math.round(x));

        /** {@link Math#abs(double)}. */
        public static final Function ABS = new Function("abs", Math::abs);
        /** {@link Math#sqrt(double)}. */
        public static final Function SQRT = new Function("sqrt", Math::sqrt);
        /** {@link Math#exp(double)}. */
        public static final Function EXP = new Function("exp", Math::exp);

        /** {@link Math#toRadians(double)}. */
        public static final Function RAD = new Function("rad", Math::toRadians);
        /** {@link Math#toDegrees(double)}. */
        public static final Function DEG = new Function("deg", Math::toDegrees);

        /** {@link Math#sin(double)}. */
        public static final Function SIN = new Function("sin", Math::sin);
        /** {@link Math#cos(double)}. */
        public static final Function COS = new Function("cos", Math::cos);
        /** {@link Math#tan(double)}. */
        public static final Function TAN = new Function("tan", Math::tan);

        /** {@link Math#asin(double)}. */
        public static final Function ASIN = new Function("asin", Math::asin);
        /** {@link Math#acos(double)}. */
        public static final Function ACOS = new Function("acos", Math::acos);
        /** {@link Math#atan(double)}. */
        public static final Function ATAN = new Function("atan", Math::atan);

        /** {@link Math#log(double)}. */
        public static final Function LN = new Function("ln", Math::log);
        /** {@link Math#log10(double)}. */
        public static final Function LOG = new Function("log", Math::log10);

        /** Map of default functions. */
        public static final Map<String, Function> FUNCTIONS = ImmutableMap.<String, Function>builder()
            .put(FLOOR.name, FLOOR)
            .put(CEIL.name, CEIL)
            .put(ROUND.name, ROUND)

            .put(ABS.name, ABS)
            .put(SQRT.name, SQRT)
            .put(EXP.name, EXP)

            .put(RAD.name, RAD)
            .put(DEG.name, DEG)

            .put(SIN.name, SIN)
            .put(COS.name, COS)
            .put(TAN.name, TAN)

            .put(ASIN.name, ASIN)
            .put(ACOS.name, ACOS)
            .put(ATAN.name, ATAN)

            .put(LN.name, LN)
            .put(LOG.name, LOG)
            .build();

        private final Function function;
        private final MathNode term;

        /**
         * Creates an instance.
         * @param function The function.
         * @param term The term that applies to the function.
         */
        public MathFunction(Function function, MathNode term) {
            Validation.notNull("function", function);
            Validation.notNull("term", term);
            this.function = function;
            this.term = term;
        }

        /**
         * Gets a default function from its name, throwing an exception if it is invalid.
         * @param name The function name.
         * @return The function.
         * @throws NodeException If the function is invalid.
         */
        public static Function function(String name) throws NodeException {
            Validation.notNull("name", name);
            Function function = FUNCTIONS.get(name);
            Validation.notNull(function, new NodeException("Invalid function `%s`".formatted(name)));
            return function;
        }

        /**
         * Creates a math function node using a default function from {@link #function(String)}.
         * @param name The function name.
         * @param term The term/
         * @return The node.
         * @throws NodeException If the function is invalid.
         */
        public static MathFunction of(String name, MathNode term) throws NodeException {
            return new MathFunction(function(name), term);
        }

        @Override public int type() { return TYPE; }

        /**
         * Gets the function.
         * @return The function.
         */
        public Function function() { return function; }

        /**
         * Gets the term.
         * @return The term.
         */
        public MathNode term() { return term; }

        @Override
        public MathNode accept(MathVisitor visitor) {
            visitor.visit(this);
            term.accept(visitor);
            return this;
        }

        @Override
        public double eval() throws EvaluationException {
            return function.apply(term.eval());
        }

        @Override public String toString() { return "%s(%s)".formatted(function.name, term); }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MathFunction that = (MathFunction) o;
            return function.equals(that.function) && term.equals(that.term);
        }

        @Override
        public int hashCode() {
            return Objects.hash(function, term);
        }
    }
}
