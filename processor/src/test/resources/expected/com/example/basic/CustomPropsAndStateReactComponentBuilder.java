package com.example.basic;

import java.util.Objects;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.React;
import react4j.core.ReactNode;

class CustomPropsAndStateReactComponentBuilder {
  private CustomPropsAndStateReactComponentBuilder() {
  }

  @Nonnull
  static Builder2 key(@Nonnull final String key) {
    return new Builder().key( key );
  }

  @Nonnull
  static ReactNode someField(final boolean someField) {
    return new Builder().someField( someField );
  }

  public interface Builder1 {
    @Nonnull
    Builder2 key(@Nonnull String key);
  }

  public interface Builder2 {
    @Nonnull
    ReactNode someField(boolean someField);
  }

  private static class Builder implements Builder1, Builder2 {
    private final JsPropertyMap<Object> _props = JsPropertyMap.of();

    @Override
    @Nonnull
    public final Builder2 key(@Nonnull final String key) {
      _props.set( "key", Objects.requireNonNull( key ) );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode someField(final boolean someField) {
      _props.set( "someField", Objects.requireNonNull( someField ) );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return React.createElement( CustomPropsAndStateReactComponent_.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
