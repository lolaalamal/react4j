package com.example.on_error;

import elemental2.core.JsError;
import javax.annotation.Nonnull;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.annotations.OnError;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class AbstractOnErrorComponent
{
  @Render
  ReactNode render()
  {
    return null;
  }

  @OnError
  abstract void onError( @Nonnull JsError error, @Nonnull ReactErrorInfo info );
}
