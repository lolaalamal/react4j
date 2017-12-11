package react4j.core;

import elemental2.core.JsArray;
import elemental2.core.JsError;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsFunction;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import static org.realityforge.braincheck.Guards.*;

/**
 * The base java class that mirrors the react component.
 *
 * @param <P> the type of props that this component supports.
 * @param <S> the type of state that this component maintains.
 */
public abstract class Component<P extends BaseProps, S extends BaseState, C extends BaseContext>
{
  /**
   * Callback function for updating state.
   * Useful if the state update is based on current state.
   */
  @JsFunction
  public interface SetStateCallback<P, S>
  {
    /**
     * Callback used to update state.
     * Result is merged into existing state.
     * Return null to abort state update.
     *
     * @param previousState the preiovus state.
     * @param currentProps  the current props.
     * @return the state to shallow merge or null to abort state update.
     */
    @Nullable
    S onSetState( @Nullable S previousState, @Nullable P currentProps );
  }

  @Nonnull
  private ComponentPhase _phase = ComponentPhase.INITIALIZING;
  @Nonnull
  private LifecycleMethod _lifecycleMethod = LifecycleMethod.UNKNOWN;
  @Nullable
  private NativeComponent<P, S, C> _nativeComponent;

  /**
   * Set the phase of the component. Only used for invariant checking.
   */
  final void setPhase( @Nonnull final ComponentPhase phase )
  {
    invariant( ReactConfig::checkComponentStateInvariants,
               () -> "Component.setComponentPhase() invoked on " + this +
                     " when ReactConfig.checkComponentStateInvariants() is false" );
    _phase = Objects.requireNonNull( phase );
  }

  /**
   * Set the current lifecycle method of the component. Only used for invariant checking.
   */
  final void setLifecycleMethod( @Nonnull final LifecycleMethod lifecycleMethod )
  {
    invariant( ReactConfig::checkComponentStateInvariants,
               () -> "Component.setLifecycleMethod() invoked on " + this +
                     " when ReactConfig.checkComponentStateInvariants() is false" );
    _lifecycleMethod = Objects.requireNonNull( lifecycleMethod );
  }

  final void bindComponent( @Nonnull final NativeComponent<P, S, C> nativeComponent )
  {
    _nativeComponent = Objects.requireNonNull( nativeComponent );
  }

  /**
   * Set the initial state of the component.
   * This should only be invoked when the component is initializing.
   * Calling this at any other time is an error.
   *
   * @param state the state.
   */
  protected final void setInitialState( @Nonnull final S state )
  {
    if ( ReactConfig.checkComponentStateInvariants() )
    {
      apiInvariant( () -> ComponentPhase.INITIALIZING == _phase,
                    () -> "Attempted to invoke setInitialState on " + this + " when component is " +
                          "not in INITIALIZING phase but in phase " + _phase + " and state " + _lifecycleMethod );
    }
    component().setInitialState( state );
  }

  /**
   * Return the native react component.
   */
  @Nonnull
  private NativeComponent<P, S, C> component()
  {
    invariant( () -> null != _nativeComponent,
               () -> "Invoked component() on " + this + " before a component has been bound." );
    assert null != _nativeComponent;
    return _nativeComponent;
  }

  /**
   * Return the component state from the native component.
   * This may be null if initial state was never set.
   *
   * @return the component state.
   */
  protected S state()
  {
    return component().state();
  }

  /**
   * Return the component props from the native component.
   * This may be null if no props were supplied.
   *
   * @return the component state.
   */
  protected P props()
  {
    return component().props();
  }

  /**
   * Return the component context from the native component.
   * This may be null if no context is present.
   *
   * @return the component context if any.
   */
  protected final C context()
  {
    return component().context();
  }

  /**
   * Schedule a shallow merge of supplied state into current state.
   * This will trigger an update cycle and is the primary method you
   * use to trigger UI updates from event handlers and server request callbacks.
   *
   * @param state the object literal representing state.
   */
  protected final void scheduleStateUpdate( @Nonnull final S state )
  {
    scheduleStateUpdate( ( p, s ) -> state );
  }

  /**
   * Schedule a shallow merge of supplied state into current state.
   * This will trigger an update cycle and is the primary method you
   * use to trigger UI updates from event handlers and server request callbacks.
   *
   * @param state                 the object literal representing state.
   * @param onStateUpdateComplete a callback that will be invoked after state has been updated.
   */
  protected final void scheduleStateUpdate( @Nonnull final S state,
                                            @Nullable final Procedure onStateUpdateComplete )
  {
    scheduleStateUpdate( ( p, s ) -> state, onStateUpdateComplete );
  }

