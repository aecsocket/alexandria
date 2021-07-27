package com.gitlab.aecsocket.minecommons.core;

import com.gitlab.aecsocket.minecommons.core.bounds.Box;
import com.gitlab.aecsocket.minecommons.core.bounds.Sphere;
import org.junit.jupiter.api.Test;

import static com.gitlab.aecsocket.minecommons.core.bounds.Box.*;
import static com.gitlab.aecsocket.minecommons.core.bounds.Sphere.*;

import static com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3.*;

import static org.junit.jupiter.api.Assertions.*;

public class BoundsTest {
    void testBox0(Box box) {
        assertTrue(box.intersects(box.min()), box + " -> min");
        assertTrue(box.intersects(box.max()), box + " -> max");
        assertTrue(box.intersects(box.center()), box + " -> center");

        assertFalse(box.intersects(box.min().subtract(0.1)), box + " !-> slightly out of min");
        assertFalse(box.intersects(box.min().subtract(1)), box + " !-> largely out of min");
        assertFalse(box.intersects(box.max().add(0.1)), box + " !-> slightly out of max");
        assertFalse(box.intersects(box.max().add(1)), box + " !-> largely out of max");

        box = box.angle(Math.PI / 4); // make a diamond
        assertTrue(box.intersects(box.center().add(box.size().x() / 2, 0, 0)), box + " -> right vertex"); // right vertex
        assertFalse(box.intersects(box.center().add((box.size().x() / 2) + 1, 0, 0)), box + " !-> past right vertex"); // right vertex

        assertTrue(box.intersects(box.center().subtract(box.size().x() / 2, 0, 0)), box + " -> left vertex"); // left vertex
        assertFalse(box.intersects(box.center().subtract((box.size().x() / 2) + 1, 0, 0)), box + " !-> past left vertex"); // left vertex

        assertTrue(box.intersects(box.center().add(0, 0, box.size().z() / 2)), box + " -> top vertex"); // top vertex
        assertFalse(box.intersects(box.center().add(0, 0, (box.size().z() / 2) + 1)), box + " !-> past top vertex"); // top vertex

        assertTrue(box.intersects(box.center().subtract(0, 0, box.size().z() / 2)), box + " -> bottom vertex"); // bottom vertex
        assertFalse(box.intersects(box.center().subtract(0, 0, (box.size().z() / 2) + 1)), box + " !-> past bottom vertex"); // bottom vertex

        assertFalse(box.intersects(box.min()), box + " !-> min");
        assertFalse(box.intersects(box.max()), box + " !-> max");
    }

    @Test
    void testBoxIntersects() {
        testBox0(box(vec3(0), vec3(1)));
        testBox0(box(vec3(1), vec3(0)));
        testBox0(box(vec3(-1), vec3(1)));
        testBox0(box(vec3(0.5), vec3(1)));
        testBox0(box(vec3(-0.75), vec3(-0.5)));
    }

    @Test
    void testBoxValidation() {
        assertThrows(NullPointerException.class, () -> box(null, vec3(1)), "null min");
        assertThrows(NullPointerException.class, () -> box(vec3(1), null), "null max");
    }

    /*void testCylinder0(Cylinder cyl) {
        assertTrue(cyl.intersects(cyl.base()), cyl + " -> base");

        assertTrue(cyl.intersects(cyl.base().add(cyl.radius(), 0, 0)), cyl + " -> radius");
        assertFalse(cyl.intersects(cyl.base().add(cyl.radius() + 0.1, 0, 0)), cyl + " !-> past radius");
        assertFalse(cyl.intersects(cyl.base().add(cyl.radius(), 0, cyl.radius())), cyl + " !-> corner");

        assertTrue(cyl.intersects(cyl.base().add(0, cyl.height(), 0)), cyl + " -> radius");
        assertFalse(cyl.intersects(cyl.base().add(0, cyl.height() + 0.1, 0)), cyl + " !-> past radius");
    }

    @Test
    void testCylinderIntersects() {
        testCylinder0(cylinder(vec3(0), 1, 1));
        testCylinder0(cylinder(vec3(2), 0.75, 0.1));
        testCylinder0(cylinder(vec3(-0.5), 0.25, 2));
    }

    @Test
    void testCylinderValidation() {
        assertThrows(NullPointerException.class, () -> cylinder(null, 0, 0), "null base");
        assertThrows(IllegalArgumentException.class, () -> cylinder(vec3(0), 0, 1), "zero radius");
        assertThrows(IllegalArgumentException.class, () -> cylinder(vec3(0), -0.1, 1), "negative radius");
        assertThrows(IllegalArgumentException.class, () -> cylinder(vec3(0), 1, 0), "zero height");
        assertThrows(IllegalArgumentException.class, () -> cylinder(vec3(0), 1, -0.1), "negative height");
    }*/

    void testSphere0(Sphere sphr) {
        assertTrue(sphr.intersects(sphr.center()), sphr + " -> center");
        assertTrue(sphr.intersects(sphr.center().add(sphr.radius(), 0, 0)), sphr + " -> radius");
        assertFalse(sphr.intersects(sphr.center().add(sphr.radius())), sphr + " !-> corner");
    }

    @Test
    void testSphereIntersects() {
        testSphere0(sphere(vec3(0), 1));
        testSphere0(sphere(vec3(-2), 0.25));
    }

    @Test
    void testSphereValidation() {
        assertThrows(NullPointerException.class, () -> sphere(null, 0), "null center");
        assertThrows(IllegalArgumentException.class, () -> sphere(vec3(0), 0), "zero radius");
        assertThrows(IllegalArgumentException.class, () -> sphere(vec3(0), -0.1), "negative radius");
    }
}
