package io.github.aecsocket.alexandria.extension

import java.util.logging.Level
import java.util.logging.Logger

fun Logger.severe (cause: Throwable? = null, message: () -> String) = log(Level.SEVERE,  cause, message)
fun Logger.warning(cause: Throwable? = null, message: () -> String) = log(Level.WARNING, cause, message)
fun Logger.info   (cause: Throwable? = null, message: () -> String) = log(Level.INFO,    cause, message)
fun Logger.config (cause: Throwable? = null, message: () -> String) = log(Level.CONFIG,  cause, message)
fun Logger.fine   (cause: Throwable? = null, message: () -> String) = log(Level.FINE,    cause, message)
fun Logger.finer  (cause: Throwable? = null, message: () -> String) = log(Level.FINER,   cause, message)
fun Logger.finest (cause: Throwable? = null, message: () -> String) = log(Level.FINEST,  cause, message)
