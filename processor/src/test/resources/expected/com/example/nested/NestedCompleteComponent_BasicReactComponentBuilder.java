package com.example.nested;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
final class NestedCompleteComponent_BasicReactComponentBuilder {
  private NestedCompleteComponent_BasicReactComponentBuilder() {
  }

  @Nonnull
  static ReactNode myProp(final String myProp) {
    return new Builder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(String myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( NestedCompleteComponent_React4j_BasicReactComponent.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final String myProp) {
      _element.props().set( NestedCompleteComponent_React4j_BasicReactComponent.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
