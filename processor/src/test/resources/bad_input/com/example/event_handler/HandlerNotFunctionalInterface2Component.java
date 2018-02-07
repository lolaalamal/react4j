package com.example.event_handler;

import jsinterop.annotations.JsFunction;
import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.core.BaseContext;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class HandlerNotFunctionalInterface2Component
  extends Component<BaseState, BaseContext>
{
  @JsFunction
  interface Foo
  {
    default void foo()
    {
    }

    default void foo2()
    {
    }
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }

  @EventHandler( Foo.class )
  void handleFoo()
  {
  }
}
