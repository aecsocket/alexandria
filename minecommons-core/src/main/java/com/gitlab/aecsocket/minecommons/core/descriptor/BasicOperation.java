package com.gitlab.aecsocket.minecommons.core.descriptor;

public abstract class BasicOperation implements Operation {
    private final String symbol;
    private final boolean showSymbol;
    private final boolean supportsPercent;
    private final String[] aliases;

    public BasicOperation(String symbol, boolean showSymbol, boolean supportsPercent, String... aliases) {
        this.symbol = symbol;
        this.showSymbol = showSymbol;
        this.supportsPercent = supportsPercent;
        this.aliases = aliases;
    }

    @Override public String symbol() { return symbol; }
    @Override public boolean showSymbol() { return showSymbol; }
    @Override public boolean supportsPercent() { return supportsPercent; }
    @Override public String[] aliases() { return aliases; }
}
