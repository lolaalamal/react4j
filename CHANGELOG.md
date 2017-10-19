# Change Log

### Unreleased

##### Fixed
* Fixed nullability annotations on `React.createElement`.

##### Added
* Added `i` method to DOM factory.

##### Changed
* Move to GWT 2.8.2.

### [v0.05](https://github.com/realityforge/react4j/tree/v0.05) (2017-10-19)
[Full Changelog](https://github.com/realityforge/react4j/compare/v0.04...v0.05)

##### Added
* Added `RenderResult` union type that abstracts over the results of rendering from a component. It is designed
  to support the types supported by React16, namely `ReactElement`, `String` and arrays of values to render.

##### Changed
* **\[core\]** Change the return type of `Component.render()` method to `RenderResult` and add a default
  implementation that returns `null`. Also provide several other render method variants as part of the base
  component implementation `Component.renderAsString()`, `Component.renderAsElement()`, `Component.renderAsJsArray()`
  and `Component.renderAsArray()`. The developer is expected to override a single render method variant and
  the annotation processor will ensure the enhanced class uses that render method variant to render the component.

### [v0.04](https://github.com/realityforge/react4j/tree/v0.04) (2017-10-16)
[Full Changelog](https://github.com/realityforge/react4j/compare/v0.03...v0.04)

##### Changed
* **\[core\]** 💥 Replace `Component.setState(...)` with `Component.scheduleStateUpdate(...)` so that it
  accurately reflects intent.
* Link Javadocs with Arez, GWT and JRE javadocs so as to improve the experience of looking up docs.
* Complete the automation of releasing to Maven central.

### [v0.03](https://github.com/realityforge/react4j/tree/v0.03) (2017-10-16)
[Full Changelog](https://github.com/realityforge/react4j/compare/v0.02...v0.03)

##### Added
* Cached javascript functions created for methods annotated with `@EventHandler`. This allows the equivalent
  of `PureComponent` to be used in React4j to avoid expensive re-renders when only event handlers have updated.

##### Fixed
* Add the missing inherit `com.google.gwt.user.UI` to `react4j.widget.ReactWidget.gwt.xml`.
* **\[processor\]** Add an explicit check to verify that `@EventHandler` annotated methods do not have the same
  logical name nor do they overload each other (i.e. have the same method name but with different parameters).
  Either scenario will result in compilation errors and these checks just make the failure reason explicit.
* **\[arez\]** Make sure `ReactArezComponent` implements `componentWillUnmount` lifecycle method and cleans up
  the arez resources.

##### Changed
* 💥 Change all the compile time constant property keys from being prefixed by `react.` to being prefixed
  by `react4j.` to align with project name change. This should impact downstream users if the builtin modules
  are inherited from and no custom properties were set.
* 💥 **\[arez\]** Classes extending `ReactArezComponent` must NOT be annotated with `@ArezComponent` as the
  generated subclass will be annotated instead. Any `@EventHandler` annotated method have the `@Action` annotation
  added by default and it is an error for the developer to explicitly annotate an `@EventHandler` with an `@Action`
  annotation. This behaviour can be aborted by annotating the method with `@NoAutoAction` in which case the
  developer is responsible for adding the `@Action` annotation if needed.
* **\[processor\]** Change the output of processor so that the method that previously provided the constructor
  function and the event handlers is now a subclass of the component. Also move the lifecycle class and native
  component adapter as static subclasses of this single enhanced component class. This means that React4j generates
  a single java file for each component. The intent is to make it easier to understand what is happening underneath
  the covers.
* **\[core\]** 💥 Remove `Component.forceUpdate()` and replace it with the api `Component.scheduleRender(boolean force)`
  where `force=true` calls the underlying `NativeComponent.forceUpdate()` and `force=false` is equivalent
  to `setState({})`.
* 💥 Changed the maven group coordinate from `org.realityforge.react` to `org.realityforge.react4j`
  so as to reflect project name change.
* Began to automate the release and publishing of both the website to GitHub and the release artifacts
  to Maven Central.
* Replace usages of `org.jetbrains:annotations:jar` dependency with `org.realityforge.anodoc:anodoc:jar`
  as that is the same dependency used in `arez`, `braincheck` and other upstream dependencies.

### [v0.02](https://github.com/realityforge/react4j/tree/v0.02) (2017-10-09)
[Full Changelog](https://github.com/realityforge/react4j/compare/v0.01...v0.02)

##### Fixed
* Upgraded `braincheck` dependency to ensure that invariant checks are correctly compiled out during
  production builds.
* Upgraded `arez` dependency to ensure that compile time constants are correctly resolved at compile time.
* Restructure the `ReactConfig` class so the compile time constants are correctly resolved at compile time.
* Removed the requirement to supply `-generateJsInteropExports` argument when compiling the GWT code by
  defining a `@JsType(isNative = true)` interface to define the lifecycle methods and manually patching
  the constructor function to specify the equivalent of ES6 static properties (i.e. `displayName` atm).
* Made sure that `ReactElement` does not expect a native type named `ReactElement` and is an `Object`.

##### Added
* ✨ Added [ArezSpyUtil](http://realityforge.org/react4j/api/react4j/arez/spy/ArezSpyUtil.html) to simplify the
  setup of Arez spy console logger during development and debugging.
* ✨ Added [ReactWidget](http://realityforge.org/react4j/api/react4j/widget/ReactWidget.html) that simplifies
  integration with applications still using GWT Widgets.

##### Changed
* 💥 Updated `ArezComponent` so subclasses implement the `render()` method rather than `doRender()` to make it
  consistent with the way components that do not extend `ArezComponent` are written.
* 💥 Removed `NativeComponent` from the public API supported by the library by making it package access. It is
  an implementation detail that should not be relevant to downstream consumers.
* 💥 Renamed `componentInitialize()` to `componentDidConstruct()` to follow the naming conventions baked into
  the react component model.
* 💥 Stopped shipping artifacts with a classifier of `gwt` as there is no consumer of the library that is not
  gwt based. This essentially entails shipping the `.gwt.xml` and source files inside the main jar artifact.

### [v0.01](https://github.com/realityforge/react4j/tree/v0.01) (2017-10-06)
[Full Changelog](https://github.com/realityforge/react4j/compare/934ed5a707bfdab7959e9af5a793575a42a780ff...v0.01)

 ‎🎉	Initial super-alpha release ‎🎉.
