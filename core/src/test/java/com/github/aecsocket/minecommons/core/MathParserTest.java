package com.github.aecsocket.minecommons.core;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.Deque;
import java.util.function.Consumer;

import com.github.aecsocket.minecommons.core.expressions.math.MathNode;
import com.github.aecsocket.minecommons.core.expressions.math.MathParser;
import com.github.aecsocket.minecommons.core.expressions.node.EvaluationException;
import com.github.aecsocket.minecommons.core.expressions.parsing.ParsingException;
import com.github.aecsocket.minecommons.core.expressions.parsing.Token;
import com.github.aecsocket.minecommons.core.expressions.parsing.TokenzingException;

import static com.github.aecsocket.minecommons.core.expressions.math.MathNode.*;
import static org.junit.jupiter.api.Assertions.*;

class MathParserTest {
    void testToken(String text, Token... expected) throws TokenzingException {
        Deque<Token> actual = MathParser.tokenize(text);
        var it = actual.iterator();
        for (Token token : expected) {
            Token next = it.next();
            assertEquals(token, next);
        }
    }

    @Test
    void testTokens() throws TokenzingException {
        testToken("(", MathParser.OPEN_BRACKET_DEF.create("("));
        testToken(")", MathParser.CLOSE_BRACKET_DEF.create(")"));
        testToken("^", MathParser.EXPONENT_DEF.create("^"));
        testToken("sin", MathParser.FUNCTION_DEF.create("sin"));
        testToken("+ -", MathParser.PLUS_MINUS_DEF.create("+"), MathParser.PLUS_MINUS_DEF.create("-"));
        testToken("* /", MathParser.MULTIPLY_DIVIDE_DEF.create("*"), MathParser.MULTIPLY_DIVIDE_DEF.create("/"));
        // negative numbers use the sum node
        testToken("0 0.5", MathParser.CONSTANT_DEF.create("0"), MathParser.CONSTANT_DEF.create("0.5"));
        testToken("a a0", MathParser.VARIABLE_DEF.create("a"), MathParser.VARIABLE_DEF.create("a0"));

        testToken("1 + 2", MathParser.CONSTANT_DEF.create("1"), MathParser.PLUS_MINUS_DEF.create("+"), MathParser.CONSTANT_DEF.create("2"));
    }

    void testNode(String text, double expectedValue, MathNode expectedNode, @Nullable Consumer<MathNode> initializer) throws ParsingException, EvaluationException {
        if (initializer != null)
            initializer.accept(expectedNode);
        MathNode actual = MathParser.parse(text);
        if (initializer != null)
            initializer.accept(actual);
        assertEquals(expectedNode, actual);
        assertEquals(expectedValue, actual.eval());
    }

    void testNode(String text, double expectedValue, MathNode expectedNode) throws ParsingException, EvaluationException {
        testNode(text, expectedValue, expectedNode, null);
    }

    @Test
    void testConstants() throws ParsingException, EvaluationException {
        testNode("1", 1, constant(1));
        testNode("1.5", 1.5, constant(1.5));
    }

    @Test
    void testArithmetic() throws ParsingException, EvaluationException {
        testNode("1 + 1", 2, sum()
            .add(constant(1))
            .add(constant(1))
        );
        testNode("10 - 5", 5, sum()
            .add(constant(10))
            .add(constant(5), false)
        );
        testNode("10 + 5 - 3", 12, sum()
            .add(constant(10))
            .add(constant(5))
            .add(constant(3), false)
        );

        testNode("3 * 3", 9, product()
            .add(constant(3))
            .add(constant(3))
        );
        testNode("10 / 5 / 2", 4, product()
            .add(constant(10))
            .add(product()
                .add(constant(5))
                .add(constant(2), false),
            false)
        );
    }

    @Test
    void testVariables() throws ParsingException, EvaluationException {
        testNode("a", 2, variable("a"), n -> n.set("a", 2));
        testNode("a - b", 1, sum()
            .add(variable("a"))
            .add(variable("b"), false),
        n -> n.set("a", 3).set("b", 2));
    }

    @Test
    void testOrder() throws ParsingException, EvaluationException {
        testNode("2 * 8 + 2", 18, sum()
            .add(product()
                .add(constant(2))
                .add(constant(8))
            )
            .add(constant(2))
        );
        testNode("2 * (8 + 2)", 20, product()
            .add(constant(2))
            .add(sum()
                .add(constant(8))
                .add(constant(2))
            )
        );
    }

    @Test
    void testExtended() throws ParsingException, EvaluationException {
        testNode("4 ^ 2", 16, exponent(
            constant(4),
            constant(2)
        ));
        testNode("2 ^ 4", 16, exponent(
            constant(2),
            constant(4)
        ));

        testNode("sin PI", Math.sin(Math.PI), mathFunction(
            MathFunction.SIN,
            variable("PI")
        ));

        testNode("cos PI", Math.cos(Math.PI), mathFunction(
            MathFunction.COS,
            variable("PI")
        ));
    }
}
