package com.example.prop;

import java.util.HashSet;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class CollectionHashSetPropModel
{
  @Prop
  abstract HashSet<String> getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
