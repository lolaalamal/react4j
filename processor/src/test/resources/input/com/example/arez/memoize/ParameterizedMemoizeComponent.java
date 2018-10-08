package com.example.arez.memoize;

import arez.annotations.Memoize;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;

@ReactComponent
abstract class ParameterizedMemoizeComponent
  extends ReactArezComponent
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Memoize( name = "foo", observeLowerPriorityDependencies = true, requireEnvironment = true )
  String getIcon( String key )
  {
    return null;
  }
}
