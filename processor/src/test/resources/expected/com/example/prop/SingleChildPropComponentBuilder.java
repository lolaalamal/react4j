package com.example.prop;

import elemental2.core.JsArray;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
final class SingleChildPropComponentBuilder {
  private SingleChildPropComponentBuilder() {
  }

  @Nonnull
  static ReactNode child(final ReactNode child) {
    return new Builder().child( child );
  }

  public interface Step1 {
    @Nonnull
    ReactNode child(ReactNode child);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_SingleChildPropComponent.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode child(final ReactNode child) {
      _element.props().set( React4j_SingleChildPropComponent.Props.child, JsArray.of( child ) );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
