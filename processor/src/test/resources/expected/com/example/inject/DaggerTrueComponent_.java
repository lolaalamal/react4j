package com.example.inject;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import jsinterop.base.Js;
import org.realityforge.braincheck.Guards;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.React;
import react4j.core.ReactConfig;
import react4j.core.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class DaggerTrueComponent_ extends DaggerTrueComponent {
  static final ComponentConstructorFunction<BaseProps, BaseContext> TYPE = getConstructorFunction();

  private static Provider<DaggerTrueComponent> c_provider;

  @Inject
  DaggerTrueComponent_() {
  }

  static void setProvider(final Provider<DaggerTrueComponent> provider) {
    c_provider = provider;
  }

  private static Provider<DaggerTrueComponent> getProvider() {
    Guards.invariant( () -> null != c_provider, () -> "Attempted to create an instance of the React4j component named 'DaggerTrueComponent' before the dependency injection provider has been initialized. Please see the documentation at https://react4j.github.io/dependency_injection for directions how to configure dependency injection." );
    return c_provider;
  }

  @Nonnull
  static ReactNode _create() {
    return React.createElement( TYPE );
  }

  @Nonnull
  static ReactNode _create(@Nullable final BaseProps props) {
    return React.createElement( TYPE, props );
  }

  @Nonnull
  static ReactNode _create(@Nullable final BaseProps props, @Nullable final ReactNode child) {
    return React.createElement( TYPE, props, child );
  }

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseContext> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseContext> componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "DaggerTrueComponent" );
    }
    return componentConstructor;
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BaseProps, BaseState, BaseContext, DaggerTrueComponent> {
    NativeReactComponent(@Nullable final BaseProps props, @Nullable final BaseContext context) {
      super( props, context );
    }

    @Override
    protected DaggerTrueComponent createComponent() {
      return getProvider().get();
    }
  }
}
