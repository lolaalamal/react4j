package com.example.prop;

import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.React;
import react4j.ReactNode;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeAdapterComponent;
import react4j.internal.NativeComponent;

@ArezComponent(
    name = "SingleChildPropComponent",
    disposeTrackable = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_SingleChildPropComponent extends SingleChildPropComponent {
  React4j_SingleChildPropComponent(@Nonnull final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "SingleChildPropComponent" );
    }
    return componentConstructor;
  }

  @Override
  protected ReactNode getChild() {
    if ( React.shouldCheckInvariants() ) {
      return null != props().getAny( Props.child ) ? props().getAny( Props.child ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.child ) );
    }
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String child = "children";
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<SingleChildPropComponent> {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected SingleChildPropComponent createComponent() {
      return new Arez_React4j_SingleChildPropComponent( this );
    }
  }
}
