package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.Keyed;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
final class ImmutablePropTypeKeyedMultiStepInheritedBuilder {
  private ImmutablePropTypeKeyedMultiStepInheritedBuilder() {
  }

  @Nonnull
  static ReactNode myProp(final ImmutablePropTypeKeyedMultiStepInherited.KeyedComponent myProp) {
    return new Builder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(ImmutablePropTypeKeyedMultiStepInherited.KeyedComponent myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_ImmutablePropTypeKeyedMultiStepInherited.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(
        final ImmutablePropTypeKeyedMultiStepInherited.KeyedComponent myProp) {
      _element.setKey( ImmutablePropTypeKeyedMultiStepInherited.class.getName() + Keyed.getKey( myProp ) );
      _element.props().set( React4j_ImmutablePropTypeKeyedMultiStepInherited.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
