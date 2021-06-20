package com.gitlab.aecsocket.minecommons.core.descriptor;

import com.gitlab.aecsocket.minecommons.core.Validation;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector2;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;

public abstract class Descriptor<T> {
    protected final T mod;
    protected final Operation operation;
    protected final boolean percent;

    public Descriptor(T mod, Operation operation, boolean percent) {
        Validation.is(!operation.supportsPercent() && percent, "Operation [" + operation.symbol() + "] does not support percentages");
        this.mod = mod;
        this.operation = operation;
        this.percent = percent;
    }

    public Descriptor(T mod, Operation operation) {
        this(mod, operation, false);
    }

    public T mod() { return mod; }
    public Operation operation() { return operation; }
    public boolean percent() { return percent; }

    public abstract T apply(T base);

    @Override
    public String toString() {
        return (operation.showSymbol() ? operation.symbol() + " " : "") + mod + (percent ? "%" : "");
    }

    public static DDouble ofDouble(double mod, Operation operation, boolean percent) { return new DDouble(mod, operation, percent); }
    public static DDouble ofDouble(double mod, Operation operation) { return new DDouble(mod, operation); }

    public static DInteger ofInt(int mod, Operation operation, boolean percent) { return new DInteger(mod, operation, percent); }
    public static DInteger ofInt(int mod, Operation operation) { return new DInteger(mod, operation); }

    public static DVector2 ofVector2(Vector2 mod, Operation operation, boolean percent) { return new DVector2(mod, operation, percent); }
    public static DVector2 ofVector2(Vector2 mod, Operation operation) { return new DVector2(mod, operation); }

    public static DVector3 ofVector3(Vector3 mod, Operation operation, boolean percent) { return new DVector3(mod, operation, percent); }
    public static DVector3 ofVector3(Vector3 mod, Operation operation) { return new DVector3(mod, operation); }

    public static class DDouble extends Descriptor<Double> {
        public DDouble(double mod, Operation operation, boolean percent) { super(mod, operation, percent); }
        public DDouble(double mod, Operation operation) { super(mod, operation); }
        public double apply(double base) { return operation.apply(base, mod, percent); }
        @Override @Deprecated public Double apply(Double base) { return apply(base.doubleValue()); }
    }

    public static class DInteger extends Descriptor<Integer> {
        public DInteger(int mod, Operation operation, boolean percent) { super(mod, operation, percent); }
        public DInteger(int mod, Operation operation) { super(mod, operation); }
        public int apply(int base) { return (int) operation.apply(base, mod, percent); }
        @Override @Deprecated public Integer apply(Integer base) { return apply(base.intValue()); }
    }

    public static class DVector2 extends Descriptor<Vector2> {
        public DVector2(Vector2 mod, Operation operation, boolean percent) { super(mod, operation, percent); }
        public DVector2(Vector2 mod, Operation operation) { super(mod, operation); }
        @Override
        public Vector2 apply(Vector2 base) {
            return new Vector2(
                    operation.apply(base.x(), mod.x(), percent),
                    operation.apply(base.y(), mod.y(), percent)
            );
        }
    }

    public static class DVector3 extends Descriptor<Vector3> {
        public DVector3(Vector3 mod, Operation operation, boolean percent) { super(mod, operation, percent); }
        public DVector3(Vector3 mod, Operation operation) { super(mod, operation); }
        @Override
        public Vector3 apply(Vector3 base) {
            return new Vector3(
                    operation.apply(base.x(), mod.x(), percent),
                    operation.apply(base.y(), mod.y(), percent),
                    operation.apply(base.z(), mod.z(), percent)
            );
        }
    }
}
