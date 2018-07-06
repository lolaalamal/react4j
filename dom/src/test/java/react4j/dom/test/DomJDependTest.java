package react4j.dom.test;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import jdepend.framework.DependencyConstraint;
import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;
import jdepend.framework.PackageFilter;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class DomJDependTest
{
  @Test
  public void dependencyAnalysis()
    throws Exception
  {
    final JDepend jdepend = new JDepend( PackageFilter.all().excluding( "java.*", "javax.*" ) );
    jdepend.addDirectory( compileTargetDir() );
    jdepend.analyze();

    final DependencyConstraint constraint = new DependencyConstraint();

    final JavaPackage react4j = constraint.addPackage( "react4j" );
    final JavaPackage react4jDom = constraint.addPackage( "react4j.dom" );
    final JavaPackage react4jDomEvents = constraint.addPackage( "react4j.dom.events" );
    final JavaPackage react4jDomCssPropTypes = constraint.addPackage( "react4j.dom.proptypes.cssPropertyTypes" );
    final JavaPackage react4jDomHtml = constraint.addPackage( "react4j.dom.proptypes.html" );
    final JavaPackage react4jDomAttributeTypes = constraint.addPackage( "react4j.dom.proptypes.html.attributeTypes" );
    final JavaPackage braincheck = constraint.addPackage( "org.realityforge.braincheck" );
    final JavaPackage jsinteropAnnotations = constraint.addPackage( "jsinterop.annotations" );
    final JavaPackage jsinteropBase = constraint.addPackage( "jsinterop.base" );
    final JavaPackage elemental2Core = constraint.addPackage( "elemental2.core" );
    final JavaPackage elemental2Dom = constraint.addPackage( "elemental2.dom" );

    react4jDom.dependsUpon( react4j );
    react4jDom.dependsUpon( jsinteropAnnotations );
    react4jDom.dependsUpon( braincheck );
    react4jDom.dependsUpon( elemental2Dom );
    react4jDom.dependsUpon( react4jDomHtml );

    react4jDomEvents.dependsUpon( jsinteropAnnotations );
    react4jDomEvents.dependsUpon( elemental2Core );
    react4jDomEvents.dependsUpon( elemental2Dom );

    react4jDomHtml.dependsUpon( react4j );
    react4jDomHtml.dependsUpon( jsinteropAnnotations );
    react4jDomHtml.dependsUpon( jsinteropBase );
    react4jDomHtml.dependsUpon( react4jDomCssPropTypes );
    react4jDomHtml.dependsUpon( react4jDomEvents );
    react4jDomHtml.dependsUpon( react4jDomAttributeTypes );

    final DependencyConstraint.MatchResult result = jdepend.analyzeDependencies( constraint );

    assertEquals( result.getUndefinedPackages().size(), 0, "Undefined Packages: " + result.getUndefinedPackages() );

    assertTrue( result.matches(),
                "NonMatchingPackages: " +
                result.getNonMatchingPackages().stream().map( Arrays::asList ).collect( Collectors.toList() ) );
  }

  @Nonnull
  private String compileTargetDir()
  {
    final String fixtureDir = System.getProperty( "react4j.dom.compile_target" );
    assertNotNull( fixtureDir, "Expected System.getProperty( \"react4j.dom.compile_target\" ) to return directory" );
    return new File( fixtureDir ).getAbsolutePath();
  }
}
