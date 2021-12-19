package com.gitlab.aecsocket.minecommons.core.expressions.math;

import com.gitlab.aecsocket.minecommons.core.expressions.parsing.*;

import java.util.Deque;

/**
 * Tokenizer and evaluator of math expressions.
 */
public final class MathParser {
    private MathParser() {}

    /** Token of {@link #OPEN_BRACKET_DEF}. */
    public static final int OPEN_BRACKET = 1;
    /** Token of {@link #CLOSE_BRACKET_DEF}. */
    public static final int CLOSE_BRACKET = 2;
    /** Token of {@link #EXPONENT_DEF}. */
    public static final int EXPONENT = 3;
    /** Token of {@link #FUNCTION_DEF}. */
    public static final int FUNCTION = 4;
    /** Token of {@link #PLUS_MINUS_DEF}. */
    public static final int PLUS_MINUS = 5;
    /** Token of {@link #MULTIPLY_DIVIDE_DEF}. */
    public static final int MULTIPLY_DIVIDE = 6;
    /** Token of {@link #CONSTANT_DEF}. */
    public static final int CONSTANT = 7;
    /** Token of {@link #VARIABLE_DEF}. */
    public static final int VARIABLE = 8;

    /** Definition for an opening bracket. */
    public static final Tokenizer.Definition OPEN_BRACKET_DEF = Tokenizer.Definition.of("\\(", OPEN_BRACKET);
    /** Definition for a closing bracket. */
    public static final Tokenizer.Definition CLOSE_BRACKET_DEF = Tokenizer.Definition.of("\\)", CLOSE_BRACKET);
    /** Definition for an exponent. */
    public static final Tokenizer.Definition EXPONENT_DEF = Tokenizer.Definition.of("\\^", EXPONENT);
    /** Definition for a mathematical function. */
    public static final Tokenizer.Definition FUNCTION_DEF = Tokenizer.Definition.of(String.join("|", MathNode.MathFunction.FUNCTIONS.keySet()), FUNCTION);
    /** Definition for a plus/minus operator. */
    public static final Tokenizer.Definition PLUS_MINUS_DEF = Tokenizer.Definition.of("[+-]", PLUS_MINUS);
    /** Definition for a multiply/divide operator. */
    public static final Tokenizer.Definition MULTIPLY_DIVIDE_DEF = Tokenizer.Definition.of("[*/]", MULTIPLY_DIVIDE);
    /** Definition for a numerical constant. */
    public static final Tokenizer.Definition CONSTANT_DEF = Tokenizer.Definition.of("[0-9.]+", CONSTANT);
    /** Definition for a numerical variable. */
    public static final Tokenizer.Definition VARIABLE_DEF = Tokenizer.Definition.of("[a-zA-Z][a-zA-Z0-9_]*", VARIABLE);

    /** The singleton tokenizer instance. */
    public static final Tokenizer TOKENIZER = Tokenizer.builder()
            .add(OPEN_BRACKET_DEF)
            .add(CLOSE_BRACKET_DEF)
            .add(EXPONENT_DEF)
            .add(FUNCTION_DEF)
            .add(PLUS_MINUS_DEF)
            .add(MULTIPLY_DIVIDE_DEF)
            .add(CONSTANT_DEF)
            .add(VARIABLE_DEF)
            .build();

    /**
     * Tokenizes text input.
     * @param text The text.
     * @return The tokenized output.
     * @throws TokenzingException If the text could not be tokenized.
     */
    public static Deque<Token> tokenize(String text) throws TokenzingException {
        return TOKENIZER.tokenize(text);
    }

    /**
     * Grammar:
     * <pre>{@code <expression>    ::= <signed_term> <sum_op>}</pre>
     * <pre>{@code <sum_op>        ::= PLUS_MINUS <term> <sum_op> | ε}</pre>
     * <pre>{@code <signed_term>   ::= PLUS_MINUS <term> | <term>}</pre>
     * <pre>{@code <term>          ::= <factor> <term_op>}</pre>
     * <pre>{@code <term_op>       ::= MULTIPLY_DIVIDE <factor> <term_op> | ε}</pre>
     * <pre>{@code <signed_factor> ::= PLUS_MINUS <factor> | <factor>}</pre>
     * <pre>{@code <factor>        ::= <argument> <factor_op>}</pre>
     * <pre>{@code <factor_op>     ::= EXPONENT <expression> | ε}</pre>
     * <pre>{@code <argument>      ::= FUNCTION <argument> | OPEN_BRACKET <sum> CLOSE_BRACKET | <value>}</pre>
     * <pre>{@code <value>         ::= NUMBER | VARIABLE}</pre>
     */
    public static final class MathNodeCreator extends AbstractNodeCreator<MathNode> {
        /**
         * <pre>{@code <expression> ::= <signed_term> <sum_op>}</pre>
         * @return The node.
         * @throws NodeException If the node could not be created.
         */
        @Override
        protected MathNode expression() throws NodeException {
            return sumOp(signedTerm());
        }

