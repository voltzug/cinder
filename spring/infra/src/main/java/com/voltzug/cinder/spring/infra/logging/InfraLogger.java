package com.voltzug.cinder.spring.infra.logging;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized logging utility for the infrastructure module.
 *
 * @see org.slf4j.Logger
 * @see java.util.logging.Logger
 */
public final class InfraLogger {

  private final org.slf4j.Logger _slf4jLogger;
  private final Logger _julLogger;
  private final String _name;

  private InfraLogger(Class<?> clazz) {
    this._name = clazz.getName();
    this._slf4jLogger = LoggerFactory.getLogger(clazz);
    this._julLogger = Logger.getLogger(clazz.getName());
  }

  private InfraLogger(String name) {
    this._name = name;
    this._slf4jLogger = LoggerFactory.getLogger(name);
    this._julLogger = Logger.getLogger(name);
  }

  /**
   * Creates a logger for the specified class.
   * This is the preferred way to create loggers.
   *
   * @param clazz the class for which to create a logger
   * @return a new InfraLogger instance
   */
  public static InfraLogger of(Class<?> clazz) {
    return new InfraLogger(clazz);
  }

  /**
   * Creates a logger with the specified name.
   * Use this when you need a custom logger name.
   *
   * @param name the logger name
   * @return a new InfraLogger instance
   */
  public static InfraLogger of(String name) {
    return new InfraLogger(name);
  }

  /**
   * Returns the logger name.
   */
  public String getName() {
    return _name;
  }

  /**
   * Logs an error message.
   */
  public void error(String message) {
    _slf4jLogger.error(message);
    _julLogger.severe(message);
  }

  /**
   * Logs an error message with a throwable.
   *
   * @param message the error message
   * @param throwable the exception
   */
  public void error(String message, Throwable throwable) {
    _slf4jLogger.error(message, throwable);
    _julLogger.log(Level.SEVERE, message, throwable);
  }

  /**
   * Logs an error message with one argument.
   *
   * @param format the message format
   * @param arg the argument
   */
  public void error(String format, Object arg) {
    _slf4jLogger.error(format, arg);
    _julLogger.severe(String.format(format.replace("{}", "%s"), arg));
  }

  /**
   * Logs an error message with two arguments.
   *
   * @param format the message format
   * @param arg1 the first argument
   * @param arg2 the second argument
   */
  public void error(String format, Object arg1, Object arg2) {
    _slf4jLogger.error(format, arg1, arg2);
    _julLogger.severe(String.format(format.replace("{}", "%s"), arg1, arg2));
  }

  /**
   * Logs an error message with multiple arguments.
   *
   * @param format the message format
   * @param args the arguments
   */
  public void error(String format, Object... args) {
    _slf4jLogger.error(format, args);
    _julLogger.severe(String.format(format.replaceAll("\\{\\}", "%s"), args));
  }

  /**
   * Logs a warning message.
   */
  public void warn(String message) {
    _slf4jLogger.warn(message);
    _julLogger.warning(message);
  }

  /**
   * Logs a warning message with a throwable.
   *
   * @param message the warning message
   * @param throwable the exception
   */
  public void warn(String message, Throwable throwable) {
    _slf4jLogger.warn(message, throwable);
    _julLogger.log(Level.WARNING, message, throwable);
  }

  /**
   * Logs a warning message with one argument.
   *
   * @param format the message format
   * @param arg the argument
   */
  public void warn(String format, Object arg) {
    _slf4jLogger.warn(format, arg);
    _julLogger.warning(String.format(format.replace("{}", "%s"), arg));
  }

  /**
   * Logs a warning message with two arguments.
   *
   * @param format the message format
   * @param arg1 the first argument
   * @param arg2 the second argument
   */
  public void warn(String format, Object arg1, Object arg2) {
    _slf4jLogger.warn(format, arg1, arg2);
    _julLogger.warning(String.format(format.replace("{}", "%s"), arg1, arg2));
  }

  /**
   * Logs a warning message with multiple arguments.
   *
   * @param format the message format
   * @param args the arguments
   */
  public void warn(String format, Object... args) {
    _slf4jLogger.warn(format, args);
    _julLogger.warning(String.format(format.replaceAll("\\{\\}", "%s"), args));
  }

