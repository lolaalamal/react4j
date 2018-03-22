package com.example.state;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class React4j_NullabilityAnnotations extends NullabilityAnnotations {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "NullabilityAnnotations" );
    }
    return componentConstructor;
  }

  @Nonnull
  @Override
  protected String getMyValue() {
    return state().getAny( "myValue" ).asString();
  }

  @Override
  protected void setMyValue(@Nonnull final String value) {
    scheduleStateUpdate( ( ( previousState, currentProps ) -> JsPropertyMap.of( "myValue", value ) ) );
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<NullabilityAnnotations> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected NullabilityAnnotations createComponent() {
      return new React4j_NullabilityAnnotations();
    }
  }
}