package com.example.prop;

import arez.annotations.ArezComponent;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.ComponentConstructorFunction;
import react4j.NativeAdapterComponent;
import react4j.ReactConfig;

@ArezComponent(
    name = "NonDisposableDisposableProp"
)
@Generated("react4j.processor.ReactProcessor")
abstract class React4j_NonDisposableDisposableProp extends NonDisposableDisposableProp {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  static final String PROP_model = ReactConfig.shouldMinimizePropKeys() ? "a" : "model";

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( ReactConfig.shouldStoreDebugDataAsState() || ReactConfig.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "NonDisposableDisposableProp" );
    }
    return componentConstructor;
  }

  @Override
  protected NonDisposableDisposableProp.Model getModel() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( PROP_model ) ? props().getAny( PROP_model ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( PROP_model ) );
    }
  }

  @Override
  protected boolean shouldUpdateOnPropChanges(@Nullable final JsPropertyMap<Object> nextProps) {
    if ( !Js.isTripleEqual( props().get( PROP_model ), null == nextProps ? Js.undefined() : nextProps.get( PROP_model ) ) ) {
      return true;
    }
    return false;
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface LiteLifecycle {
    void componentWillUnmount();
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface Lifecycle {
    void componentDidMount();

    void componentDidUpdate(@Nonnull JsPropertyMap<Object> arg0);

    void componentWillUnmount();
  }

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<NonDisposableDisposableProp> implements LiteLifecycle {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected NonDisposableDisposableProp createComponent() {
      return new Arez_React4j_NonDisposableDisposableProp();
    }

    @Override
    public void componentWillUnmount() {
      performComponentWillUnmount();
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<NonDisposableDisposableProp> implements Lifecycle {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected NonDisposableDisposableProp createComponent() {
      return new Arez_React4j_NonDisposableDisposableProp();
    }

    @Override
    public void componentDidMount() {
      performComponentDidMount();
    }

    @Override
    public void componentDidUpdate(@Nonnull final JsPropertyMap<Object> arg0) {
      performComponentDidUpdate(arg0);
    }

    @Override
    public void componentWillUnmount() {
      performComponentWillUnmount();
    }
  }
}
