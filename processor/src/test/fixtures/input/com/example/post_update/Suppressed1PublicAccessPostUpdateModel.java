package com.example.post_update;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostUpdate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class Suppressed1PublicAccessPostUpdateModel
  extends Component
{
  // This uses the SOURCE retention suppression
  @SuppressWarnings( "React4j:PublicLifecycleMethod" )
  @PostUpdate
  public void postUpdate()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
