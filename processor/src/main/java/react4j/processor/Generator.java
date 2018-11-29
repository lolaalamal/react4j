package react4j.processor;

import com.google.auto.common.GeneratedAnnotationSpecs;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

@SuppressWarnings( "Duplicates" )
final class Generator
{
  private static final ClassName INJECT_CLASSNAME = ClassName.get( "javax.inject", "Inject" );
  private static final ClassName PROVIDER_CLASSNAME = ClassName.get( "javax.inject", "Provider" );
  private static final ClassName NONNULL_CLASSNAME = ClassName.get( "javax.annotation", "Nonnull" );
  private static final ClassName NULLABLE_CLASSNAME = ClassName.get( "javax.annotation", "Nullable" );
  private static final ClassName GUARDS_CLASSNAME = ClassName.get( "org.realityforge.braincheck", "Guards" );
  private static final ClassName OBSERVABLE_CLASSNAME = ClassName.get( "arez", "ObservableValue" );
  private static final ClassName DISPOSABLE_CLASSNAME = ClassName.get( "arez", "Disposable" );
  private static final ClassName AREZ_FEATURE_CLASSNAME =
    ClassName.get( "arez.annotations", "Feature" );
  private static final ClassName ACTION_CLASSNAME = ClassName.get( "arez.annotations", "Action" );
  private static final ClassName DEP_TYPE_CLASSNAME = ClassName.get( "arez.annotations", "DepType" );
  private static final ClassName MEMOIZE_CLASSNAME = ClassName.get( "arez.annotations", "Memoize" );
  private static final ClassName PRIORITY_CLASSNAME = ClassName.get( "arez.annotations", "Priority" );
  private static final ClassName OBSERVABLE_ANNOTATION_CLASSNAME = ClassName.get( "arez.annotations", "Observable" );
  private static final ClassName OBSERVABLE_VALUE_REF_ANNOTATION_CLASSNAME =
    ClassName.get( "arez.annotations", "ObservableValueRef" );
  private static final ClassName AREZ_COMPONENT_CLASSNAME =
    ClassName.get( "arez.annotations", "ArezComponent" );
  private static final ClassName JS_ARRAY_CLASSNAME = ClassName.get( "elemental2.core", "JsArray" );
  private static final ClassName JS_ERROR_CLASSNAME = ClassName.get( "elemental2.core", "JsError" );
  private static final ClassName JS_TYPE_CLASSNAME = ClassName.get( "jsinterop.annotations", "JsType" );
  private static final ClassName JS_CONSTRUCTOR_CLASSNAME = ClassName.get( "jsinterop.annotations", "JsConstructor" );
  private static final ClassName JS_PACKAGE_CLASSNAME = ClassName.get( "jsinterop.annotations", "JsPackage" );
  private static final ClassName JS_CLASSNAME = ClassName.get( "jsinterop.base", "Js" );
  private static final ClassName JS_PROPERTY_MAP_CLASSNAME = ClassName.get( "jsinterop.base", "JsPropertyMap" );
  private static final ParameterizedTypeName JS_PROPERTY_MAP_T_OBJECT_CLASSNAME =
    ParameterizedTypeName.get( JS_PROPERTY_MAP_CLASSNAME, TypeName.OBJECT );
  private static final ClassName COMPONENT_CONSTRUCTOR_FUNCTION_CLASSNAME =
    ClassName.get( "react4j", "ComponentConstructorFunction" );
  private static final ClassName REACT_NODE_CLASSNAME = ClassName.get( "react4j", "ReactNode" );
  private static final ClassName REACT_ELEMENT_CLASSNAME = ClassName.get( "react4j", "ReactElement" );
  private static final ClassName REACT_ERROR_INFO_CLASSNAME = ClassName.get( "react4j", "ReactErrorInfo" );
  private static final ClassName REACT_NATIVE_ADAPTER_COMPONENT_CLASSNAME =
    ClassName.get( "react4j", "NativeAdapterComponent" );
  private static final ClassName REACT_CONFIG_CLASSNAME = ClassName.get( "react4j", "ReactConfig" );
  private static final ClassName COMPONENT_CLASSNAME = ClassName.get( "react4j", "Component" );
  private static final String INTERNAL_METHOD_PREFIX = "$$react4j$$_";
  private static final String COMPONENT_PRE_UPDATE_METHOD = INTERNAL_METHOD_PREFIX + "componentPreUpdate";
  private static final String COMPONENT_DID_UPDATE_METHOD = INTERNAL_METHOD_PREFIX + "componentDidUpdate";
  private static final String COMPONENT_DID_MOUNT_METHOD = INTERNAL_METHOD_PREFIX + "componentDidMount";

  private Generator()
  {
  }

  @Nonnull
  static TypeSpec buildComponentBuilder( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( descriptor.getBuilderClassName() );
    addGeneratedAnnotation( descriptor, builder );

    ProcessorUtil.copyAccessModifiers( descriptor.getElement(), builder );

    // Private constructor so can not instantiate
    builder.addMethod( MethodSpec.constructorBuilder().addModifiers( Modifier.PRIVATE ).build() );

    final BuilderDescriptor builderDescriptor = buildBuilderDescriptor( descriptor );

    final ArrayList<Step> steps = builderDescriptor.getSteps();
    for ( final Step step : steps )
    {
      builder.addType( buildBuilderStepInterface( descriptor, step ) );
    }

    // key step
    buildStaticStepMethodMethods( descriptor, builder, steps.get( 0 ) );

    // first step which may be required prop, optional props, or build terminal step.
    buildStaticStepMethodMethods( descriptor, builder, steps.get( 1 ) );

    builder.addType( buildBuilder( descriptor, builderDescriptor ) );

    return builder.build();
  }

  private static void buildStaticStepMethodMethods( @Nonnull final ComponentDescriptor descriptor,
                                                    @Nonnull final TypeSpec.Builder builder,
                                                    @Nonnull final Step step )
  {
    final ArrayList<StepMethod> methods = step.getMethods();
    for ( final StepMethod method : methods )
    {
      builder.addMethod( buildStaticStepMethodMethod( descriptor, step, method ) );
    }
  }

  @Nonnull
  private static MethodSpec buildStaticStepMethodMethod( @Nonnull final ComponentDescriptor descriptor,
                                                         @Nonnull final Step step,
                                                         @Nonnull final StepMethod stepMethod )
  {
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( stepMethod.getName() ).
        addAnnotation( NONNULL_CLASSNAME );

    method.addModifiers( Modifier.STATIC );
    if ( descriptor.getDeclaredType().asElement().getModifiers().contains( Modifier.PUBLIC ) )
    {
      method.addModifiers( Modifier.PUBLIC );
    }
    final ExecutableType propMethodType = stepMethod.getPropMethodType();
    if ( null != propMethodType )
    {
      ProcessorUtil.copyTypeParameters( propMethodType, method );
    }
    ProcessorUtil.copyTypeParameters( descriptor.getElement(), method );

    if ( stepMethod.isBuildIntrinsic() )
    {
      final String infix = asTypeArgumentsInfix( descriptor.getDeclaredType() );
      method.addStatement( "return new $T" + infix + "().build()", ClassName.bestGuess( "Builder" ) );
    }
    else
    {
      final ParameterSpec.Builder parameter =
        ParameterSpec.builder( stepMethod.getType(), stepMethod.getName(), Modifier.FINAL );
      final ExecutableElement propMethod = stepMethod.getPropMethod();
      if ( null != propMethod )
      {
        ProcessorUtil.copyWhitelistedAnnotations( propMethod, parameter );
      }
      else if ( stepMethod.isChildrenStreamIntrinsic() )
      {
        parameter.addAnnotation( NONNULL_CLASSNAME );
      }
      else if ( stepMethod.isKeyIntrinsic() && !stepMethod.getKey().equals( "*key_int*" ) )
      {
        parameter.addAnnotation( NONNULL_CLASSNAME );
      }
      method.addParameter( parameter.build() );
      final String infix = asTypeArgumentsInfix( descriptor.getDeclaredType() );
      method.addStatement( "return new $T" + infix + "().$N( $N )",
                           ClassName.bestGuess( "Builder" ),
                           stepMethod.getName(),
                           stepMethod.getName() );
    }
    configureStepMethodReturns( descriptor, method, step, stepMethod.getStepMethodType() );
    return method.build();
  }

  @Nonnull
  private static MethodSpec.Builder buildStepInterfaceMethod( @Nonnull final ComponentDescriptor descriptor,
                                                              @Nonnull final String name,
                                                              @Nonnull final Step step,
                                                              @Nonnull final StepMethodType stepMethodType,
                                                              @Nonnull final Consumer<MethodSpec.Builder> action )
  {
    final MethodSpec.Builder method = MethodSpec.methodBuilder( name );
    method.addModifiers( Modifier.PUBLIC, Modifier.ABSTRACT );
    method.addAnnotation( NONNULL_CLASSNAME );
    action.accept( method );
    configureStepMethodReturns( descriptor, method, step, stepMethodType );
    return method;
  }

