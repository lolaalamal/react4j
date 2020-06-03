package com.example.post_mount;

import react4j.ReactNode;
import react4j.annotations.PostMount;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class Suppressed1ProtectedAccessPostMountModel
{
  // This uses the SOURCE retention suppression
  @SuppressWarnings( "React4j:ProtectedMethod" )
  @PostMount
  protected void postMount()
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
