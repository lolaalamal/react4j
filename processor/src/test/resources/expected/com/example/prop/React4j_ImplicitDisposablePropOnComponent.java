package com.example.prop;

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

@Generated("react4j.processor.ReactProcessor")
class React4j_ImplicitDisposablePropOnComponent extends ImplicitDisposablePropOnComponent {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( ReactConfig.shouldStoreDebugDataAsState() || ReactConfig.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "ImplicitDisposablePropOnComponent" );
    }
    return componentConstructor;
  }

  @Override
  protected ImplicitDisposablePropOnComponent.Model getModel() {
    if ( ReactConfig.shouldCheckInvariants() ) {
      return null != props().getAny( Props.model ) ? props().getAny( Props.model ).cast() : null;
    } else {
      return Js.uncheckedCast( props().getAny( Props.model ) );
    }
  }

  void $$react4j$$_componentDidMount() {
    storeDebugDataAsState();
  }

  final void $$react4j$$_componentDidUpdate(@Nullable final JsPropertyMap<Object> prevProps) {
    storeDebugDataAsState();
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Props {
    static final String model = ReactConfig.shouldMinimizePropKeys() ? "a" : "model";
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface Lifecycle {
    void componentDidMount();

    void componentDidUpdate(@Nonnull JsPropertyMap<Object> prevProps);
  }

  private static final class LiteNativeReactComponent extends NativeAdapterComponent<ImplicitDisposablePropOnComponent> {
    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ImplicitDisposablePropOnComponent createComponent() {
      return new React4j_ImplicitDisposablePropOnComponent();
    }
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<ImplicitDisposablePropOnComponent> implements Lifecycle {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected ImplicitDisposablePropOnComponent createComponent() {
      return new React4j_ImplicitDisposablePropOnComponent();
    }

    @Override
    public void componentDidMount() {
      ((React4j_ImplicitDisposablePropOnComponent) component() ).$$react4j$$_componentDidMount();
    }

    @Override
    public void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevProps) {
      ((React4j_ImplicitDisposablePropOnComponent) component() ).$$react4j$$_componentDidUpdate( prevProps );
    }
  }
}