  private static void configureStepMethodReturns( @Nonnull final ComponentDescriptor descriptor,
                                                  @Nonnull final MethodSpec.Builder method,
                                                  @Nonnull final Step step,
                                                  @Nonnull final StepMethodType stepMethodType )
  {
    if ( StepMethodType.TERMINATE == stepMethodType )
    {
      method.returns( REACT_NODE_CLASSNAME );
    }
    else
    {
      final int returnIndex = step.getIndex() + ( StepMethodType.STAY == stepMethodType ? 0 : 1 );
      final ClassName className = ClassName.bestGuess( "Builder" + returnIndex );
      final List<TypeVariableName> variableNames =
        ProcessorUtil.getTypeArgumentsAsNames( descriptor.getDeclaredType() );
      if ( variableNames.isEmpty() )
      {
        method.returns( className );
      }
      else
      {
        method.returns( ParameterizedTypeName.get( className, variableNames.toArray( new TypeName[ 0 ] ) ) );
      }
    }
  }

  @Nonnull
  private static TypeSpec buildBuilderStepInterface( @Nonnull final ComponentDescriptor descriptor,
                                                     @Nonnull final Step step )
  {
    final int stepIndex = step.getIndex();
    final TypeSpec.Builder builder = TypeSpec.interfaceBuilder( "Builder" + stepIndex );
    builder.addModifiers( Modifier.PUBLIC, Modifier.STATIC );
    builder.addTypeVariables( ProcessorUtil.getTypeArgumentsAsNames( descriptor.getDeclaredType() ) );

    if ( !descriptor.getDeclaredType().getTypeArguments().isEmpty() )
    {
      builder.addAnnotation( AnnotationSpec.builder( SuppressWarnings.class )
                               .addMember( "value", "$S", "unused" )
                               .build() );
    }

    for ( final StepMethod stepMethod : step.getMethods() )
    {
      final StepMethodType stepMethodType = stepMethod.getStepMethodType();
      // Magically handle the step method named build
      if ( stepMethod.isBuildIntrinsic() )
      {
        builder.addMethod( buildStepInterfaceMethod( descriptor, "build", step, stepMethodType, m -> {
        } ).build() );
      }
      else
      {
        builder.addMethod( buildStepInterfaceMethod( descriptor, stepMethod.getName(), step, stepMethodType, m -> {
          final ExecutableType propMethodType = stepMethod.getPropMethodType();
          if ( null != propMethodType )
          {
            ProcessorUtil.copyTypeParameters( propMethodType, m );
          }
          if ( stepMethod.isChildrenIntrinsic() )
          {
            m.varargs();
          }
          final ParameterSpec.Builder parameter = ParameterSpec.builder( stepMethod.getType(), stepMethod.getName() );
          final ExecutableElement propMethod = stepMethod.getPropMethod();
          if ( null != propMethod )
          {
            ProcessorUtil.copyWhitelistedAnnotations( propMethod, parameter );
          }
          else if ( stepMethod.isKeyIntrinsic() || stepMethod.isChildrenStreamIntrinsic() )
          {
            parameter.addAnnotation( NONNULL_CLASSNAME );
          }
          m.addParameter( parameter.build() );
        } ).build() );
      }
    }

    return builder.build();
  }

  @Nonnull
  private static MethodSpec buildBuilderStepImpl( @Nonnull final ComponentDescriptor descriptor,
                                                  @Nonnull final Step step,
                                                  @Nonnull final StepMethod stepMethod )
  {
    final MethodSpec.Builder method = MethodSpec.methodBuilder( stepMethod.getName() );
    method.addModifiers( Modifier.PUBLIC, Modifier.FINAL );
    method.addAnnotation( Override.class );
    method.addAnnotation( NONNULL_CLASSNAME );

    final ExecutableType propMethodType = stepMethod.getPropMethodType();
    if ( null != propMethodType )
    {
      ProcessorUtil.copyTypeParameters( propMethodType, method );
    }
    final ParameterSpec.Builder parameter =
      ParameterSpec.builder( stepMethod.getType(), stepMethod.getName(), Modifier.FINAL );
    final ExecutableElement propMethod = stepMethod.getPropMethod();
    if ( null != propMethod )
    {
      ProcessorUtil.copyWhitelistedAnnotations( propMethod, parameter );
    }
    else if ( stepMethod.isKeyIntrinsic() || stepMethod.isChildrenStreamIntrinsic() )
    {
      parameter.addAnnotation( NONNULL_CLASSNAME );
    }
    method.addParameter( parameter.build() );

    boolean returnHandled = false;

    if ( stepMethod.isChildrenIntrinsic() )
    {
      method.varargs();
      final PropDescriptor prop = stepMethod.getProp();
      assert null != prop;
      method.addStatement( "_element.props().set( $T.Props.$N, $T.of( $N ) )",
                           descriptor.getEnhancedClassName(),
                           prop.getConstantName(),
                           JS_ARRAY_CLASSNAME,
                           stepMethod.getName() );
    }
    else if ( stepMethod.isChildrenStreamIntrinsic() )
    {
      method.addStatement( "children( $N.toArray( $T[]::new ) )", stepMethod.getName(), REACT_NODE_CLASSNAME );
    }
    else if ( stepMethod.isChildIntrinsic() )
    {
      assert null != propMethod;
      final PropDescriptor prop = stepMethod.getProp();
      assert null != prop;
      if ( isNonnull( propMethod ) )
      {
        method.addStatement( "_element.props().set( $T.Props.$N, $T.of( $T.requireNonNull( $N ) ) )",
                             descriptor.getEnhancedClassName(),
                             prop.getConstantName(),
                             JS_ARRAY_CLASSNAME,
                             Objects.class,
                             stepMethod.getName() );
      }
      else
      {
        method.addStatement( "_element.props().set( $T.Props.$N, $T.of( $N ) )",
                             descriptor.getEnhancedClassName(),
                             prop.getConstantName(),
                             JS_ARRAY_CLASSNAME,
                             stepMethod.getName() );
      }
    }
    else if ( stepMethod.getKey().equals( "*key_int*" ) )
    {
      returnHandled = true;
      // TODO: Handle this by prop enhancer ?
      method.addStatement( "return key( $T.valueOf( $N ) )", TypeName.get( String.class ), stepMethod.getName() );
    }
    else
    {
      if ( stepMethod.isKeyIntrinsic() )
      {
        method.addStatement( "_element.setKey( $T.requireNonNull( $N ) )",
                             TypeName.get( Objects.class ),
                             stepMethod.getName() );
      }
      else
      {
        if ( ( null != propMethod && isNonnull( propMethod ) ) && !stepMethod.getType().isPrimitive() )
        {
          method.addStatement( "$T.requireNonNull( $N )", Objects.class, stepMethod.getName() );
        }
        final PropDescriptor prop = stepMethod.getProp();
        assert null != prop;
        method.addStatement( "_element.props().set( $T.Props.$N, $N )",
                             descriptor.getEnhancedClassName(),
                             prop.getConstantName(),
                             stepMethod.getName() );
      }
    }

    if ( !returnHandled )
    {
      if ( StepMethodType.TERMINATE == stepMethod.getStepMethodType() )
      {
        method.addStatement( "return build()" );
      }
      else
      {
        method.addStatement( "return this" );
      }
    }
    configureStepMethodReturns( descriptor, method, step, stepMethod.getStepMethodType() );

    return method.build();
  }

  private static boolean isNonnull( @Nonnull final ExecutableElement method )
  {
    return null != ProcessorUtil.findAnnotationByType( method, Constants.NONNULL_ANNOTATION_CLASSNAME );
  }

  @Nonnull
  private static MethodSpec buildBuildStepImpl()
  {
    return MethodSpec.methodBuilder( "build" )
      .addModifiers( Modifier.PUBLIC, Modifier.FINAL )
      .addAnnotation( NONNULL_CLASSNAME )
      .addStatement( "_element.complete()" )
      .addStatement( "return _element" )
      .returns( REACT_NODE_CLASSNAME )
      .build();
  }

