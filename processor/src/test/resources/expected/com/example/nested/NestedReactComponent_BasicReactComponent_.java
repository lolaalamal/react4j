package com.example.nested;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class NestedReactComponent_BasicReactComponent_ extends NestedReactComponent.BasicReactComponent {
  static final ComponentConstructorFunction<BaseProps, BaseContext> TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseContext> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseContext> componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "BasicReactComponent" );
    }
    return componentConstructor;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BaseProps, BaseState, BaseContext, NestedReactComponent.BasicReactComponent> {
    NativeReactComponent(@Nullable final BaseProps props, @Nullable final BaseContext context) {
      super( props, context );
    }

    @Override
    protected NestedReactComponent.BasicReactComponent createComponent() {
      return new NestedReactComponent_BasicReactComponent_();
    }
  }
}
