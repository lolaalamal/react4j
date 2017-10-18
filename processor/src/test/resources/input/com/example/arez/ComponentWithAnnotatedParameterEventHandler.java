package com.example.arez;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsFunction;
import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.RenderResult;

@ReactComponent
class ComponentWithAnnotatedParameterEventHandler
  extends ReactArezComponent<BaseProps, BaseState>
{
  @JsFunction
  public interface CustomHandler
  {
    void onMyEvent( @Nonnull String i );
  }

  @JsFunction
  public interface CustomHandler2
  {
    @Nonnull
    String onMyEvent();
  }

  @Nullable
  @Override
  protected RenderResult render()
  {
    return null;
  }

  @EventHandler( CustomHandler2.class )
  @Nonnull
  String handleFoo()
  {
    return "";
  }

  @EventHandler( CustomHandler.class )
  void handleFoo2( @Nonnull String i )
  {
  }
}
