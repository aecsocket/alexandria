package io.github.aecsocket.alexandria.core.extension

import java.util.logging.Level
import java.util.logging.Logger

fun Logger.severe(message: String, cause: Throwable? = null) = log(Level.SEVERE, cause) { message }
fun Logger.warning(message: String, cause: Throwable? = null) = log(Level.WARNING, cause) { message }
fun Logger.info(message: String, cause: Throwable? = null) = log(Level.INFO, cause) { message }
fun Logger.config(message: String, cause: Throwable? = null) = log(Level.CONFIG, cause) { message }
fun Logger.fine(message: String, cause: Throwable? = null) = log(Level.FINE, cause) { message }
fun Logger.finer(message: String, cause: Throwable? = null) = log(Level.FINER, cause) { message }
fun Logger.finest(message: String, cause: Throwable? = null) = log(Level.FINEST, cause) { message }
