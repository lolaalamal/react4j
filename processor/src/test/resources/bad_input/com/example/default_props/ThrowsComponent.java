package com.example.default_props;

import javax.annotation.Nonnull;
import react4j.annotations.ReactComponent;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
class ThrowsComponent
  extends Component<BaseProps, BaseState, BaseContext>
{
  @Nonnull
  static BaseProps getInitialProps()
    throws Exception
  {
    return new BaseProps();
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
