package com.gitlab.aecsocket.minecommons.core.i18n;

public interface MutableI18N extends I18N {
    void clear();

    void registerFormat(String key, Format format);

    void registerTranslation(Translation translation);
}
