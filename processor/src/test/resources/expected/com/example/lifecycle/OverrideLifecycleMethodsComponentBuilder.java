package com.example.lifecycle;

import java.util.Objects;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.React;
import react4j.core.ReactNode;

class OverrideLifecycleMethodsComponentBuilder {
  private OverrideLifecycleMethodsComponentBuilder() {
  }

  @Nonnull
  static ReactNode key(@Nonnull final String key) {
    return new Builder().key( key );
  }

  @Nonnull
  static ReactNode build() {
    return new Builder().build();
  }

  public interface Builder1 {
    @Nonnull
    ReactNode key(@Nonnull String key);
  }

  public interface Builder2 {
    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Builder1, Builder2 {
    private final JsPropertyMap<Object> _props = JsPropertyMap.of();

    @Override
    @Nonnull
    public final ReactNode key(@Nonnull final String key) {
      _props.set( "key", Objects.requireNonNull( key ) );
      return build();
    }

    @Override
    @Nonnull
    public final ReactNode build() {
      return React.createElement( OverrideLifecycleMethodsComponent_.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