        /**
         * <pre>{@code <signed_term> ::= PLUS_MINUS <term> | <term>}</pre>
         * @param term <pre>{@code <term>}</pre>
         * @return The node.
         * @throws NodeException If the node could not be created.
         */
        private MathNode sumOp(MathNode term) throws NodeException {
            switch (lookahead.type()) {
                case PLUS_MINUS -> {
                    MathNode.Sum sum = term.type() == MathNode.Sum.TYPE
                            ? (MathNode.Sum) term
                            : MathNode.sum().add(term);

                    boolean positive = lookahead.sequence().equals("+");
                    next();
                    MathNode next = term();
                    sum.add(next, positive);
                    return sumOp(sum);
                }
            }
            return term;
        }

        /**
         * <pre>{@code <signed_term> ::= PLUS_MINUS <term> | <term>}</pre>
         * @return The node.
         * @throws NodeException If the node could not be created.
         */
        private MathNode signedTerm() throws NodeException {
            switch (lookahead.type()) {
                case PLUS_MINUS -> {
                    boolean positive = lookahead.sequence().equals("+");
                    next();
                    MathNode next = term();
                    return positive
                            ? next
                            : MathNode.sum().add(next, false);
                }
            }
            return term();
        }

        /**
         * <pre>{@code <term> ::= <factor> <term_op>}</pre>
         * @return The node.
         * @throws NodeException If the node could not be created.
         */
        private MathNode term() throws NodeException {
            return termOp(factor());
        }


        /**
         * <pre>{@code <term_op> ::= MULTIPLY_DIVIDE <factor> <term_op> | ε}</pre>
         * @param term <pre>{@code <term>}</pre>
         * @return The node.
         * @throws NodeException If the node could not be created.
         */
        private MathNode termOp(MathNode term) throws NodeException {
            switch (lookahead.type()) {
                case MULTIPLY_DIVIDE -> {
                    MathNode.Product product = term.type() == MathNode.Product.TYPE
                            ? (MathNode.Product) term
                            : MathNode.product().add(term);

                    boolean multiply = lookahead.sequence().equals("*");
                    next();
                    MathNode next = term();
                    product.add(next, multiply);
                    return termOp(product);
                }
            }
            return term;
        }

        /**
         * <pre>{@code <signed_factor> ::= PLUS_MINUS <factor> | <factor>}</pre>
         * @return The node.
         * @throws NodeException If the node could not be created.
         */
        private MathNode signedFactor() throws NodeException {
            switch (lookahead.type()) {
                case PLUS_MINUS -> {
                    boolean positive = lookahead.sequence().equals("+");
                    next();
                    MathNode next = factor();
                    return positive ? next : MathNode.sum().add(next, false);
                }
            }
            return factor();
        }

        /**
         * <pre>{@code <factor> ::= <argument> <factor_op>}</pre>
         * @return The node.
         * @throws NodeException If the node could not be created.
         */
        private MathNode factor() throws NodeException {
            return factorOp(argument());
        }

        /**
         * <pre>{@code <factor_op>     ::= EXPONENT <expression> | ε}</pre>
         * @param term <pre>{@code <term>}</pre>
         * @return The node.
         * @throws NodeException If the node could not be created.
         */
        private MathNode factorOp(MathNode term) throws NodeException {
            switch (lookahead.type()) {
                case EXPONENT -> {
                    next();
                    MathNode exponent = signedFactor();
                    return MathNode.exponent(term, exponent);
                }
            }
            return term;
        }

        /**
         * <pre>{@code <argument>      ::= FUNCTION <argument> | OPEN_BRACKET <sum> CLOSE_BRACKET | <value>}</pre>
         * @return The node.
         * @throws NodeException If the node could not be created.
         */
        private MathNode argument() throws NodeException {
            switch (lookahead.type()) {
                case FUNCTION -> {
                    MathNode.MathFunction.Function function = MathNode.MathFunction.function(lookahead.sequence());
                    next();
                    MathNode next = argument();
                    return MathNode.mathFunction(function, next);
                }
                case OPEN_BRACKET -> {
                    next();
                    MathNode result = expression();
                    if (lookahead.type() != CLOSE_BRACKET)
                        throw new NodeException.IllegalToken(lookahead, "CLOSE_BRACKET");
                    next();
                    return result;
                }
            }
            return value();
        }

        /**
         * <pre>{@code <value> ::= NUMBER | VARIABLE}</pre>
         * @return The node.
         * @throws NodeException If the node could not be created.
         */
        private MathNode value() throws NodeException {
            switch (lookahead.type()) {
                case CONSTANT -> {
                    MathNode result = MathNode.constant(lookahead.sequence());
                    next();
                    return result;
                }
                case VARIABLE -> {
                    MathNode result = MathNode.variable(lookahead.sequence());
                    next();
                    return result;
                }
            }
            throw new NodeException.IllegalToken(lookahead, "CONSTANT or VARIABLE");
        }
    }

    /**
     * Evaluates expression nodes from tokens.
     * @param tokens The tokens.
     * @return The node.
     * @throws NodeException If the node could not be created.
     */
    public static MathNode node(Deque<Token> tokens) throws NodeException {
        return new MathNodeCreator().node(tokens);
    }

    /**
     * Fully parses text input into an expression node: tokenizes and evaluates.
     * @param text The text.
     * @return The node.
     * @throws TokenzingException If the text could nto be tokenized.
     * @throws NodeException If the node could not be created.
     */
    public static MathNode parse(String text) throws TokenzingException, NodeException { return node(tokenize(text)); }
}
