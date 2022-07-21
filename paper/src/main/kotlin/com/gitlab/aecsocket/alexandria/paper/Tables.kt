package com.gitlab.aecsocket.alexandria.paper

import com.gitlab.aecsocket.alexandria.core.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text

fun tableOfComponents(
    rows: Iterable<TableRow<Component>>,
    colAlign: (Int) -> TableAlign,
    rowAlign: (Int) -> TableAlign,
    colSeparator: (TableColumnSeparatorData) -> Component,
    rowSeparator: (TableRowSeparatorData) -> Iterable<Component>,
): TableData<Component> = tableOfComponents(
    rows, colAlign, rowAlign, colSeparator, rowSeparator,
    { AlexandriaAPI.widthOf(it) },
    { text(AlexandriaAPI.paddingOf(it)) },
)
