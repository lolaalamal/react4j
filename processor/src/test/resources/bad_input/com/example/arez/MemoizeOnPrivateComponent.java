package com.example.arez;

import arez.annotations.Memoize;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent( type = ReactComponent.Type.TRACKING )
abstract class MemoizeOnPrivateComponent
  extends Component
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Memoize
  private String getIcon( String key )
  {
    return null;
  }
}
