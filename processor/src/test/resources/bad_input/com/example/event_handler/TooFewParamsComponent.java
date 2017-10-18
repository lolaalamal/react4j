package com.example.event_handler;

import jsinterop.annotations.JsFunction;
import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.RenderResult;

@ReactComponent
class TooFewParamsComponent
  extends Component<BaseProps, BaseState>
{
  @JsFunction
  public interface CustomHandler
  {
    void onMouseEvent( Object o1, Object o2 );
  }

  @Override
  protected RenderResult render()
  {
    return null;
  }

  @EventHandler( CustomHandler.class )
  void handleFoo( int i )
  {
  }
}
