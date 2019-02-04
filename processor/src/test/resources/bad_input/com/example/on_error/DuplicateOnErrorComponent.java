package com.example.on_error;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnError;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class DuplicateOnErrorComponent
  extends Component
{
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @OnError
  void onError1()
  {
  }

  @OnError
  void onError2()
  {
  }
}