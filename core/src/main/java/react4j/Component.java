package react4j;

import elemental2.core.JsError;
import elemental2.core.JsObject;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Any;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.annotations.Prop;
import static org.realityforge.braincheck.Guards.*;

/**
 * The base java class that mirrors the react component.
 */
public abstract class Component
{
  @Nonnull
  private ComponentPhase _phase = ComponentPhase.INITIALIZING;
  @Nonnull
  private LifecycleMethod _lifecycleMethod = LifecycleMethod.UNKNOWN;
  @Nullable
  private NativeComponent _nativeComponent;
  /**
   * If the last render resulted in state update to record new debug state then this will be true.
   * It guards against multiple renders of a single component where rendering is just updating
   * react state to expose debug data. This should only be true if {@link ReactConfig#shouldStoreDebugDataAsState()}
   * returns true.
   */
  private boolean _scheduledDebugStateUpdate;

  /**
   * Set the phase of the component. Only used for invariant checking.
   */
  final void setPhase( @Nonnull final ComponentPhase phase )
  {
    if ( ReactConfig.shouldCheckInvariants() )
    {
      invariant( ReactConfig::checkComponentStateInvariants,
                 () -> "Component.setComponentPhase() invoked on " + this +
                       " when ReactConfig.checkComponentStateInvariants() is false" );
    }
    _phase = Objects.requireNonNull( phase );
  }

  /**
   * Set the current lifecycle method of the component. Only used for invariant checking.
   */
  final void setLifecycleMethod( @Nonnull final LifecycleMethod lifecycleMethod )
  {
    if ( ReactConfig.shouldCheckInvariants() )
    {
      invariant( ReactConfig::checkComponentStateInvariants,
                 () -> "Component.setLifecycleMethod() invoked on " + this +
                       " when ReactConfig.checkComponentStateInvariants() is false" );
    }
    _lifecycleMethod = Objects.requireNonNull( lifecycleMethod );
  }

  final void bindComponent( @Nonnull final NativeComponent nativeComponent )
  {
    _nativeComponent = Objects.requireNonNull( nativeComponent );
  }

  /**
   * Return the native react component.
   */
  @Nonnull
  private NativeComponent component()
  {
    if ( ReactConfig.shouldCheckInvariants() )
    {
      invariant( () -> null != _nativeComponent,
                 () -> "Invoked component() on " + this + " before a component has been bound." );
    }
    assert null != _nativeComponent;
    return _nativeComponent;
  }

  /**
   * Return the component state from the native component.
   * This may be null if initial state was never set.
   *
   * @return the component state.
   */
  protected final JsPropertyMap<Object> state()
  {
    return component().state();
  }

  /**
   * Return the component props from the native component.
   * This may be null if no props were supplied.
   *
   * @return the component state.
   */
  protected final JsPropertyMap<Object> props()
  {
    return component().props();
  }

  /**
   * Return the key associated with the component if any.
   * The key is used by the react reconcilliation process as a means to identify components so that they
   * can be moved rather than recreated or reconfigured. Typically, it should not be used by the component
   * itself except for generating keys of sub-components. This value may not be specified in which case null
   * will be returned.
   *
   * @return the key if specified.
   */
  @Nullable
  protected final String getKey()
  {
    return Js.asPropertyMap( props() ).getAny( "key" ).asString();
  }

  /**
   * Schedule a shallow merge of supplied state into current state.
   *
   * @param state the state to merge.
   */
  private void scheduleStateUpdate( @Nonnull JsPropertyMap<Object> state )
  {
    invariantsSetState();
    component().setState( state );
  }

  /**
   * Check invariants that should be true when invoking setState()
   */
  private void invariantsSetState()
  {
    if ( ReactConfig.shouldCheckInvariants() && ReactConfig.checkComponentStateInvariants() )
    {
      apiInvariant( () -> LifecycleMethod.RENDER != _lifecycleMethod,
                    () -> "Incorrectly invoked scheduleStateUpdate() on " + this + " in scope of render()." );
      apiInvariant( () -> ComponentPhase.UNMOUNTING != _phase,
                    () -> "Incorrectly invoked scheduleStateUpdate() on " + this + " when component is " +
                          "unmounting or has unmounted." );
    }
  }

  /**
   * Schedule this component for re-rendering.
   * The component re-renders when props change but calling this method is another way to schedule the
   * component to be re-rendered. When this method is called the {@link #performShouldComponentUpdate(JsPropertyMap)}
   * lifecycle method will be skipped. Calling this method is equivalent to calling forceUpdate() on the native
   * react component. See the <a href="https://reactjs.org/docs/react-component.html#forceupdate">React Component
   * documentation</a> for more details.
   */
  protected final void scheduleRender()
  {
    if ( ReactConfig.shouldCheckInvariants() && ReactConfig.checkComponentStateInvariants() )
    {
      apiInvariant( () -> ComponentPhase.UNMOUNTING != _phase,
                    () -> "Incorrectly invoked scheduleRender() on " + this + " when component is " +
                          "unmounting or has unmounted." );
    }
    component().forceUpdate();
  }

