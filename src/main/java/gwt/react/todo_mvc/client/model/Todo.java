package gwt.react.todo_mvc.client.model;

import java.util.Objects;
import javax.annotation.Nonnull;
import org.realityforge.arez.annotations.Action;
import org.realityforge.arez.annotations.Container;
import org.realityforge.arez.annotations.ContainerId;
import org.realityforge.arez.annotations.Observable;

@Container
public class Todo
{
  @Nonnull
  private String _id;
  @Nonnull
  private String _title;
  private boolean _completed;

  Todo( @Nonnull final String id, @Nonnull final String title, final boolean completed )
  {
    _id = Objects.requireNonNull( id );
    _title = Objects.requireNonNull( title );
    _completed = completed;
  }

  @ContainerId
  @Nonnull
  public final String getId()
  {
    return _id;
  }

  @Observable
  @Nonnull
  public String getTitle()
  {
    return _title;
  }

  public void setTitle( @Nonnull final String title )
  {
    _title = Objects.requireNonNull( title );
  }

  @Observable
  public boolean isCompleted()
  {
    return _completed;
  }

  public void setCompleted( final boolean completed )
  {
    _completed = completed;
  }

  @Action
  public void toggle()
  {
    setCompleted( !isCompleted() );
  }
}
