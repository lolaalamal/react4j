package com.example.prop_validate;

import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.React;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeAdapterComponent;
import react4j.internal.NativeComponent;
import react4j.internal.OnShouldComponentUpdate;

@ArezComponent(
    name = "DoublePropValidate",
    disposeTrackable = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_DoublePropValidate extends DoublePropValidate {
  React4j_DoublePropValidate(@Nonnull final NativeComponent nativeComponent) {
    bindComponent( nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "DoublePropValidate" );
    }
    return componentConstructor;
  }

  @Override
  protected double getMyProp() {
    return props().getAny( Props.myProp ).asDouble();
  }

  @Override
  protected final void validatePropValues(@Nonnull final JsPropertyMap<Object> props) {
    final Object raw$myProp = props.get( Props.myProp );
    if ( null != raw$myProp ) {
      final double typed$myProp = Js.asDouble( raw$myProp );
      validateMyProp( typed$myProp );
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextProps) {
    assert null != nextProps;
    if ( React.shouldValidatePropValues() ) {
      validatePropValues( nextProps );
    }
    final JsPropertyMap<Object> props = props();
    if ( !Js.isTripleEqual( props.get( Props.myProp ), nextProps.get( Props.myProp ) ) ) {
      return true;
    }
    return false;
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String myProp = React.shouldMinimizePropKeys() ? "a" : "myProp";
  }

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<DoublePropValidate> {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected DoublePropValidate createComponent() {
      return new Arez_React4j_DoublePropValidate( this );
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<DoublePropValidate> implements OnShouldComponentUpdate {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected DoublePropValidate createComponent() {
      return new Arez_React4j_DoublePropValidate( this );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return ((React4j_DoublePropValidate) component() ).$$react4j$$_shouldComponentUpdate( nextProps );
    }
  }
}