  @Nonnull
  private static TypeSpec buildBuilder( @Nonnull final ComponentDescriptor descriptor,
                                        @Nonnull final BuilderDescriptor builderDescriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( "Builder" );
    ProcessorUtil.copyTypeParameters( descriptor.getElement(), builder );
    builder.addModifiers( Modifier.PRIVATE, Modifier.STATIC );
    final ArrayList<Step> steps = builderDescriptor.getSteps();
    for ( int i = 0; i < steps.size(); i++ )
    {
      builder.addSuperinterface( getParameterizedTypeName( descriptor, ClassName.bestGuess( "Builder" + ( i + 1 ) ) ) );
    }

    final List<PropDescriptor> propsWithDefaults = descriptor.getProps()
      .stream()
      .filter( p -> p.hasDefaultField() || p.hasDefaultMethod() )
      .collect( Collectors.toList() );
    if ( !propsWithDefaults.isEmpty() )
    {
      final MethodSpec.Builder method = MethodSpec.constructorBuilder();
      //TODO: Should we enforce the non-presence of _element field so we guarantee the wrapper class is eliminated?
      method.addStatement( "_element = $T.createComponentElement( $T.Factory.TYPE )",
                           REACT_ELEMENT_CLASSNAME,
                           descriptor.getEnhancedClassName() );
      for ( final PropDescriptor prop : propsWithDefaults )
      {

        method.addStatement( "_element.props().set( $T.Props.$N, $T.$N" +
                             ( prop.hasDefaultField() ? "" : "()" ) + " )",
                             descriptor.getEnhancedClassName(),
                             prop.getConstantName(),
                             descriptor.getClassName(),
                             prop.hasDefaultField() ?
                             prop.getDefaultField().getSimpleName() :
                             prop.getDefaultMethod().getSimpleName() );
      }

      builder.addMethod( method.build() );
    }

    final HashSet<String> stepMethodsAdded = new HashSet<>();
    for ( final Step step : steps )
    {
      for ( final StepMethod stepMethod : step.getMethods() )
      {
        if ( stepMethodsAdded.add( stepMethod.getName() + stepMethod.getType().toString() ) )
        {
          if ( !stepMethod.isBuildIntrinsic() )
          {
            builder.addMethod( buildBuilderStepImpl( descriptor, step, stepMethod ) );
          }
        }
      }
    }

    final FieldSpec.Builder field =
      FieldSpec.builder( REACT_ELEMENT_CLASSNAME, "_element", Modifier.PRIVATE, Modifier.FINAL );
    if ( propsWithDefaults.isEmpty() )
    {
      field.initializer( "$T.createComponentElement( $T.Factory.TYPE )",
                         REACT_ELEMENT_CLASSNAME,
                         descriptor.getEnhancedClassName() );
    }
    builder.addField( field.build() );

    builder.addMethod( buildBuildStepImpl() );

    return builder.build();
  }

  @Nonnull
  private static TypeName getParameterizedTypeName( @Nonnull final ComponentDescriptor descriptor,
                                                    @Nonnull final ClassName baseName )
  {
    final List<? extends TypeMirror> arguments = descriptor.getDeclaredType().getTypeArguments();
    if ( arguments.isEmpty() )
    {
      return baseName;
    }
    else
    {
      return ParameterizedTypeName.get( baseName, arguments.stream().map( TypeName::get ).toArray( TypeName[]::new ) );
    }
  }

  @Nonnull
  static TypeSpec buildEnhancedComponent( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( descriptor.getEnhancedClassName() );
    builder.addTypeVariables( ProcessorUtil.getTypeArgumentsAsNames( descriptor.getDeclaredType() ) );

    builder.superclass( descriptor.getComponentType() );

    if ( descriptor.isArezComponent() )
    {
      final AnnotationSpec.Builder annotation =
        AnnotationSpec.builder( AREZ_COMPONENT_CLASSNAME ).
          addMember( "name", "$S", descriptor.getName() );
      if ( descriptor.shouldRunArezScheduler() )
      {
        annotation.addMember( "deferSchedule", "true" );
      }
      if ( descriptor.needsInjection() )
      {
        annotation.addMember( "inject", "$T.ENABLE", AREZ_FEATURE_CLASSNAME );
      }
      builder.addAnnotation( annotation.build() );
      builder.addModifiers( Modifier.ABSTRACT );
    }

    addGeneratedAnnotation( descriptor, builder );
    addOriginatingTypes( descriptor.getElement(), builder );

    builder.addType( buildFactory() );
    if ( descriptor.needsInjection() )
    {
      builder.addType( buildInjectSupport( descriptor ) );
    }
    builder.addType( buildPropsType( descriptor ) );

    builder.addMethod( buildConstructorFnMethod( descriptor ).build() );

    final List<PropDescriptor> props = descriptor.getProps();
    if ( descriptor.isArezComponent() )
    {
      final List<PropDescriptor> disposableProps =
        props.stream().filter( PropDescriptor::isDisposable ).collect( Collectors.toList() );
      if ( !disposableProps.isEmpty() )
      {
        builder.addMethod( buildAnyPropsDisposedMethod( disposableProps ).build() );
      }
    }

    for ( final PropDescriptor prop : props )
    {
      builder.addMethod( buildPropMethod( descriptor, prop ).build() );
      if ( descriptor.isArezComponent() && prop.isObservable() )
      {
        builder.addMethod( buildPropObservableValueRefMethod( prop ).build() );
      }
    }

    if ( descriptor.hasValidatedProps() )
    {
      builder.addMethod( buildPropValidatorMethod( descriptor ).build() );
    }

    if ( descriptor.hasObservableProps() )
    {
      builder.addMethod( buildReportPropChangesMethod( descriptor ).build() );
    }
    if ( descriptor.hasPreUpdateOnPropChange() )
    {
      builder.addMethod( buildPreUpdateOnPropChangeMethod( descriptor ).build() );
    }
    if ( descriptor.hasPostUpdateOnPropChange() )
    {
      builder.addMethod( buildPostUpdateOnPropChangeMethod( descriptor ).build() );
    }
    if ( descriptor.generateComponentPreUpdate() )
    {
      builder.addMethod( buildComponentPreUpdate( descriptor ).build() );
    }
    builder.addMethod( buildComponentDidMount( descriptor ).build() );
    builder.addMethod( buildComponentDidUpdate( descriptor ).build() );

    if ( descriptor.isArezComponent() )
    {
      final MethodSpec.Builder method = buildShouldComponentUpdateMethod( descriptor );
      if ( null != method )
      {
        builder.addMethod( method.build() );
      }
    }

    if ( descriptor.needsInjection() && !descriptor.isArezComponent() )
    {
      builder.addMethod( MethodSpec.constructorBuilder().addAnnotation( INJECT_CLASSNAME ).build() );
    }

    if ( descriptor.shouldRunArezScheduler() && descriptor.isArezComponent() )
    {
      builder.addMethod( MethodSpec.methodBuilder( "triggerScheduler" ).
        addAnnotation( Override.class ).
        addModifiers( Modifier.PROTECTED, Modifier.FINAL ).
        addStatement( "getContext().triggerScheduler()" ).
        build() );
    }

    if ( descriptor.isArezComponent() )
    {
      for ( final MethodDescriptor method : descriptor.getMemoizeMethods() )
      {
        builder.addMethod( buildMemoizeWrapperMethod( method ).build() );
      }
    }

    if ( !descriptor.getLifecycleMethods().isEmpty() )
    {
      if ( descriptor.shouldGenerateLiteLifecycle() && !descriptor.getLiteLifecycleMethods().isEmpty() )
      {
        builder.addType( buildNativeLiteLifecycleInterface( descriptor ) );
      }
      builder.addType( buildNativeLifecycleInterface( descriptor ) );
    }
    if ( descriptor.shouldGenerateLiteLifecycle() )
    {
      builder.addType( buildNativeComponent( descriptor, true ) );
    }
    builder.addType( buildNativeComponent( descriptor, false ) );

    return builder.build();
  }

  private static FieldSpec.Builder buildPropKeyConstantField( @Nonnull final PropDescriptor descriptor,
                                                              final int index )
  {
    final String name = descriptor.getName();

    final FieldSpec.Builder field =
      FieldSpec.builder( TypeName.get( String.class ),
                         descriptor.getConstantName(),
                         Modifier.STATIC,
                         Modifier.FINAL );
    if ( descriptor.isSpecialChildrenProp() )
    {
      return field.initializer( "$S", "children" );
    }
    else
    {
      return field.initializer( "$T.shouldMinimizePropKeys() ? $S : $S",
                                REACT_CONFIG_CLASSNAME,
                                Character.toString( (char) ( 'a' + index ) ),
                                name );
    }
  }

  @Nonnull
  private static MethodSpec.Builder buildMemoizeWrapperMethod( @Nonnull final MethodDescriptor descriptor )
  {
    return generateOverrideMethod( descriptor ).addAnnotation( buildMemoizeAnnotation( descriptor ).build() );
  }

