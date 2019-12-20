package com.example.prop;

import java.util.ArrayList;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class CollectionArrayListPropModel
  extends Component
{
  @Prop
  abstract ArrayList<String> getMyProp();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}