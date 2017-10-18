package com.example.render;

import javax.annotation.Nullable;
import react4j.annotations.ReactComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.RenderResult;

@ReactComponent
class BaseRenderComponent
  extends Component<BaseProps, BaseState>
{
  @Nullable
  @Override
  protected RenderResult render()
  {
    return super.render();
  }
}