  /**
   * Logs an info message.
   */
  public void info(String message) {
    _slf4jLogger.info(message);
    _julLogger.info(message);
  }

  /**
   * Logs an info message with one argument.
   *
   * @param format the message format
   * @param arg the argument
   */
  public void info(String format, Object arg) {
    _slf4jLogger.info(format, arg);
    _julLogger.info(String.format(format.replace("{}", "%s"), arg));
  }

  /**
   * Logs an info message with two arguments.
   *
   * @param format the message format
   * @param arg1 the first argument
   * @param arg2 the second argument
   */
  public void info(String format, Object arg1, Object arg2) {
    _slf4jLogger.info(format, arg1, arg2);
    _julLogger.info(String.format(format.replace("{}", "%s"), arg1, arg2));
  }

  /**
   * Logs an info message with multiple arguments.
   *
   * @param format the message format
   * @param args the arguments
   */
  public void info(String format, Object... args) {
    _slf4jLogger.info(format, args);
    _julLogger.info(String.format(format.replaceAll("\\{\\}", "%s"), args));
  }

  /**
   * Checks if debug logging is enabled.
   */
  public boolean isDebugEnabled() {
    return _slf4jLogger.isDebugEnabled();
  }

  /**
   * Logs a debug message.
   */
  public void debug(String message) {
    _slf4jLogger.debug(message);
    _julLogger.fine(message);
  }

  /**
   * Logs a debug message with one argument.
   *
   * @param format the message format
   * @param arg the argument
   */
  public void debug(String format, Object arg) {
    _slf4jLogger.debug(format, arg);
    if (_julLogger.isLoggable(Level.FINE)) {
      _julLogger.fine(String.format(format.replace("{}", "%s"), arg));
    }
  }

  /**
   * Logs a debug message with two arguments.
   *
   * @param format the message format
   * @param arg1 the first argument
   * @param arg2 the second argument
   */
  public void debug(String format, Object arg1, Object arg2) {
    _slf4jLogger.debug(format, arg1, arg2);
    if (_julLogger.isLoggable(Level.FINE)) {
      _julLogger.fine(String.format(format.replace("{}", "%s"), arg1, arg2));
    }
  }

  /**
   * Logs a debug message with multiple arguments.
   *
   * @param format the message format
   * @param args the arguments
   */
  public void debug(String format, Object... args) {
    _slf4jLogger.debug(format, args);
    if (_julLogger.isLoggable(Level.FINE)) {
      _julLogger.fine(String.format(format.replaceAll("\\{\\}", "%s"), args));
    }
  }

  /**
   * Checks if trace logging is enabled.
   */
  public boolean isTraceEnabled() {
    return _slf4jLogger.isTraceEnabled();
  }

  /**
   * Logs a trace message.
   */
  public void trace(String message) {
    _slf4jLogger.trace(message);
    _julLogger.finest(message);
  }

  /**
   * Logs a trace message with one argument.
   *
   * @param format the message format
   * @param arg the argument
   */
  public void trace(String format, Object arg) {
    _slf4jLogger.trace(format, arg);
    if (_julLogger.isLoggable(Level.FINEST)) {
      _julLogger.finest(String.format(format.replace("{}", "%s"), arg));
    }
  }

  /**
   * Logs a trace message with two arguments.
   *
   * @param format the message format
   * @param arg1 the first argument
   * @param arg2 the second argument
   */
  public void trace(String format, Object arg1, Object arg2) {
    _slf4jLogger.trace(format, arg1, arg2);
    if (_julLogger.isLoggable(Level.FINEST)) {
      _julLogger.finest(String.format(format.replace("{}", "%s"), arg1, arg2));
    }
  }

  /**
   * Returns the underlying SLF4J logger.
   * Use this for advanced SLF4J-specific features.
   */
  public org.slf4j.Logger getSlf4jLogger() {
    return _slf4jLogger;
  }

  /**
   * Returns the underlying java.util.logging Logger.
   * Use this for advanced JUL-specific features.
   */
  public Logger getJulLogger() {
    return _julLogger;
  }
}
