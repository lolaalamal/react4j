package react.common;

import jsinterop.annotations.JsFunction;

/**
 * Interface for performing an action that does not return a value.
 */
@FunctionalInterface
@JsFunction
public interface Procedure
{
  /**
   * Perform an action.
   */
  void call();
}
