package com.example.schedule_render;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.ScheduleRender;

@ReactComponent
abstract class SkipScheduleRenderComponent
  extends Component
{
  @SuppressWarnings( "DefaultAnnotationParam" )
  @ScheduleRender( skipShouldComponentUpdate = true )
  abstract void myScheduleRender();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}