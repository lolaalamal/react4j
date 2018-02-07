package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseContext;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;
import react4j.core.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class MultiPropComponent3_ extends MultiPropComponent3 {
  static final ComponentConstructorFunction<BaseContext> TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction<BaseContext> getConstructorFunction() {
    final ComponentConstructorFunction<BaseContext> componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "MultiPropComponent3" );
    }
    return componentConstructor;
  }

  @Override
  protected String getMyProp() {
    return Js.asPropertyMap( props() ).getAny( "myProp" ).asString();
  }

  @Override
  protected String getMyProp2() {
    return Js.asPropertyMap( props() ).getAny( "myProp2" ).asString();
  }

  @Override
  protected ReactNode getChild() {
    return Js.asPropertyMap( props() ).getAny( "child" ).cast();
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BaseState, BaseContext, MultiPropComponent3> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props,
        @Nullable final BaseContext context) {
      super( props, context );
    }

    @Override
    protected MultiPropComponent3 createComponent() {
      return new MultiPropComponent3_();
    }
  }
}
