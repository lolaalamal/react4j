package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;
import react4j.core.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class React4j_MultiPropComponent2 extends MultiPropComponent2 {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "MultiPropComponent2" );
    }
    return componentConstructor;
  }

  @Override
  protected String getMyProp() {
    return props().has( "myProp" ) ? props().getAny( "myProp" ).asString() : null;
  }

  @Override
  protected String getMyProp2() {
    return props().has( "myProp2" ) ? props().getAny( "myProp2" ).asString() : null;
  }

  @Override
  protected ReactNode[] getChildren() {
    return props().has( "children" ) ? props().getAny( "children" ).cast() : null;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<MultiPropComponent2> {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected MultiPropComponent2 createComponent() {
      return new React4j_MultiPropComponent2();
    }
  }
}