  @Nonnull
  private static MethodSpec.Builder generateOverrideMethod( final @Nonnull MethodDescriptor descriptor )
  {
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( descriptor.getMethod().getSimpleName().toString() ).
        addAnnotation( Override.class ).
        returns( TypeName.get( descriptor.getMethodType().getReturnType() ) );
    ProcessorUtil.copyTypeParameters( descriptor.getMethodType(), method );
    ProcessorUtil.copyAccessModifiers( descriptor.getMethod(), method );
    ProcessorUtil.copyWhitelistedAnnotations( descriptor.getMethod(), method );

    final int paramCount = descriptor.getMethod().getParameters().size();
    for ( int i = 0; i < paramCount; i++ )
    {
      final TypeMirror paramType = descriptor.getMethodType().getParameterTypes().get( i );
      final VariableElement param = descriptor.getMethod().getParameters().get( i );
      final ParameterSpec.Builder parameter =
        ParameterSpec.builder( TypeName.get( paramType ), param.getSimpleName().toString(), Modifier.FINAL );
      ProcessorUtil.copyWhitelistedAnnotations( param, parameter );
      method.addParameter( parameter.build() );
    }
    final String params =
      0 == paramCount ?
      "" :
      IntStream.range( 0, paramCount )
        .mapToObj( i -> descriptor.getMethod().getParameters().get( i ).getSimpleName().toString() )
        .collect( Collectors.joining( "," ) );

    final boolean isVoid = descriptor.getMethodType().getReturnType().getKind() == TypeKind.VOID;

    method.addStatement( ( isVoid ? "" : "return " ) + "super.$N(" + params + ")",
                         descriptor.getMethod().getSimpleName() );
    return method;
  }

  @Nonnull
  private static AnnotationSpec.Builder buildMemoizeAnnotation( @Nonnull final MethodDescriptor descriptor )
  {
    final AnnotationSpec.Builder annotation =
      AnnotationSpec.builder( MEMOIZE_CLASSNAME ).
        addMember( "priority", "$T.LOWEST", PRIORITY_CLASSNAME );
    final AnnotationValue nameValue =
      ProcessorUtil.findDeclaredAnnotationValue( descriptor.getMethod(),
                                                 Constants.MEMOIZE_ANNOTATION_CLASSNAME,
                                                 "name" );
    if ( null != nameValue )
    {
      annotation.addMember( "name", "$S", nameValue.getValue().toString() );
    }
    final AnnotationValue keepAliveValue =
      ProcessorUtil.findDeclaredAnnotationValue( descriptor.getMethod(),
                                                 Constants.MEMOIZE_ANNOTATION_CLASSNAME,
                                                 "keepAlive" );
    if ( null != keepAliveValue )
    {
      annotation.addMember( "keepAlive", "$N", keepAliveValue.getValue().toString() );
    }
    final AnnotationValue reportResultValue =
      ProcessorUtil.findDeclaredAnnotationValue( descriptor.getMethod(),
                                                 Constants.MEMOIZE_ANNOTATION_CLASSNAME,
                                                 "reportResult" );
    if ( null != reportResultValue )
    {
      annotation.addMember( "reportResult", "$N", reportResultValue.getValue().toString() );
    }

    final AnnotationValue requireEnvironmentValue =
      ProcessorUtil.findDeclaredAnnotationValue( descriptor.getMethod(),
                                                 Constants.MEMOIZE_ANNOTATION_CLASSNAME,
                                                 "requireEnvironment" );
    if ( null != requireEnvironmentValue )
    {
      annotation.addMember( "requireEnvironment", "$N", requireEnvironmentValue.getValue().toString() );
    }
    final AnnotationValue depTypeValue =
      ProcessorUtil.findDeclaredAnnotationValue( descriptor.getMethod(),
                                                 Constants.MEMOIZE_ANNOTATION_CLASSNAME,
                                                 "depType" );
    if ( null != depTypeValue )
    {
      annotation.addMember( "depType", "$T.$N", DEP_TYPE_CLASSNAME, depTypeValue.getValue().toString() );
    }

    final AnnotationValue observeLowerPriorityDependenciesValue =
      ProcessorUtil.findDeclaredAnnotationValue( descriptor.getMethod(),
                                                 Constants.MEMOIZE_ANNOTATION_CLASSNAME,
                                                 "observeLowerPriorityDependencies" );
    if ( null != observeLowerPriorityDependenciesValue )
    {
      annotation.addMember( "observeLowerPriorityDependencies",
                            "$N",
                            observeLowerPriorityDependenciesValue.getValue().toString() );
    }
    return annotation;
  }

  @Nonnull
  private static MethodSpec.Builder buildAnyPropsDisposedMethod( @Nonnull final List<PropDescriptor> props )
  {
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( "anyPropsDisposed" )
        .addModifiers( Modifier.PROTECTED, Modifier.FINAL )
        .addAnnotation( Override.class )
        .returns( TypeName.BOOLEAN );

    for ( final PropDescriptor prop : props )
    {
      final String varName = "$$react4jv$$_" + prop.getMethod().getSimpleName();
      method.addStatement( "final $T $N = $N()",
                           prop.getMethodType().getReturnType(),
                           varName,
                           prop.getMethod().getSimpleName().toString() );
      final CodeBlock.Builder block = CodeBlock.builder();
      if ( prop.isOptional() )
      {
        block.beginControlFlow( "if ( null != $N && $T.isDisposed( $N ) )", varName, DISPOSABLE_CLASSNAME, varName );
      }
      else
      {
        block.beginControlFlow( "if ( $T.isDisposed( $N ) )", DISPOSABLE_CLASSNAME, varName );
      }
      block.addStatement( "return true" );
      block.endControlFlow();
      method.addCode( block.build() );

    }
    method.addStatement( "return false" );

    return method;
  }

