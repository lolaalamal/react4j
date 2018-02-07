package react4j.core;

import jsinterop.annotations.JsFunction;
import jsinterop.base.JsPropertyMap;

/**
 * A function interface to create component instances.
 * This is typically implemented by the classed generated by the annotation processor and should
 * not be directly implemented.
 *
 * @param <C> the type of the component context.
 */
@FunctionalInterface
@JsFunction
public interface ComponentConstructorFunction<C extends BaseContext>
{
  /**
   * Construct a component based on specified properties.
   *
   * @param props   the component props.
   * @param context the component context.
   * @return the component.
   */
  NativeComponent<?, C> construct( JsPropertyMap<Object> props, C context );
}
