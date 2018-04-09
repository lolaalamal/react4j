package com.example.inject;

import javax.inject.Inject;
import react4j.annotations.Feature;
import react4j.annotations.ReactComponent;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent( dagger = Feature.ENABLE )
abstract class GenericTypeInjectedComponent<T>
  extends Component
{
  @Inject
  String someParam;

  @Override
  protected ReactNode render()
  {
    return null;
  }
}