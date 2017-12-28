package com.example.arez;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import org.realityforge.arez.annotations.ArezComponent;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.React;
import react4j.core.ReactConfig;
import react4j.core.ReactNode;

@ArezComponent(
    type = "OverridingComponentDidUpdateComponent",
    deferSchedule = true
)
@Generated("react4j.processor.ReactProcessor")
class OverridingComponentDidUpdateComponent_ extends OverridingComponentDidUpdateComponent {
  private static final ComponentConstructorFunction<BaseProps, BaseContext> TYPE = getConstructorFunction();

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
      Js.asPropertyMap( componentConstructor ).set( "displayName", "OverridingComponentDidUpdateComponent" );
    }
    return componentConstructor;
  }

  @JsType(
      isNative = true
  )
  interface Lifecycle {
    void componentDidMount();

    void componentDidUpdate(@Nonnull BaseProps nextProps, @Nonnull BaseState nextState);

    void componentWillUnmount();

    boolean shouldComponentUpdate(@Nonnull BaseProps arg0, @Nonnull BaseState arg1,
        @Nonnull BaseContext arg2);
  }

  static final class NativeReactComponent extends NativeAdapterComponent<BaseProps, BaseState, BaseContext, OverridingComponentDidUpdateComponent> implements Lifecycle {
    NativeReactComponent(@Nullable final BaseProps props, @Nullable final BaseContext context) {
      super( props, context );
    }

    @Override
    protected OverridingComponentDidUpdateComponent createComponent() {
      return new Arez_OverridingComponentDidUpdateComponent_();
    }

    @Override
    public void componentDidMount() {
      performComponentDidMount();
    }

    @Override
    public void componentDidUpdate(@Nonnull final BaseProps nextProps,
        @Nonnull final BaseState nextState) {
      performComponentDidUpdate(nextProps,nextState);
    }

    @Override
    public void componentWillUnmount() {
      performComponentWillUnmount();
    }

    @Override
    public boolean shouldComponentUpdate(@Nonnull final BaseProps arg0,
        @Nonnull final BaseState arg1, @Nonnull final BaseContext arg2) {
      return performShouldComponentUpdate(arg0,arg1,arg2);
    }
  }
}
