package com.example.arez;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
final class ComponentFunctionalInterfacePropBuilder {
  private ComponentFunctionalInterfacePropBuilder() {
  }

  @Nonnull
  static ReactNode value(final ComponentFunctionalInterfaceProp.TestFunction value) {
    return new Builder().value( value );
  }

  public interface Step1 {
    @Nonnull
    ReactNode value(ComponentFunctionalInterfaceProp.TestFunction value);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_ComponentFunctionalInterfaceProp.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode value(final ComponentFunctionalInterfaceProp.TestFunction value) {
      _element.props().set( React4j_ComponentFunctionalInterfaceProp.Props.value, value );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
