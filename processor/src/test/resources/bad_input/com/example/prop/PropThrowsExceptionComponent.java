package com.example.prop;

import java.io.IOException;
import javax.annotation.Nullable;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.core.BaseContext;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class PropThrowsExceptionComponent
  extends Component<BaseState, BaseContext>
{
  @Prop
  protected abstract String getMyProp()
    throws IOException;

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
