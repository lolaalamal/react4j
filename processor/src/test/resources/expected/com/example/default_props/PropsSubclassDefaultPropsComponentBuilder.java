package com.example.default_props;

import java.util.Objects;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.React;
import react4j.core.ReactNode;

class PropsSubclassDefaultPropsComponentBuilder {
  private PropsSubclassDefaultPropsComponentBuilder() {
  }

  @Nonnull
  static Builder2 key(@Nonnull final String key) {
    return new Builder().key( key );
  }

  @Nonnull
  static ReactNode isMyField(@Nonnull final int isMyField) {
    return new Builder().isMyField( isMyField );
  }

  public interface Builder1 {
    Builder2 key(@Nonnull String key);
  }

  public interface Builder2 {
    ReactNode isMyField(@Nonnull int isMyField);
  }

  public interface Builder3 {
    ReactNode build();
  }

  private static class Builder implements Builder1, Builder2, Builder3 {
    private final JsPropertyMap<Object> _props = JsPropertyMap.of();

    @Override
    @Nonnull
    public final Builder2 key(@Nonnull final String key) {
      _props.set( "key", Objects.requireNonNull( key ) );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode isMyField(@Nonnull final int isMyField) {
      _props.set( "isMyField", Objects.requireNonNull( isMyField ) );
      return build();
    }

    @Override
    @Nonnull
    public final ReactNode build() {
      return React.createElement( PropsSubclassDefaultPropsComponent_.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
