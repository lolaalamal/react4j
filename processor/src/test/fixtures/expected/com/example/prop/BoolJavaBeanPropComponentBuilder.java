package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
final class BoolJavaBeanPropComponentBuilder {
  private BoolJavaBeanPropComponentBuilder() {
  }

  @Nonnull
  static ReactNode foo(final boolean foo) {
    return new Builder().foo( foo );
  }

  public interface Step1 {
    @Nonnull
    ReactNode foo(boolean foo);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_BoolJavaBeanPropComponent.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode foo(final boolean foo) {
      _element.props().set( React4j_BoolJavaBeanPropComponent.Props.foo, foo );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}