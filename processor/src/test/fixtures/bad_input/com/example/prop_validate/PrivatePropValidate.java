package com.example.prop_validate;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputValidate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class PrivatePropValidate
{
  @InputValidate
  private void validateMyProp( String prop )
  {
  }

  @Input
  protected abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