  /**
   * Render the component.
   * See the <a href="https://reactjs.org/docs/react-component.html#render">React Component documentation</a> for more details.
   *
   * @return the result of rendering.
   */
  @Nullable
  protected abstract ReactNode render();

  /**
   * Wrapper method that delegates to the {@link #render()} method.
   * This method exists to give middleware a mechanism to hook into component lifecycle step.
   *
   * @return the result of rendering.
   */
  @Nullable
  protected ReactNode performRender()
  {
    return render();
  }

  /**
   * Wrapper method that delegates to the {@link #postConstruct()} method.
   * This method exists to give middleware a mechanism to hook into component lifecycle step.
   */
  protected void performPostConstruct()
  {
    final JsPropertyMap<Object> props = props();
    if ( ReactConfig.shouldValidatePropValues() && null != props )
    {
      validatePropValues( props );
    }
    postConstruct();
  }

  /**
   * This method is invoked after the component is constructed and bound to a native react component.
   * This is a good place to perform initialization. It is called before {@link #render()}, therefore
   * setting state in this method will not trigger a re-rendering. This replaces the
   * <a href="https://reactjs.org/docs/react-component.html#componentwillmount">componentWillMount</a>
   * lifecycle method from react as well as the code that appears in constructors in native React ES6
   * components.
   */
  protected void postConstruct()
  {
  }

  /**
   * This method is invoked after a component is attached to the DOM.
   * Initialization that requires DOM nodes should go here.
   * Setting state in this method will trigger a re-rendering.
   * See the <a href="https://reactjs.org/docs/react-component.html#componentdidmount">React Component documentation</a> for more details.
   */
  protected void componentDidMount()
  {
  }

  /**
   * Wrapper method that delegates to the {@link #componentDidMount()} method.
   */
  final void performComponentDidMount()
  {
    componentDidMount();
    storeDebugDataAsState();
  }

  /**
   * This method is invoked immediately after updating occurs.
   * If you need to interact with the DOM after the component has been updated.
   * See the <a href="https://reactjs.org/docs/react-component.html#componentdidupdate">React Component documentation</a> for more details.
   *
   * @param prevProps the props before the component was updated.
   */
  protected void componentDidUpdate( @Nullable final JsPropertyMap<Object> prevProps )
  {
  }

  /**
   * Wrapper method that delegates to the {@link #componentDidUpdate(JsPropertyMap)} method.
   *
   * @param prevProps the props before the component was updated.
   */
  final void performComponentDidUpdate( @Nullable final JsPropertyMap<Object> prevProps )
  {
    componentDidUpdate( prevProps );
    storeDebugDataAsState();
  }

  /**
   * This method is invoked immediately before a component is unmounted and destroyed.
   * Perform any necessary cleanup in this method, such as invalidating timers, canceling network requests, or cleaning up
   * any DOM elements that were created in {@link #componentDidMount()}
   * See the <a href="https://reactjs.org/docs/react-component.html#componentwillunmount">React Component documentation</a> for more details.
   */
  protected void componentWillUnmount()
  {
  }

  /**
   * Wrapper method that delegates to the {@link #componentWillUnmount()} method.
   * This method exists to give middleware a mechanism to hook into component lifecycle step.
   */
  protected void performComponentWillUnmount()
  {
    componentWillUnmount();
  }

  /**
   * The componentDidCatch() method works like a JavaScript catch {} block, but for components.
   * Only class components can be error boundaries. In practice, most of the time you’ll want to
   * declare an error boundary component once and use it throughout your application.
   *
   * <p>Note that error boundaries only catch errors in the components below them in the tree. An
   * error boundary can’t catch an error within itself. If an error boundary fails trying to render
   * the error message, the error will propagate to the closest error boundary above it. This, too,
   * is similar to how catch {} block works in JavaScript.</p>
   *
   * @param error the error that has been thrown.
   * @param info  information about component stack during thrown error.
   */
  protected void componentDidCatch( @Nonnull final JsError error, @Nonnull final ReactErrorInfo info )
  {
  }

  /**
   * Wrapper method that delegates to the {@link #componentDidCatch(JsError, ReactErrorInfo)} method.
   * This method exists to give middleware a mechanism to hook into component lifecycle step.
   *
   * @param error the error that has been thrown.
   * @param info  information about component stack during thrown error.
   */
  final void performComponentDidCatch( @Nonnull final JsError error, @Nonnull final ReactErrorInfo info )
  {
    componentDidCatch( error, info );
  }