  /**
   * Schedule a shallow merge of supplied state into current state.
   * This will trigger an update cycle and is the primary method you
   * use to trigger UI updates from event handlers and server request callbacks.
   *
   * @param callback the callback that will be invoked to update state.
   */
  protected final void scheduleStateUpdate( @Nonnull final SetStateCallback<P, S> callback )
  {
    scheduleStateUpdate( callback, null );
  }

  /**
   * Schedule a shallow merge of supplied state into current state.
   * This will trigger an update cycle and is the primary method you
   * use to trigger UI updates from event handlers and server request callbacks.
   *
   * @param callback              the callback that will be invoked to update state.
   * @param onStateUpdateComplete a callback that will be invoked after state has been updated.
   */
  protected void scheduleStateUpdate( @Nonnull final SetStateCallback<P, S> callback,
                                      @Nullable final Procedure onStateUpdateComplete )
  {
    invariantsSetState();
    component().setState( callback );
  }

  /**
   * Check invariants that should be true when invoking setState()
   */
  private void invariantsSetState()
  {
    if ( ReactConfig.checkComponentStateInvariants() )
    {
      apiInvariant( () -> LifecycleMethod.COMPONENT_WILL_UPDATE != _lifecycleMethod,
                    () -> "Incorrectly invoked scheduleStateUpdate() on " + this + " in scope of " +
                          "componentWillUpdate(). If you need to update state in response to " +
                          "a prop change, use componentWillReceiveProps() instead." );
      apiInvariant( () -> LifecycleMethod.RENDER != _lifecycleMethod,
                    () -> "Incorrectly invoked scheduleStateUpdate() on " + this + " in scope of render()." );
      apiInvariant( () -> ComponentPhase.UNMOUNTING != _phase,
                    () -> "Incorrectly invoked scheduleStateUpdate() on " + this + " when component is " +
                          "unmounting or has unmounted." );
    }
  }

  /**
   * Schedule this component for re-rendering.
   * The component re-renders when state or props change but calling this method is another way to
   * schedule the component to be re-rendered.
   *
   * <p>If the force parameter is true then the {@link #shouldComponentUpdate(BaseProps, BaseState, BaseContext)} will be skipped
   * and it is equivalent to calling forceUpdate() on the native react component. See the
   * <a href="https://reactjs.org/docs/react-component.html#forceupdate">React Component documentation</a> for more
   * details.</p>
   *
   * <p>If the force parameter is true then the {@link #shouldComponentUpdate(BaseProps, BaseState, BaseContext)} will be
   * invoked. This is equivalent to calling setState({}) on the native react component.</p>
   *
   * @param force true to skip shouldComponentUpdate during re-render, false otherwise.
   */
  protected final void scheduleRender( final boolean force )
  {
    if ( ReactConfig.checkComponentStateInvariants() )
    {
      apiInvariant( () -> ComponentPhase.UNMOUNTING != _phase,
                    () -> "Incorrectly invoked scheduleRender() on " + this + " when component is " +
                          "unmounting or has unmounted." );
    }
    if ( force )
    {
      component().forceUpdate();
    }
    else
    {
      // This schedules a re-render but will not skip shouldComponentUpdate
      component().setState( ( p, s ) -> Js.uncheckedCast( JsPropertyMap.of() ) );
    }
  }

  /**
   * Render the component.
   * See the <a href="https://reactjs.org/docs/react-component.html#render">React Component documentation</a> for more details.
   * The developer can either override this method or override one of the renderAs* methods. If the developer overrides
   * one of the renderAs* then the annotation processor will override this method. It is an error to override multiple
   * render methods.
   *
   * @return the result of rendering.
   */
  @Nullable
  protected ReactNode render()
  {
    return null;
  }

  /**
   * Render the component as an array.
   * See {@link #render()} for further details.
   *
   * @return the result of rendering.
   */
  @Nullable
  protected ReactNode[] renderAsArray()
  {
    return null;
  }

  /**
   * Render the component as a javascript array.
   * See {@link #render()} for further details.
   *
   * @return the result of rendering.
   */
  @Nullable
  protected JsArray<ReactNode> renderAsJsArray()
  {
    return null;
  }

  /**
   * Wrapper method for rendering the component.
   * By default it just delegates to the {@link #render()} method.
   * This method exists to give middleware a mechanism of wrapping calls to render
   *
   * @return the result of rendering.
   */
  @Nullable
  protected ReactNode performRender()
  {
    return render();
  }

  /**
   * Return the context published by this component to child components.
   *
   * @return the child context if any.
   */
  @Nullable
  protected BaseChildContext getChildContext()
  {
    return null;
  }

