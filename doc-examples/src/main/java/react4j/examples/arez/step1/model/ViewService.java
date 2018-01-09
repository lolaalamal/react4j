package react4j.examples.arez.step1.model;

import arez.annotations.ArezComponent;
import arez.annotations.Observable;
import java.util.Objects;
import javax.annotation.Nonnull;

@ArezComponent( nameIncludesId = false )
public class ViewService
{
  @Nonnull
  private FilterMode _filterMode = FilterMode.ALL;

  ViewService()
  {
  }

  @Observable
  @Nonnull
  public FilterMode getFilterMode()
  {
    return _filterMode;
  }

  public void setFilterMode( @Nonnull final FilterMode filterMode )
  {
    _filterMode = Objects.requireNonNull( filterMode );
  }
}
