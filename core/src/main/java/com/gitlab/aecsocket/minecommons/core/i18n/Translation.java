package com.gitlab.aecsocket.minecommons.core.i18n;

import java.util.*;
import java.util.function.Consumer;

public final class Translation {
    private final Locale locale;
    private final Map<String, List<String>> handle;

    public Translation(Locale locale, Map<String, List<String>> handle) {
        this.locale = locale;
        this.handle = handle;
    }

    public Translation(Locale locale) {
        this.locale = locale;
        handle = new HashMap<>();
    }

    public interface TranslationsContext {
        TranslationsContext add(String key, String... lines);
    }

    public static Translation translation(Locale locale, Consumer<TranslationsContext> translations) {
        Map<String, List<String>> handle = new HashMap<>();
        translations.accept(new TranslationsContext() {
            @Override
            public TranslationsContext add(String key, String... lines) {
                handle.put(key, Arrays.asList(lines));
                return this;
            }
        });
        return new Translation(locale, handle);
    }

    public Locale locale() { return locale; }
    public Map<String, List<String>> handle() { return handle; }

    public List<String> get(String key) { return handle.get(key); }
    public List<String> get(String key, List<String> def) { return handle.getOrDefault(key, def); }
}
