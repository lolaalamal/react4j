package com.example.prop_validate;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class PrivatePropValidate
  extends Component
{
  @PropValidate
  private void validateMyProp( String prop )
  {
  }

  @Prop
  protected abstract String getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