  /**
   * Wrapper method for constructing the component.
   * By default it just delegates to the {@link #componentDidConstruct(BaseProps, BaseContext)} method.
   * This method exists to give middleware a mechanism to hook into component construction lifecycle.
   *
   * @param props   the properties that the component was constructed with.
   * @param context the context that the component was constructed with.
   */
  protected void performComponentDidConstruct( @Nullable final P props, @Nullable final C context )
  {
    componentDidConstruct( props, context );
  }

  /**
   * This method is invoked after the component is bound to a native react component.
   * This is a good place to perform initialization.
   *
   * @param props   the properties that the component was constructed with.
   * @param context the context that the component was constructed with.
   */
  protected void componentDidConstruct( @Nullable final P props, @Nullable final C context )
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
   * This method is invoked immediately after updating occurs.
   * If you need to interact with the DOM after the component has been updated.
   * See the <a href="https://reactjs.org/docs/react-component.html#componentdidupdate">React Component documentation</a> for more details.
   *
   * @param prevProps the props before the component was updated.
   * @param prevState the state before the component was updated.
   */
  protected void componentDidUpdate( @Nullable final P prevProps, @Nullable final S prevState )
  {
  }

  /**
   * This method is invoked immediately before mounting occurs.
   * It is called before {@link #render()}, therefore setting state in this method will not trigger a re-rendering.
   * See the <a href="https://reactjs.org/docs/react-component.html#componentwillmount">React Component documentation</a> for more details.
   */
  protected void componentWillMount()
  {
  }

  /**
   * This method is invoked before a mounted component receives new props.
   * If you need to update the state in response to prop changes (for example, to reset it), you
   * may compare the {@link #props()} and supplied nextProps and perform state transitions using
   * {@link #scheduleStateUpdate(BaseState)} in this method.
   * See the <a href="https://reactjs.org/docs/react-component.html#componentwillreceiveprops">React Component documentation</a> for more details.
   *
   * <p>Note that React may call this method even if the props have not changed, so make sure to
   * compare the current and next values if you only want to handle changes. This may occur when the
   * parent component causes your component to re-render.</p>
   *
   * <p>React doesn't call this method with initial props during mounting. It only calls this method
   * if some of component's props may update. Calling {@link #scheduleStateUpdate(BaseState)} generally doesn't trigger
   * this method.</p>
   *
   * @param nextProps   the new properties of the component.
   * @param nextContext the new context of the component.
   */
  protected void componentWillReceiveProps( @Nonnull final P nextProps, @Nonnull final C nextContext )
  {
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
   * This method is invoked immediately before rendering when new props or state are being received.
   * Use this as an opportunity to perform preparation before an update occurs. This method is not
   * called for the initial render.
   * See the <a href="https://reactjs.org/docs/react-component.html#componentwillupdate">React Component documentation</a> for more details.
   *
   * <p>Note that you cannot call {@link #scheduleStateUpdate(BaseState)} here. If you need to update state in
   * response to a prop change, use {@link #componentWillReceiveProps(BaseProps, BaseContext)} instead.</p>
   *
   * <p>Note: This method will not be invoked if {@link #shouldComponentUpdate(BaseProps, BaseState, BaseContext)}
   * returns false.</p>
   *
   * @param nextProps   the new properties of the component.
   * @param nextState   the new state of the component.
   * @param nextContext the new context of the component.
   */
  protected void componentWillUpdate( @Nullable final P nextProps,
                                      @Nullable final S nextState,
                                      @Nonnull final C nextContext )
  {
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
   * Use this method to let React know if a component's output is not affected
   * by the current change in state or props. The default behavior is to re-render on
   * every state change, and in the vast majority of cases you should rely on the default behavior.
   * See the <a href="https://reactjs.org/docs/react-component.html#shouldcomponentupdate">React Component documentation</a> for more details.
   *
   * <p>This method is invoked before rendering when new props or state are being received.
   * This method is not called for the initial render or when {@link #scheduleRender(boolean)}} is called
   * with the force parameter set to true.</p>
   *
   * <p>Returning false does not prevent child components from re-rendering when their state changes.</p>
   *
   * <p>If this method returns false, then {@link #componentWillUpdate(BaseProps, BaseState, BaseContext)},
   * {@link #render()}, and {@link #componentDidUpdate(BaseProps, BaseState)} will not be invoked. In the future
   * React may treat this method  as a hint rather than a strict directive, and returning false may still result
   * in a re-rendering of the component.</p>
   *
   * @param nextProps   the new properties of the component.
   * @param nextState   the new state of the component.
   * @param nextContext the new context of the component.
   * @return true if the component should be updated.
   */
  protected boolean shouldComponentUpdate( @Nullable final P nextProps,
                                           @Nullable final S nextState,
                                           @Nullable final C nextContext )
  {
    return true;
  }
}
