package react4j.downstream;

import gir.sys.SystemProperty;
import java.io.IOException;
import java.util.Properties;
import javax.annotation.Nonnull;
import org.realityforge.gwt.symbolmap.SoycSizeMapsDiff;
import org.realityforge.gwt.symbolmap.SymbolEntryIndexDiff;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class BuildStatsTest
  extends AbstractDownstreamTest
{
  @Test
  public void raw()
    throws Exception
  {
    compareSizesForBranch( "raw" );
  }

  @Test
  public void arez()
    throws Exception
  {
    compareSizesForBranch( "arez" );
  }

  @Test
  public void dagger()
    throws Exception
  {
    compareSizesForBranch( "dagger" );
  }

  @Test
  public void raw_j2cl()
    throws Exception
  {
    compareSizesForBranch( "raw_maven_j2cl" );
  }

  @Test
  public void arez_j2cl()
    throws Exception
  {
    compareSizesForBranch( "arez_maven_j2cl" );
  }

  @Test
  public void dagger_j2cl()
    throws Exception
  {
    compareSizesForBranch( "dagger_maven_j2cl" );
  }

  private void compareSizesForBranch( @Nonnull final String branch )
    throws Exception
  {
    final Properties buildStatistics = loadBuildStatistics();
    final Properties fixtureStatistics = loadFixtureStatistics();

    final long nextVersionFixtureSize =
      extractSize( fixtureStatistics, getNextVersion() + "." + branch );
    final long currentVersionFixtureSize =
      extractSize( fixtureStatistics, getCurrentVersion() + "." + branch );
    final String beforeBuild = branch + ".before";
    final String afterBuild = branch + ".after";
    final long beforeSize = extractSize( buildStatistics, beforeBuild );
    final long afterSize = extractSize( buildStatistics, afterBuild );
    final boolean j2cl = branch.endsWith( "_j2cl" );

    if ( 0 != nextVersionFixtureSize )
    {
      if ( nextVersionFixtureSize != afterSize )
      {
        if ( !j2cl )
        {
          reportSymbolDifferences( beforeBuild, afterBuild );
        }
        fail( "Build size after upgrading react4j in branch '" + branch + "' does not match the build size " +
              "configured for the next version '" + getNextVersion() + "' which was " + nextVersionFixtureSize +
              ". Actual build size is " + afterSize + ". The statistics can be updated by " +
              "running 'buildr update_downstream_build_stats' and committing the changes if the new size " +
              "is acceptable." );
      }
    }
    else if ( 0 != currentVersionFixtureSize )
    {
      if ( currentVersionFixtureSize != afterSize )
      {
        if ( !j2cl )
        {
          reportSymbolDifferences( beforeBuild, afterBuild );
        }
        fail( "Build size after upgrading react4j in branch '" + branch + "' does not match the build size " +
              "configured for the current version '" + getCurrentVersion() + "' which was " +
              currentVersionFixtureSize + ". No build size was specified for the next version. The actual build " +
              "size is " + afterSize + ". The statistics for the next version can be generated by running " +
              "'buildr update_downstream_build_stats' and committing the changes if the new size is acceptable." );
      }
    }
    else
    {
      if ( beforeSize != afterSize )
      {
        if ( !j2cl )
        {
          reportSymbolDifferences( beforeBuild, afterBuild );
        }
        fail( "Build size changed after upgrading react4j in branch '" + branch + "' from " + beforeSize + " to " +
              afterSize + ". The statistics for the next version can be generated by running " +
              "'buildr update_downstream_build_stats' and committing the changes if the new size is acceptable." );
      }
    }
  }

  private void reportSymbolDifferences( @Nonnull final String beforeBuild, @Nonnull final String afterBuild )
    throws Exception
  {
    final SymbolEntryIndexDiff diff =
      SymbolEntryIndexDiff.diff( getSymbolMapIndex( getArchiveDir(), beforeBuild ),
                                 getSymbolMapIndex( getArchiveDir(), afterBuild ) );
    if ( diff.hasDifferences() )
    {
      System.out.println( "Differences detected in symbols compiled between the " +
                          "two variants " + beforeBuild + " and " + afterBuild );
      System.out.println( diff.printToString() );
    }
    final SoycSizeMapsDiff soycDiff =
      SoycSizeMapsDiff.diff( getSoycSizeMaps( getArchiveDir(), beforeBuild ),
                             getSoycSizeMaps( getArchiveDir(), afterBuild ) );
    if ( soycDiff.hasDifferences() )
    {
      System.out.println( "Differences detected in sizes compiled between the " +
                          "two variants " + beforeBuild + " and " + afterBuild );
      System.out.println( soycDiff.printToString() );
    }
  }

  private long extractSize( @Nonnull final Properties properties,
                            @Nonnull final String prefix )
  {
    return Long.parseLong( properties.getProperty( prefix + ".todomvc.size", "0" ) );
  }

  @Nonnull
  private String getCurrentVersion()
  {
    return SystemProperty.get( "react4j.current.version" );
  }

  @Nonnull
  private String getNextVersion()
  {
    return SystemProperty.get( "react4j.next.version" );
  }

  @Nonnull
  private Properties loadBuildStatistics()
    throws IOException
  {
    return loadProperties( getWorkDir().resolve( "statistics.properties" ).toFile() );
  }

  @Nonnull
  private Properties loadFixtureStatistics()
    throws IOException
  {
    return loadProperties( getFixtureDir().resolve( "statistics.properties" ).toFile() );
  }
}
