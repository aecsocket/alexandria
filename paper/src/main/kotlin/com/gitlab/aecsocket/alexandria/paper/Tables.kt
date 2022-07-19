package com.gitlab.aecsocket.alexandria.paper

import com.gitlab.aecsocket.alexandria.core.ColumnAlign
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

fun tableOfComponents(
    rows: Iterable<Iterable<Component>>,
    separator: Component,
    alignCol: (Int) -> ColumnAlign,
) = com.gitlab.aecsocket.alexandria.core.tableOfComponents(
    rows,
    separator,
    alignCol,
    { AlexandriaAPI.widthOf(PlainTextComponentSerializer.plainText().serialize(it)) },
    { text(AlexandriaAPI.padding.repeat(it / (AlexandriaAPI.paddingWidth + 1))) }
)
