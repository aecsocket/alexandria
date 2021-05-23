package com.gitlab.aecsocket.minecommons.core.expressions.math;

import com.gitlab.aecsocket.minecommons.core.CollectionBuilder;
import com.gitlab.aecsocket.minecommons.core.Validation;
import com.gitlab.aecsocket.minecommons.core.expressions.node.EvaluationException;
import com.gitlab.aecsocket.minecommons.core.expressions.node.Node;
import com.gitlab.aecsocket.minecommons.core.expressions.parsing.NodeException;

import java.util.*;

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

    static Constant constant(double value) {
        return new Constant(value);
    }
    static Constant constant(String text) throws NodeException {
        return Constant.of(text);
    }

    static Variable variable(String name){
        return Variable.of(name);
    }

    static Sum sum() {
        return new Sum();
    }
    static Sum sum(Sum.Term... terms) {
        return new Sum(terms);
    }

    static Product product() {
        return new Product();
    }
    static Product product(Product.Term... terms) {
        return new Product(terms);
    }

    static Exponent exponent(MathNode base, MathNode exponent) {
        return new Exponent(base, exponent);
    }

    static MathFunction mathFunction(MathFunction.Function function, MathNode term) {
        return new MathFunction(function, term);
    }
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
        interface Term extends Node.Sequence.Term<MathNode, MathVisitor> {
            /**
             * The operation that this term applies.
             * @return The operation.
             */
            String op();
            default double eval() throws EvaluationException { return node().eval(); }
        }

        public Sequence() {}
        public Sequence(List<T> terms) { super(terms); }
        public Sequence(T... terms) { super(terms); }

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
        public static final int TYPE = 1;

        private final double value;

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

        public double value() { return value; }
        @Override public double eval() throws EvaluationException { return value; }

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
        public static final int TYPE = 2;

        /**
         * The default variable values that are used on variable nodes.
         */
        public static final Map<String, Double> DEFAULT_VARIABLES = CollectionBuilder.map(new HashMap<String, Double>())
                .put("PI", Math.PI)
                .put("E", Math.E)
                .build();

        private final String name;
        private Double value;

        public Variable(String name, Double value) {
            this.name = name;
            this.value = value;
        }

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

        public String name() { return name; }

        public Double value() { return value; }
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
        public static final int TYPE = 3;

        record Term(MathNode node, boolean positive) implements MathNode.Sequence.Term {
            @Override public String op() { return positive ? "+" : "-"; }
        }

        public Sum() {}
        public Sum(List<Sum.Term> terms) { super(terms); }
        public Sum(Sum.Term... terms) { super(terms); }

        @Override public int type() { return TYPE; }

        @Override
        public Sum add(Term term) {
            super.add(term);
            return this;
        }

        public Sum add(MathNode node) { return add(node, true); }
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
        public static final int TYPE = 4;

        record Term(MathNode node, boolean multiply) implements MathNode.Sequence.Term {
            @Override public String op() { return multiply ? "*" : "/"; }
        }

        public Product() {}
        public Product(List<Product.Term> terms) { super(terms); }
        public Product(Product.Term... terms) { super(terms); }

        @Override public int type() { return TYPE; }

        @Override
        public Product add(Product.Term term) {
            super.add(term);
            return this;
        }

        public Product add(MathNode node) { return add(node, true); }
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
        public static final int TYPE = 5;

        private final MathNode base;
        private final MathNode exponent;

        public Exponent(MathNode base, MathNode exponent) {
            Validation.notNull(base, "base");
            Validation.notNull(exponent, "exponent");
            this.base = base;
            this.exponent = exponent;
        }

        @Override public int type() { return TYPE; }

        public MathNode base() { return base; }
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
        public static final int TYPE = 6;

        public record Function(String name, java.util.function.Function<Double, Double> function) {
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

        public static final Function FLOOR = new Function("floor", Math::floor);
        public static final Function CEIL = new Function("ceil", Math::ceil);
        public static final Function ROUND = new Function("round", x -> (double) Math.round(x));

        public static final Function ABS = new Function("abs", Math::abs);
        public static final Function SQRT = new Function("sqrt", Math::sqrt);
        public static final Function EXP = new Function("exp", Math::exp);

        public static final Function RAD = new Function("rad", Math::toRadians);
        public static final Function DEG = new Function("deg", Math::toDegrees);

        public static final Function SIN = new Function("sin", Math::sin);
        public static final Function COS = new Function("cos", Math::cos);
        public static final Function TAN = new Function("tan", Math::tan);

        public static final Function ASIN = new Function("asin", Math::asin);
        public static final Function ACOS = new Function("acos", Math::acos);
        public static final Function ATAN = new Function("atan", Math::atan);

        public static final Function LN = new Function("ln", Math::log);
        public static final Function LOG = new Function("log", Math::log10);

        /** Map of default functions. */
        public static final Map<String, Function> FUNCTIONS = CollectionBuilder.map(new HashMap<String, Function>())
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

        public MathFunction(Function function, MathNode term) {
            Validation.notNull(function, "function");
            Validation.notNull(term, "term");
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
            Validation.notNull(name, "name");
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

        public Function function() { return function; }
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
