package com.example.lifecycle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react.annotations.ReactComponent;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.Component;
import react.core.ReactElement;

@ReactComponent
class OverrideLifecycleMethodsComponent
  extends Component<BaseProps, BaseState>
{
  @Override
  protected ReactElement<?, ?> render()
  {
    return null;
  }

  @Override
  protected final void componentDidUpdate( @Nullable final BaseProps nextProps, @Nullable final BaseState nextState )
  {
  }

  @Override
  protected void componentWillMount()
  {
  }

  @Override
  protected void componentWillReceiveProps( @Nonnull final BaseProps nextProps )
  {
  }

  @Override
  protected void componentWillUnmount()
  {
  }

  @Override
  protected void componentWillUpdate( @Nullable final BaseProps nextProps, @Nullable final BaseState nextState )
  {
  }

  @Override
  public boolean shouldComponentUpdate( @Nullable final BaseProps nextProps, @Nullable final BaseState nextState )
  {
    return super.shouldComponentUpdate( nextProps, nextState );
  }
}
