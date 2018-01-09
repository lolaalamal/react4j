package com.example.arez;

import javax.annotation.Nullable;
import arez.annotations.Action;
import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ReactNode;

@ReactComponent
class AnnotatedEventHandler
  extends ReactArezComponent<BaseProps, BaseContext>
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @EventHandler
  @Action
  void handleFoo()
  {
  }
}