  private static MethodSpec.Builder buildPropMethod( @Nonnull final ComponentDescriptor descriptor,
                                                     @Nonnull final PropDescriptor prop )
  {
    final ExecutableElement methodElement = prop.getMethod();
    final ExecutableType methodType = prop.getMethodType();
    final TypeMirror returnType = methodType.getReturnType();
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( methodElement.getSimpleName().toString() ).
        returns( TypeName.get( returnType ) );
    ProcessorUtil.copyTypeParameters( methodType, method );
    ProcessorUtil.copyAccessModifiers( methodElement, method );
    ProcessorUtil.copyWhitelistedAnnotations( methodElement, method );

    method.addAnnotation( Override.class );

    if ( descriptor.isArezComponent() && prop.isObservable() )
    {
      final AnnotationSpec.Builder annotation =
        AnnotationSpec.builder( OBSERVABLE_ANNOTATION_CLASSNAME ).
          addMember( "name", "$S", prop.getName() ).
          addMember( "expectSetter", "false" ).
          addMember( "readOutsideTransaction", "true" );
      method.addAnnotation( annotation.build() );
    }
    final String convertMethodName = getConverter( returnType, methodElement );
    final TypeKind resultKind = methodElement.getReturnType().getKind();
    if ( !resultKind.isPrimitive() && !isNonnull( methodElement ) )
    {
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "if ( $T.shouldCheckInvariants() )", REACT_CONFIG_CLASSNAME );
      block.addStatement( "return null != props().getAny( Props.$N ) ? props().getAny( Props.$N ).$N() : null",
                          prop.getConstantName(),
                          prop.getConstantName(),
                          convertMethodName );
      block.nextControlFlow( "else" );
      block.addStatement( "return $T.uncheckedCast( props().getAny( Props.$N ) )",
                          JS_CLASSNAME,
                          prop.getConstantName() );
      block.endControlFlow();
      method.addCode( block.build() );
    }
    else
    {
      method.addStatement( "return props().getAny( Props.$N ).$N()", prop.getConstantName(), convertMethodName );
    }
    return method;
  }

  @Nonnull
  private static String getConverter( @Nonnull final TypeMirror type, @Nonnull final Element element )
  {
    switch ( type.getKind() )
    {
      case BOOLEAN:
        return "asBoolean";
      case BYTE:
        return "asByte";
      case CHAR:
        return "asChar";
      case DOUBLE:
        return "asDouble";
      case FLOAT:
        return "asFloat";
      case INT:
        return "asInt";
      case LONG:
        return "asLong";
      case SHORT:
        return "asShort";
      case TYPEVAR:
        return "cast";
      case DECLARED:
        if ( type.toString().equals( "java.lang.String" ) )
        {
          return "asString";
        }
        else
        {
          return "cast";
        }
      case ARRAY:
        return "cast";
      default:
        throw new ReactProcessorException( "Return type of @Prop method is not yet " +
                                           "handled. Type: " + type.getKind(), element );
    }
  }

  @Nonnull
  private static MethodSpec.Builder buildPropObservableValueRefMethod( @Nonnull final PropDescriptor prop )
  {
    return MethodSpec.methodBuilder( toObservableValueRefMethodName( prop ) ).
      addModifiers( Modifier.PROTECTED, Modifier.ABSTRACT ).
      addAnnotation( NONNULL_CLASSNAME ).
      addAnnotation( OBSERVABLE_VALUE_REF_ANNOTATION_CLASSNAME ).
      returns( OBSERVABLE_CLASSNAME );
  }

  @Nonnull
  private static String toObservableValueRefMethodName( @Nonnull final PropDescriptor prop )
  {
    final String name = prop.getName();
    return "get" + Character.toUpperCase( name.charAt( 0 ) ) + name.substring( 1 ) + "ObservableValue";
  }

  @Nonnull
  private static MethodSpec.Builder buildReportPropChangesMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    assert descriptor.isArezComponent();
    final List<PropDescriptor> props =
      descriptor.getProps()
        .stream()
        .filter( PropDescriptor::isObservable )
        .collect( Collectors.toList() );
    assert !props.isEmpty();
    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( "reportPropChanges" ).
        addModifiers( Modifier.PROTECTED ).
        addAnnotation( Override.class ).
        addParameter( ParameterSpec.builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "props", Modifier.FINAL ).
          addAnnotation( NONNULL_CLASSNAME ).build() ).
        addParameter( ParameterSpec.builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "nextProps", Modifier.FINAL ).
          addAnnotation( NONNULL_CLASSNAME ).build() ).
        addParameter( ParameterSpec.builder( TypeName.BOOLEAN, "inComponentPreUpdate", Modifier.FINAL ).build() ).
        returns( TypeName.BOOLEAN );
    final AnnotationSpec.Builder annotation =
      AnnotationSpec.builder( ACTION_CLASSNAME ).addMember( "verifyRequired", "false" );
    method.addAnnotation( annotation.build() );
    method.addStatement( "boolean modified = false" );
    method.addStatement( "final boolean reportChanges = shouldReportPropChanges( inComponentPreUpdate )" );

    for ( final PropDescriptor prop : props )
    {
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "if ( !$T.isTripleEqual( props.get( Props.$N ), nextProps.get( Props.$N ) ) )",
                              JS_CLASSNAME,
                              prop.getConstantName(),
                              prop.getConstantName() );
      final CodeBlock.Builder reportBlock = CodeBlock.builder();
      reportBlock.beginControlFlow( "if ( reportChanges )" );
      reportBlock.addStatement( "$N().reportChanged()", toObservableValueRefMethodName( prop ) );
      reportBlock.endControlFlow();
      block.add( reportBlock.build() );
      if ( prop.shouldUpdateOnChange() )
      {
        block.addStatement( "modified = true" );
      }
      block.endControlFlow();
      method.addCode( block.build() );
    }
    method.addStatement( "return modified || hasRenderDepsChanged()" );
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildPreUpdateOnPropChangeMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    assert descriptor.hasPreUpdateOnPropChange();
    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( "preUpdateOnPropChange" ).
        addModifiers( Modifier.PROTECTED ).
        addAnnotation( Override.class ).
        addParameter( ParameterSpec
                        .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "prevProps", Modifier.FINAL )
                        .addAnnotation( NONNULL_CLASSNAME )
                        .build() ).
        addParameter( ParameterSpec
                        .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "props", Modifier.FINAL )
                        .addAnnotation( NONNULL_CLASSNAME )
                        .build() );

    buildOnPropChangeInvocations( method, descriptor.getPreUpdateOnPropChangeDescriptors() );
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildPostUpdateOnPropChangeMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    assert descriptor.hasPostUpdateOnPropChange();
    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( "postUpdateOnPropChange" ).
        addModifiers( Modifier.PROTECTED ).
        addAnnotation( Override.class ).
        addParameter( ParameterSpec
                        .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "prevProps", Modifier.FINAL )
                        .addAnnotation( NONNULL_CLASSNAME )
                        .build() ).
        addParameter( ParameterSpec
                        .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "props", Modifier.FINAL )
                        .addAnnotation( NONNULL_CLASSNAME )
                        .build() );

    buildOnPropChangeInvocations( method, descriptor.getPostUpdateOnPropChangeDescriptors() );
    return method;
  }

  private static void buildOnPropChangeInvocations( final MethodSpec.Builder method,
                                                    final List<OnPropChangeDescriptor> onPropChanges )
  {
    // The list of props we need to check for changes
    final List<PropDescriptor> props =
      onPropChanges.stream().flatMap( d -> d.getProps().stream() ).distinct().collect( Collectors.toList() );

    for ( final PropDescriptor prop : props )
    {
      method.addStatement( "final boolean $N = !$T.isTripleEqual( props.get( Props.$N ), prevProps.get( Props.$N ) )",
                           prop.getName(),
                           JS_CLASSNAME,
                           prop.getConstantName(),
                           prop.getConstantName() );
    }
    for ( final OnPropChangeDescriptor onPropChange : onPropChanges )
    {
      final CodeBlock.Builder onChangeBlock = CodeBlock.builder();
      onChangeBlock.beginControlFlow( "if ( " +
                                      onPropChange.getProps()
                                        .stream()
                                        .map( PropDescriptor::getName )
                                        .collect( Collectors.joining( " && " ) ) + " )" );
      final StringBuilder sb = new StringBuilder();
      final ArrayList<Object> params = new ArrayList<>();
      sb.append( "$N( " );
      params.add( onPropChange.getMethod().getSimpleName().toString() );
      boolean requireComma = false;
      for ( final PropDescriptor prop : onPropChange.getProps() )
      {
        if ( requireComma )
        {
          sb.append( ", " );
        }
        requireComma = true;
        final String convertMethodName = getConverter( prop.getMethod().getReturnType(), prop.getMethod() );
        final TypeKind resultKind = prop.getMethod().getReturnType().getKind();
        if ( !resultKind.isPrimitive() && !isNonnull( prop.getMethod() ) )
        {
          sb.append( "$T.uncheckedCast( props.getAny( Props.$N ) )" );
          params.add( JS_CLASSNAME );
          params.add( prop.getConstantName() );
        }
        else
        {
          sb.append( "props.getAny( Props.$N ).$N()" );
          params.add( prop.getConstantName() );
          params.add( convertMethodName );
        }
      }

      sb.append( " )" );
      onChangeBlock.addStatement( sb.toString(), params.toArray() );
      onChangeBlock.endControlFlow();
      method.addCode( onChangeBlock.build() );
    }
  }

  @Nonnull
  private static MethodSpec.Builder buildComponentDidMount( @Nonnull final ComponentDescriptor descriptor )
  {
    final MethodSpec.Builder method = MethodSpec.methodBuilder( COMPONENT_DID_MOUNT_METHOD );
    final ExecutableElement postMount = descriptor.getPostMount();
    if ( null != postMount )
    {
      method.addStatement( "$N()", postMount.getSimpleName().toString() );
    }
    method.addStatement( "storeDebugDataAsState()" );

    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildComponentPreUpdate( @Nonnull final ComponentDescriptor descriptor )
  {
    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( COMPONENT_PRE_UPDATE_METHOD )
        .addParameter( ParameterSpec
                         .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "prevProps", Modifier.FINAL )
                         .addAnnotation( NULLABLE_CLASSNAME )
                         .build() );
    final boolean hasObservableProps = descriptor.hasObservableProps();
    final boolean hasPreUpdateOnPropChange = descriptor.hasPreUpdateOnPropChange();
    if ( hasObservableProps || hasPreUpdateOnPropChange )
    {
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "if ( null != prevProps )" );
      block.addStatement( "final $T props = props()", JS_PROPERTY_MAP_T_OBJECT_CLASSNAME );
      if ( hasObservableProps )
      {
        method.addAnnotation( AnnotationSpec.builder( ACTION_CLASSNAME )
                                .addMember( "verifyRequired", "false" )
                                .build() );
        method.addModifiers( Modifier.PROTECTED );

        block.addStatement( "reportPropChanges( prevProps, props, true )" );
      }
      else
      {
        method.addModifiers( Modifier.PRIVATE );
      }
      if ( hasPreUpdateOnPropChange )
      {
        block.addStatement( "preUpdateOnPropChange( prevProps, props )" );
      }
      block.endControlFlow();
      method.addCode( block.build() );
    }
    final ExecutableElement preUpdate = descriptor.getPreUpdate();
    if ( null != preUpdate )
    {
      method.addStatement( "$N()", preUpdate.getSimpleName().toString() );
    }
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildComponentDidUpdate( @Nonnull final ComponentDescriptor descriptor )
  {
    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( COMPONENT_DID_UPDATE_METHOD )
        .addModifiers( Modifier.FINAL )
        .addParameter( ParameterSpec
                         .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "prevProps", Modifier.FINAL )
                         .addAnnotation( NULLABLE_CLASSNAME )
                         .build() );

    final boolean hasPostUpdateOnPropChange = descriptor.hasPostUpdateOnPropChange();
    if ( hasPostUpdateOnPropChange )
    {
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "if ( null != prevProps )" );
      block.addStatement( "final $T props = props()", JS_PROPERTY_MAP_T_OBJECT_CLASSNAME );
      block.addStatement( "postUpdateOnPropChange( prevProps, props )" );
      block.endControlFlow();
      method.addCode( block.build() );
    }
    final ExecutableElement postUpdate = descriptor.getPostUpdate();
    if ( null != postUpdate )
    {
      method.addStatement( "$N()", postUpdate.getSimpleName().toString() );
    }
    method.addStatement( "storeDebugDataAsState()" );
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildPropValidatorMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( "validatePropValues" ).
        addAnnotation( Override.class ).
        addModifiers( Modifier.PROTECTED, Modifier.FINAL ).
        addParameter( ParameterSpec.builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "props", Modifier.FINAL ).
          addAnnotation( NONNULL_CLASSNAME ).build() );

    for ( final PropDescriptor prop : descriptor.getProps() )
    {
      final String name = prop.getName();
      final String rawName = "raw$" + name;
      final String typedName = "typed$" + name;
      method.addStatement( "final $T $N = props.get( Props.$N )", Object.class, rawName, prop.getConstantName() );
      final boolean isNonNull = isNonnull( prop.getMethod() );
      if ( !prop.isOptional() && isNonNull )
      {
        final CodeBlock.Builder block = CodeBlock.builder();
        block.beginControlFlow( "if ( $T.shouldCheckInvariants() )", REACT_CONFIG_CLASSNAME );
        block.addStatement( "$T.apiInvariant( () -> null != $N, () -> \"Required prop named '$N' is missing from " +
                            "component named '$N' so it was either incorrectly omitted or a null value has been " +
                            "incorrectly specified.\" ) ",
                            GUARDS_CLASSNAME,
                            rawName,
                            prop.getName(),
                            descriptor.getName() );
        block.endControlFlow();
        method.addCode( block.build() );
      }
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "if ( null != $N )", rawName );
      final TypeMirror returnType = prop.getMethodType().getReturnType();
      block.addStatement( "final $T $N = $T.$N( $N )",
                          returnType,
                          typedName,
                          JS_CLASSNAME,
                          getConverter( returnType, prop.getMethod() ),
                          rawName );
      if ( prop.hasValidateMethod() )
      {
        block.addStatement( "$N( $N )", prop.getValidateMethod().getSimpleName().toString(), typedName );
      }
      block.endControlFlow();
      method.addCode( block.build() );
    }
    return method;
  }

  @Nullable
  private static MethodSpec.Builder buildShouldComponentUpdateMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<PropDescriptor> props =
      descriptor
        .getProps()
        .stream()
        .filter( PropDescriptor::shouldUpdateOnChange )
        // Observable properties already checked in reportPropChanges
        .filter( p -> !p.isObservable() )
        .collect( Collectors.toList() );
    if ( props.isEmpty() )
    {
      return null;
    }
    else
    {
      final MethodSpec.Builder method = MethodSpec.methodBuilder( "shouldUpdateOnPropChanges" ).
        addModifiers( Modifier.PROTECTED ).
        addAnnotation( Override.class ).
        addParameter( ParameterSpec.builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "nextProps", Modifier.FINAL ).
          addAnnotation( NONNULL_CLASSNAME ).build() )
        .returns( TypeName.BOOLEAN );
      for ( final PropDescriptor prop : descriptor.getProps() )
      {
        final CodeBlock.Builder block = CodeBlock.builder();
        block.beginControlFlow( "if ( !$T.isTripleEqual( props().get( Props.$N ), nextProps.get( Props.$N ) ) )",
                                JS_CLASSNAME,
                                prop.getConstantName(),
                                prop.getConstantName() );
        block.addStatement( "return true" );
        block.endControlFlow();
        method.addCode( block.build() );
      }
      method.addStatement( "return false" );
      return method;
    }
  }

  private static FieldSpec.Builder buildProviderField( @Nonnull final ComponentDescriptor descriptor )
  {
    return FieldSpec.builder( ParameterizedTypeName.get( PROVIDER_CLASSNAME,
                                                         TypeName.get( descriptor.getDeclaredType() ) ),
                              "c_provider",
                              Modifier.STATIC,
                              Modifier.PRIVATE );
  }

  @Nonnull
  private static MethodSpec.Builder buildSetProviderMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    return MethodSpec.methodBuilder( "setProvider" ).
      addModifiers( Modifier.STATIC ).
      addParameter( ParameterizedTypeName.get( PROVIDER_CLASSNAME,
                                               TypeName.get( descriptor.getDeclaredType() ) ),
                    "provider",
                    Modifier.FINAL ).
      addStatement( "c_provider = provider" );
  }

  @Nonnull
  private static MethodSpec.Builder buildGetProviderMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    final MethodSpec.Builder method = MethodSpec.methodBuilder( "getProvider" ).
      addModifiers( Modifier.PRIVATE, Modifier.STATIC ).
      returns( ParameterizedTypeName.get( PROVIDER_CLASSNAME, TypeName.get( descriptor.getDeclaredType() ) ) );
    final CodeBlock.Builder block = CodeBlock.builder();
    block.beginControlFlow( "if ( $T.shouldCheckInvariants() )", REACT_CONFIG_CLASSNAME );
    block.addStatement(
      "$T.invariant( () -> null != c_provider, () -> \"Attempted to create an instance of the React4j " +
      "component named '$N' before the dependency injection provider has been initialized. Please see " +
      "the documentation at https://react4j.github.io/dependency_injection for directions how to " +
      "configure dependency injection.\" )",
      GUARDS_CLASSNAME,
      descriptor.getName() );
    block.endControlFlow();
    method.addCode( block.build() );
    return method.addStatement( "return c_provider" );
  }

  @Nonnull
  private static MethodSpec.Builder buildConstructorFnMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( "getConstructorFunction" ).
        addAnnotation( NONNULL_CLASSNAME ).
        addModifiers( Modifier.STATIC, Modifier.PRIVATE ).
        returns( COMPONENT_CONSTRUCTOR_FUNCTION_CLASSNAME );

    final boolean shouldGenerateLiteLifecycle = descriptor.shouldGenerateLiteLifecycle();
    if ( shouldGenerateLiteLifecycle )
    {
      method.addStatement( "final $T componentConstructor = ( $T.shouldStoreDebugDataAsState() || " +
                           "$T.shouldValidatePropValues() ) ? $T::new : $T::new",
                           COMPONENT_CONSTRUCTOR_FUNCTION_CLASSNAME,
                           REACT_CONFIG_CLASSNAME,
                           REACT_CONFIG_CLASSNAME,
                           ClassName.bestGuess( "NativeReactComponent" ),
                           ClassName.bestGuess( "LiteNativeReactComponent" ) );
    }
    else
    {
      method.addStatement( "final $T componentConstructor = $T::new",
                           COMPONENT_CONSTRUCTOR_FUNCTION_CLASSNAME,
                           ClassName.bestGuess( "NativeReactComponent" ) );
    }
    final CodeBlock.Builder codeBlock = CodeBlock.builder();
    codeBlock.beginControlFlow( "if ( $T.enableComponentNames() )", REACT_CONFIG_CLASSNAME );
    codeBlock.addStatement( "$T.asPropertyMap( componentConstructor ).set( \"displayName\", $S )",
                            JS_CLASSNAME,
                            descriptor.getName() );
    codeBlock.endControlFlow();

    method.addCode( codeBlock.build() );

    method.addStatement( "return componentConstructor" );
    return method;
  }

  @Nonnull
  private static TypeSpec buildPropsType( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( "Props" );

    //Ensure it can not be subclassed
    builder.addModifiers( Modifier.FINAL );
    builder.addModifiers( Modifier.STATIC );

    // These fields have been moved to a separate class to avoid a <clinit> on containing class

    final List<PropDescriptor> props = descriptor.getProps();
    final int propCount = props.size();
    for ( int i = 0; i < propCount; i++ )
    {
      builder.addField( buildPropKeyConstantField( props.get( i ), i ).build() );
    }

    return builder.build();
  }

  @Nonnull
  private static TypeSpec buildFactory()
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( "Factory" );

    //Ensure it can not be subclassed
    builder.addModifiers( Modifier.FINAL );
    builder.addModifiers( Modifier.STATIC );

    // This field has been moved to a separate class to avoid a <clinit> on containing class as that forces
    // every call to React_MyComponent to first check <clinit> has been invoked.
    final FieldSpec.Builder field =
      FieldSpec.builder( COMPONENT_CONSTRUCTOR_FUNCTION_CLASSNAME,
                         "TYPE",
                         Modifier.STATIC,
                         Modifier.FINAL ).
        initializer( "getConstructorFunction()" );
    builder.addField( field.build() );

    return builder.build();
  }

  @Nonnull
  private static TypeSpec buildInjectSupport( @Nonnull final ComponentDescriptor descriptor )
  {
    assert descriptor.needsInjection();
    final TypeSpec.Builder builder = TypeSpec.classBuilder( "InjectSupport" );

    //Ensure it can not be subclassed
    builder.addModifiers( Modifier.FINAL );
    builder.addModifiers( Modifier.STATIC );

    builder.addField( buildProviderField( descriptor ).build() );
    builder.addMethod( buildSetProviderMethod( descriptor ).build() );
    builder.addMethod( buildGetProviderMethod( descriptor ).build() );

    return builder.build();
  }

  @Nonnull
  private static TypeSpec buildNativeComponent( @Nonnull final ComponentDescriptor descriptor, final boolean lite )
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( ( lite ? "Lite" : "" ) + "NativeReactComponent" );

    //Ensure it can not be subclassed
    builder.addModifiers( Modifier.FINAL );
    builder.addModifiers( Modifier.STATIC );
    builder.addModifiers( Modifier.PRIVATE );

    final TypeName superType =
      ParameterizedTypeName.get( REACT_NATIVE_ADAPTER_COMPONENT_CLASSNAME, descriptor.getComponentType() );

    builder.superclass( superType );
    builder.addTypeVariables( ProcessorUtil.getTypeArgumentsAsNames( descriptor.getDeclaredType() ) );

    final List<MethodDescriptor> lifecycleMethods =
      lite ? descriptor.getLiteLifecycleMethods() : descriptor.getLifecycleMethods();
    if ( !lifecycleMethods.isEmpty() )
    {
      builder.addSuperinterface( ClassName.bestGuess( ( lite ? "Lite" : "" ) + "Lifecycle" ) );
    }

    // build the constructor
    {
      final ParameterSpec.Builder props =
        ParameterSpec.builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "props", Modifier.FINAL ).
          addAnnotation( NULLABLE_CLASSNAME );
      final MethodSpec.Builder method =
        MethodSpec.constructorBuilder().
          addParameter( props.build() );
      method.addAnnotation( JS_CONSTRUCTOR_CLASSNAME );
      method.addStatement( "super( props )" );
      builder.addMethod( method.build() );
    }

    // build createComponent
    {
      final MethodSpec.Builder method =
        MethodSpec.methodBuilder( "createComponent" ).
          addAnnotation( Override.class ).
          addModifiers( Modifier.PROTECTED ).
          returns( descriptor.getComponentType() );
      if ( descriptor.needsInjection() )
      {
        method.addStatement( "return InjectSupport.getProvider().get()" );
      }
      else
      {
        final String infix = asTypeArgumentsInfix( descriptor.getDeclaredType() );
        method.addStatement( "return new $T" + infix + "()", descriptor.getClassNameToConstruct() );
      }
      builder.addMethod( method.build() );
    }

    // Lifecycle methods
    for ( final MethodDescriptor lifecycleMethod : lifecycleMethods )
    {
      builder.addMethod( buildLifecycleMethod( descriptor, lifecycleMethod ).build() );
    }

    return builder.build();
  }

  @Nonnull
  private static MethodSpec.Builder buildLifecycleMethod( @Nonnull final ComponentDescriptor componentDescriptor,
                                                          @Nonnull final MethodDescriptor descriptor )
  {
    final String methodName = descriptor.getMethod().getSimpleName().toString();
    final boolean isPreUpdate = Constants.COMPONENT_PRE_UPDATE.equals( methodName );
    final TypeName returnType =
      isPreUpdate ? TypeName.get( Object.class ) : ClassName.get( descriptor.getMethodType().getReturnType() );
    final String actualMethodName = isPreUpdate ? "getSnapshotBeforeUpdate" : methodName;
    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( actualMethodName )
        .addModifiers( Modifier.PUBLIC )
        .addAnnotation( Override.class )
        .returns( returnType );

    ProcessorUtil.copyTypeParameters( descriptor.getMethodType(), method );

    if ( Constants.COMPONENT_PRE_UPDATE.equals( methodName ) )
    {
      method.addParameter( ParameterSpec
                             .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "prevProps", Modifier.FINAL )
                             .addAnnotation( NONNULL_CLASSNAME )
                             .build() );
      method.addParameter( ParameterSpec
                             .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "prevState", Modifier.FINAL )
                             .addAnnotation( NONNULL_CLASSNAME )
                             .build() );
      method.addStatement( "(($T) component() ).$N( prevProps )",
                           componentDescriptor.getClassNameToConstruct(),
                           COMPONENT_PRE_UPDATE_METHOD );
      method.addStatement( "return null" );
    }
    else if ( Constants.COMPONENT_DID_UPDATE.equals( methodName ) )
    {
      method.addParameter( ParameterSpec
                             .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "prevProps", Modifier.FINAL )
                             .addAnnotation( NONNULL_CLASSNAME )
                             .build() );
      method.addStatement( "(($T) component() ).$N( prevProps )",
                           componentDescriptor.getClassNameToConstruct(),
                           COMPONENT_DID_UPDATE_METHOD );
    }
    else if ( Constants.COMPONENT_DID_MOUNT.equals( methodName ) )
    {
      method.addStatement( "(($T) component() ).$N()",
                           componentDescriptor.getClassNameToConstruct(),
                           COMPONENT_DID_MOUNT_METHOD );
    }
    else if ( Constants.COMPONENT_WILL_UNMOUNT.equals( methodName ) )
    {
      method.addStatement( "performComponentWillUnmount()" );
    }
    else if ( Constants.SHOULD_COMPONENT_UPDATE.equals( methodName ) )
    {
      method.addParameter( ParameterSpec.builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "nextProps", Modifier.FINAL ).
        addAnnotation( NONNULL_CLASSNAME ).build() );
      method.addStatement( "return performShouldComponentUpdate( nextProps )" );
    }
    else
    {
      assert Constants.COMPONENT_DID_CATCH.equals( methodName );

      method.addParameter( ParameterSpec.builder( JS_ERROR_CLASSNAME, "error", Modifier.FINAL )
                             .addAnnotation( NONNULL_CLASSNAME )
                             .build() );
      method.addParameter( ParameterSpec.builder( REACT_ERROR_INFO_CLASSNAME, "info", Modifier.FINAL )
                             .addAnnotation( NONNULL_CLASSNAME )
                             .build() );
      method.addStatement( "performComponentDidCatch( error, info )" );
    }
    return method;
  }

  private static String asTypeArgumentsInfix( final DeclaredType declaredType )
  {
    final List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
    return typeArguments.isEmpty() ?
           "" :
           "<" + typeArguments.stream().map( TypeMirror::toString ).collect( Collectors.joining( ", " ) ) + ">";
  }

  @Nonnull
  private static TypeSpec buildNativeLiteLifecycleInterface( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.interfaceBuilder( "LiteLifecycle" );

    builder.addAnnotation( AnnotationSpec.builder( JS_TYPE_CLASSNAME ).
      addMember( "isNative", "true" ).
      addMember( "namespace", "$T.GLOBAL", JS_PACKAGE_CLASSNAME ).
      addMember( "name", "$S", "?" ).
      build() );

    builder.addModifiers( Modifier.STATIC );

    for ( final MethodDescriptor method : descriptor.getLiteLifecycleMethods() )
    {
      builder.addMethod( buildLifecycleMethodInterface( method ).build() );
    }

    return builder.build();
  }

  @Nonnull
  private static TypeSpec buildNativeLifecycleInterface( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.interfaceBuilder( "Lifecycle" );

    builder.addAnnotation( AnnotationSpec.builder( JS_TYPE_CLASSNAME ).
      addMember( "isNative", "true" ).
      addMember( "namespace", "$T.GLOBAL", JS_PACKAGE_CLASSNAME ).
      addMember( "name", "$S", "?" ).
      build() );

    builder.addModifiers( Modifier.STATIC );

    descriptor
      .getLifecycleMethods()
      .forEach( method -> builder.addMethod( buildLifecycleMethodInterface( method ).build() ) );

    return builder.build();
  }

  @Nonnull
  private static MethodSpec.Builder buildLifecycleMethodInterface( @Nonnull final MethodDescriptor lifecycleMethod )
  {
    final String methodName = lifecycleMethod.getMethod().getSimpleName().toString();
    final boolean isPreUpdate = Constants.COMPONENT_PRE_UPDATE.equals( methodName );
    final TypeName returnType =
      isPreUpdate ? TypeName.get( Object.class ) : ClassName.get( lifecycleMethod.getMethodType().getReturnType() );
    final String actualMethodName = isPreUpdate ? "getSnapshotBeforeUpdate" : methodName;
    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( actualMethodName )
        .addModifiers( Modifier.ABSTRACT, Modifier.PUBLIC )
        .returns( returnType );

    ProcessorUtil.copyTypeParameters( lifecycleMethod.getMethodType(), method );

    if ( isPreUpdate )
    {
      method.addParameter( ParameterSpec
                             .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "prevProps" )
                             .addAnnotation( NONNULL_CLASSNAME )
                             .build() );
      method.addParameter( ParameterSpec
                             .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "prevState" )
                             .addAnnotation( NONNULL_CLASSNAME )
                             .build() );
    }
    else if ( Constants.COMPONENT_DID_UPDATE.equals( methodName ) )
    {
      method.addParameter( ParameterSpec
                             .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "prevProps" )
                             .addAnnotation( NONNULL_CLASSNAME )
                             .build() );
    }
    else if ( Constants.SHOULD_COMPONENT_UPDATE.equals( methodName ) )
    {
      method.addParameter( ParameterSpec
                             .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "nextProps" )
                             .addAnnotation( NONNULL_CLASSNAME )
                             .build() );
    }
    else if ( Constants.COMPONENT_DID_CATCH.equals( methodName ) )
    {
      method.addParameter( ParameterSpec.builder( JS_ERROR_CLASSNAME, "error" )
                             .addAnnotation( NONNULL_CLASSNAME )
                             .build() );
      method.addParameter( ParameterSpec.builder( REACT_ERROR_INFO_CLASSNAME, "info" )
                             .addAnnotation( NONNULL_CLASSNAME )
                             .build() );
    }
    return method;
  }

  @Nonnull
  static TypeSpec buildDaggerFactory( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.interfaceBuilder( descriptor.getDaggerFactoryClassName() );
    addGeneratedAnnotation( descriptor, builder );
    addOriginatingTypes( descriptor.getElement(), builder );

    builder.addModifiers( Modifier.PUBLIC );

    {
      final MethodSpec.Builder method =
        MethodSpec.methodBuilder( "get" + descriptor.getName() + "DaggerSubcomponent" ).
          addModifiers( Modifier.PUBLIC, Modifier.ABSTRACT ).
          returns( ClassName.bestGuess( "DaggerSubcomponent" ) );
      builder.addMethod( method.build() );
    }
    {
      final MethodSpec.Builder method =
        MethodSpec.methodBuilder( "bind" + descriptor.getName() ).
          addModifiers( Modifier.PUBLIC, Modifier.DEFAULT );
      if ( descriptor.getElement().getModifiers().contains( Modifier.PUBLIC ) )
      {
        method.addStatement( "$T.InjectSupport.setProvider( $N().createProvider() )",
                             descriptor.getEnhancedClassName(),
                             "get" + descriptor.getName() + "DaggerSubcomponent" );
      }
      else
      {
        method.addStatement( "$T.InjectSupport.setProvider( () -> ($T) $N().createProvider().get() )",
                             descriptor.getEnhancedClassName(),
                             descriptor.getClassName(),
                             "get" + descriptor.getName() + "DaggerSubcomponent" );
      }
      builder.addMethod( method.build() );
    }

    if ( descriptor.needsDaggerIntegration() )
    {
      builder.addType( buildDaggerModule( descriptor ) );
      builder.addType( buildDaggerComponent( descriptor ) );
    }

    return builder.build();
  }

  @Nonnull
  private static TypeSpec buildDaggerComponent( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.interfaceBuilder( "DaggerSubcomponent" );

    builder.addModifiers( Modifier.PUBLIC, Modifier.STATIC );
    final AnnotationSpec.Builder subcomponent =
      AnnotationSpec.builder( ClassName.bestGuess( Constants.DAGGER_SUBCOMPONENT_CLASSNAME ) );
    subcomponent.addMember( "modules", "DaggerModule.class" );
    builder.addAnnotation( subcomponent.build() );

    if ( descriptor.getElement().getModifiers().contains( Modifier.PUBLIC ) )
    {
      final MethodSpec.Builder method =
        MethodSpec.methodBuilder( "createProvider" ).
          addModifiers( Modifier.ABSTRACT, Modifier.PUBLIC ).
          returns( ParameterizedTypeName.get( PROVIDER_CLASSNAME, descriptor.getClassName() ) );
      builder.addMethod( method.build() );
    }
    else
    {
      final MethodSpec.Builder method =
        MethodSpec.methodBuilder( "createProvider" ).
          addModifiers( Modifier.ABSTRACT, Modifier.PUBLIC ).
          returns( ParameterizedTypeName.get( PROVIDER_CLASSNAME, COMPONENT_CLASSNAME ) );
      builder.addMethod( method.build() );
    }

    return builder.build();
  }

  @Nonnull
  private static TypeSpec buildDaggerModule( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.interfaceBuilder( "DaggerModule" );

    builder.addModifiers( Modifier.PUBLIC, Modifier.STATIC );
    builder.addAnnotation( ClassName.bestGuess( Constants.DAGGER_MODULE_CLASSNAME ) );

    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( "bindComponent" ).
        addAnnotation( ClassName.bestGuess( Constants.DAGGER_BINDS_CLASSNAME ) ).
        addModifiers( Modifier.ABSTRACT, Modifier.PUBLIC ).
        addParameter( descriptor.getClassNameToConstruct(), "component" ).
        returns( descriptor.getClassName() );
    if ( descriptor.getElement().getModifiers().contains( Modifier.PUBLIC ) )
    {
      method.returns( descriptor.getClassName() );
    }
    else
    {
      method.returns( COMPONENT_CLASSNAME );
    }

    builder.addMethod( method.build() );

    return builder.build();
  }

  @Nonnull
  private static BuilderDescriptor buildBuilderDescriptor( @Nonnull final ComponentDescriptor descriptor )
  {
    final BuilderDescriptor builder = new BuilderDescriptor();

    Step optionalPropStep = null;
    final List<PropDescriptor> props = descriptor.getProps();

    final int propsSize = props.size();

    // Key step
    final Step keyStep = builder.addStep();
    final StepMethodType keyStepMethodType = 0 == propsSize ? StepMethodType.TERMINATE : StepMethodType.ADVANCE;
    keyStep.addMethod( "key", "key", TypeName.get( String.class ), keyStepMethodType );
    keyStep.addMethod( "key", "*key_int*", TypeName.INT, keyStepMethodType );

    final boolean hasSingleOptional = props.stream().filter( PropDescriptor::isOptional ).count() == 1;
    boolean hasRequiredAfterOptional = false;
    for ( int i = 0; i < propsSize; i++ )
    {
      final PropDescriptor prop = props.get( i );
      final boolean isLast = i == propsSize - 1;
      if ( prop.isOptional() )
      {
        if ( null == optionalPropStep )
        {
          optionalPropStep = builder.addStep();
        }
        if ( prop.getName().equals( "children" ) )
        {
          addChildrenStreamPropStepMethod( optionalPropStep );
        }
        addPropStepMethod( optionalPropStep, prop, hasSingleOptional ? StepMethodType.TERMINATE : StepMethodType.STAY );
      }
      else
      {
        if ( null != optionalPropStep )
        {
          // Need this when we have children magic prop that is required that follows the optional props.
          addPropStepMethod( optionalPropStep, prop, isLast ? StepMethodType.TERMINATE : StepMethodType.ADVANCE );
          // This is when children are built up using child steps
          if ( prop.getName().equals( "children" ) )
          {
            addChildrenStreamPropStepMethod( optionalPropStep );
          }
          hasRequiredAfterOptional = true;
        }
        // Single method step
        final Step step = builder.addStep();
        addPropStepMethod( step, prop, isLast ? StepMethodType.TERMINATE : StepMethodType.ADVANCE );
        if ( prop.getName().equals( "children" ) )
        {
          addChildrenStreamPropStepMethod( step );
          addBuildStep( step );
        }
      }
    }
    if ( null != optionalPropStep && !hasRequiredAfterOptional )
    {
      addBuildStep( optionalPropStep );
    }
    if ( props.isEmpty() )
    {
      addBuildStep( builder.addStep() );
    }

    return builder;
  }

  /**
   * Setup the "build" intrinsic step.
   */
  private static void addBuildStep( @Nonnull final Step step )
  {
    step.addMethod( "build", "build", REACT_NODE_CLASSNAME, StepMethodType.TERMINATE );
  }

  /**
   * A helper intrinsic that converts children streams.
   */
  private static void addChildrenStreamPropStepMethod( @Nonnull final Step step )
  {
    final ParameterizedTypeName typeName =
      ParameterizedTypeName.get( ClassName.get( Stream.class ), WildcardTypeName.subtypeOf( REACT_NODE_CLASSNAME ) );

    //TODO: Replace this with prop enhancer
    step.addMethod( "children",
                    "*children_stream*",
                    typeName,
                    StepMethodType.TERMINATE );
  }

  private static void addPropStepMethod( @Nonnull final Step step,
                                         @Nonnull final PropDescriptor prop,
                                         @Nonnull final StepMethodType stepMethodType )
  {
    step.addMethod( prop, stepMethodType );
  }

  private static void addOriginatingTypes( @Nonnull final TypeElement element, @Nonnull final TypeSpec.Builder builder )
  {
    builder.addOriginatingElement( element );
    ProcessorUtil.getSuperTypes( element ).forEach( builder::addOriginatingElement );
  }

  private static void addGeneratedAnnotation( @Nonnull final ComponentDescriptor descriptor,
                                              @Nonnull final TypeSpec.Builder builder )
  {
    GeneratedAnnotationSpecs
      .generatedAnnotationSpec( descriptor.getElements(), descriptor.getSourceVersion(), ReactProcessor.class )
      .ifPresent( builder::addAnnotation );
  }
}
