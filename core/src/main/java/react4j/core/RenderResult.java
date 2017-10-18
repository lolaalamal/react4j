package react4j.core;

import elemental2.core.Array;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

/**
 * Union type representing possible output of render method.
 */
@JsType( isNative = true, name = "?", namespace = JsPackage.GLOBAL )
public interface RenderResult
{
  @JsOverlay
  static RenderResult of( final String string )
  {
    return Js.cast( string );
  }

  /**
   * Convert children to a render result.
   * The children type is already an abstraction over one of the underlying render results
   * so no explicit conversion required.
   *
   * @param children the react element children.
   * @return the render result.
   */
  @JsOverlay
  static RenderResult of( final ReactElementChildren children )
  {
    return Js.cast( children );
  }

  @JsOverlay
  static RenderResult of( final ReactElement<?, ?> element )
  {
    return Js.cast( element );
  }

  @JsOverlay
  static RenderResult of( final ReactElement<?, ?>... elements )
  {
    return Js.cast( elements );
  }

  @JsOverlay
  static RenderResult of( final Array<RenderResult> elements )
  {
    return Js.cast( elements );
  }

  @JsOverlay
  static RenderResult of( final RenderResult... elements )
  {
    return Js.cast( elements );
  }

  @JsOverlay
  default String asString()
  {
    return Js.cast( this );
  }

  @JsOverlay
  default ReactElement<?, ?> asReactElement()
  {
    return Js.cast( this );
  }

  @JsOverlay
  default RenderResult[] asRenderResult()
  {
    return Js.cast( this );
  }

  @JsOverlay
  default boolean isString()
  {
    return (Object) this instanceof String;
  }

  @JsOverlay
  default boolean isReactElement()
  {
    return this instanceof ReactElement && !Array.isArray( this );
  }

  @JsOverlay
  default boolean isRenderResults()
  {
    return Array.isArray( this );
  }
}