  /**
   * Detect changes in props that that are require specific actions on change.
   * This method is a template method that may be overridden by subclasses generated
   * by the annotation processor based on configuration of props.
   *
   * <p>Note: This is required when the component is an arez component and the props
   * are marked as {@link Prop#observable()} and are used by a <code>@Computed</code>
   * method from within the render call. This method gives an opportunity to mark
   * the props as changed so that the computed is marked as potentially stale, so that
   * it will recomputed next time it is invoked which may be the render. If this template
   * method did not exist then the change would not reported until after the render occurred
   * in the {@link #componentDidUpdate(JsPropertyMap)} which would result in a re-render of
   * the component.</p>
   *
   * @param props     the old properties of the component.
   * @param nextProps the new properties of the component.
   * @return true if a prop was marked with {@link Prop#shouldUpdateOnChange()} and has changed.
   */
  protected boolean notifyOnPropChanges( @Nonnull final JsPropertyMap<Object> props,
                                         @Nullable final JsPropertyMap<Object> nextProps )
  {
    return false;
  }

  /**
   * Detect changes in props that that do not require specific actions on change.
   * This method may be overridden by the annotation processor. The method will return true if a prop has been updated
   * and the prop has not set {@link Prop#shouldUpdateOnChange()} to false. Otherwise this method will return false.
   *
   * @param nextProps the new properties of the component.
   * @return true if the component should be updated.
   */
  protected boolean shouldUpdateOnPropChanges( @Nullable final JsPropertyMap<Object> nextProps )
  {
    return false;
  }

  /**
   * This method let's React know if a component's output is not affected
   * by the current change in props. The default behavior is to re-render on
   * every state change, and in the vast majority of cases you should rely on the default behavior.
   * See the <a href="https://reactjs.org/docs/react-component.html#shouldcomponentupdate">React Component documentation</a> for more details.
   *
   * <p>This method is invoked before rendering when new props or state are being received.
   * This method is not called for the initial render or when {@link #scheduleRender()}.</p>
   *
   * <p>Returning false does not prevent child components from re-rendering when their state changes.</p>
   *
   * <p>If this method returns false, then {@link #render()}, and {@link #componentDidUpdate(JsPropertyMap)}
   * will not be invoked. In the future React may treat this method  as a hint rather than a strict directive, and
   * returning false may still result in a re-rendering of the component.</p>
   *
   * @param nextProps the new properties of the component.
   * @return true if the component should be updated.
   */
  final boolean performShouldComponentUpdate( @Nullable final JsPropertyMap<Object> nextProps )
  {
    if ( ReactConfig.shouldValidatePropValues() && null != nextProps )
    {
      validatePropValues( nextProps );
    }
    return notifyOnPropChanges( props(), nextProps ) || shouldUpdateOnPropChanges( nextProps );
  }

  /**
   * Perform validation on props supplied to the component.
   *
   * @param props the props of the component.
   */
  protected void validatePropValues( @Nonnull final JsPropertyMap<Object> props )
  {
  }

  /**
   * Store debug data on the state object of the native component.
   * This is only done if {@link ReactConfig#shouldStoreDebugDataAsState()} returns true and is primarily
   * done to make it easy to debug the component from within React DevTools.
   */
  private void storeDebugDataAsState()
  {
    if ( ReactConfig.shouldStoreDebugDataAsState() )
    {
      if ( _scheduledDebugStateUpdate )
      {
        _scheduledDebugStateUpdate = false;
      }
      else
      {
        final JsPropertyMap<Object> newState = JsPropertyMap.of();
        populateDebugData( newState );

        final JsPropertyMap<Object> state = state();
        final JsPropertyMap<Object> currentState = null == state ? null : Js.asPropertyMap( state );
        /*
         * To determine whether we need to do a state update we do compare each key and value and make sure
         * they match. In some cases keys can be removed (i.e. a dependency is no longer observed) but as state
         * updates in react are merges, we need to implement this by putting undefined values into the state.
         */
        if ( null == currentState )
        {
          scheduleDebugStateUpdate( newState );
        }
        else
        {
          for ( final String key : JsObject.keys( Js.uncheckedCast( currentState ) ) )
          {
            if ( !newState.has( key ) )
            {
              newState.set( key, Js.undefined() );
            }
          }

          for ( final String key : JsObject.keys( Js.uncheckedCast( newState ) ) )
          {
            final Any newValue = currentState.getAny( key );
            final Any existingValue = newState.getAny( key );
            if ( !Objects.equals( newValue, existingValue ) )
            {
              scheduleDebugStateUpdate( newState );
              return;
            }
          }
        }
      }
    }
  }

  /**
   * Populate the state parameter with any data that is useful when debugging the component.
   *
   * @param state the property map to populate with debug data.
   */
  protected void populateDebugData( @Nonnull final JsPropertyMap<Object> state )
  {
  }

  /**
   * Schedule state update the updates debug state.
   */
  private void scheduleDebugStateUpdate( @Nonnull final JsPropertyMap<Object> data )
  {
    scheduleStateUpdate( Js.cast( JsObject.freeze( data ) ) );
    /*
     * Force an update so do not go through shouldComponentUpdate() as that would be wasted cycles.
     */
    scheduleRender();
    _scheduledDebugStateUpdate = true;
  }
}
