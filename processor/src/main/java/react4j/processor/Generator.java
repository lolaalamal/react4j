package react4j.processor;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import elemental2.core.JsObject;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import jsinterop.base.JsPropertyMapOfAny;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;
import react4j.core.util.JsUtil;

final class Generator
{
  private Generator()
  {
  }

  @Nonnull
  static TypeSpec buildConstructorFactory( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeElement element = descriptor.getElement();

    final String name = descriptor.getNestedClassPrefix() + descriptor.getConstructorFactoryName();
    final TypeSpec.Builder builder = TypeSpec.classBuilder( name );

    ProcessorUtil.copyAccessModifiers( element, builder );

    //Ensure it can not be subclassd
    builder.addModifiers( Modifier.FINAL );

    // Mark it as generated
    builder.addAnnotation( AnnotationSpec.builder( Generated.class ).
      addMember( "value", "$S", ReactProcessor.class.getName() ).
      build() );

    final FieldSpec.Builder field =
      FieldSpec.builder( getJsConstructorFnType( descriptor ),
                         "TYPE",
                         Modifier.STATIC,
                         Modifier.FINAL,
                         Modifier.PUBLIC ).
        initializer( "getConstructorFunction()" );
    builder.addField( field.build() );

    builder.addMethod( buildConstructorFnMethod( descriptor ).build() );

    for ( final EventHandlerDescriptor eventHandler : descriptor.getEventHandlers() )
    {
      builder.addMethod( buildEventHandlerMethod( descriptor, eventHandler ).build() );
    }

    return builder.build();
  }

  @Nonnull
  private static ParameterizedTypeName getJsConstructorFnType( @Nonnull final ComponentDescriptor descriptor )
  {
    return ParameterizedTypeName.get( ClassName.get( ComponentConstructorFunction.class ),
                                      TypeName.get( descriptor.getPropsType().asType() ),
                                      TypeName.get( descriptor.getStateType().asType() ),
                                      descriptor.getNativeComponentClassName() );
  }

  @Nonnull
  private static MethodSpec.Builder buildEventHandlerMethod( @Nonnull final ComponentDescriptor descriptor,
                                                             @Nonnull final EventHandlerDescriptor eventHandler )
  {
    final TypeName handlerType = TypeName.get( eventHandler.getEventHandlerType().asType() );
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( "_" + eventHandler.getMethod().getSimpleName() ).
        addAnnotation( Nonnull.class ).
        returns( handlerType );

    method.addModifiers( Modifier.STATIC );

    final ParameterSpec.Builder parameter =
      ParameterSpec.builder( TypeName.get( descriptor.getElement().asType() ), "component", Modifier.FINAL ).
        addAnnotation( Nonnull.class );
    method.addParameter( parameter.build() );

    final ExecutableElement target = eventHandler.getEventHandlerMethod();
    final int targetParameterCount = target.getParameters().size();
    String args =
      0 == targetParameterCount ?
      "()" :
      IntStream.range( 0, targetParameterCount ).mapToObj( i -> "arg" + i ).collect( Collectors.joining( "," ) );
    if ( 1 < targetParameterCount )
    {
      args = "(" + args + ")";
    }

    final int paramCount = eventHandler.getMethod().getParameters().size();
    final String params =
      0 == paramCount ?
      "" :
      IntStream.range( 0, paramCount ).mapToObj( i -> "arg" + i ).collect( Collectors.joining( "," ) );

    method.addStatement( "final $T handler = " + args + " -> component.$N(" + params + ")",
                         handlerType,
                         eventHandler.getMethod().getSimpleName() );

    final CodeBlock.Builder block = CodeBlock.builder();
    block.beginControlFlow( "if( $T.enableComponentNames() )", ReactConfig.class );
    final String code =
      "$T.defineProperty( $T.cast( handler ), \"name\", $T.cast( JsPropertyMap.of( \"value\", $S ) ) )";
    block.addStatement( code,
                        JsObject.class,
                        Js.class,
                        Js.class,
                        descriptor.getName() + "." + eventHandler.getName() );
    block.endControlFlow();
    method.addCode( block.build() );
    method.addStatement( "return handler" );
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildConstructorFnMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    final ParameterizedTypeName constructorType = getJsConstructorFnType( descriptor );

    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( "getConstructorFunction" ).
        addAnnotation( Nonnull.class ).
        addModifiers( Modifier.STATIC, Modifier.PRIVATE ).
        returns( constructorType );

    method.addStatement( "final $T componentConstructor = $T::new",
                         constructorType,
                         descriptor.getNativeComponentClassName() );
    final CodeBlock.Builder codeBlock = CodeBlock.builder();
    codeBlock.beginControlFlow( "if ( $T.enableComponentNames() )", ReactConfig.class );
    codeBlock.addStatement( "$T.of( componentConstructor ).set( \"displayName\", $S )",
                            JsPropertyMap.class,
                            descriptor.getName() );
    codeBlock.endControlFlow();

    method.addCode( codeBlock.build() );
    method.addStatement( "return componentConstructor" );
    return method;
  }

  @Nonnull
  static TypeSpec buildNativeComponent( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeElement element = descriptor.getElement();

    final String name = descriptor.getNestedClassPrefix() + descriptor.getNativeComponentName();
    final TypeSpec.Builder builder = TypeSpec.classBuilder( name );

    //Ensure it can not be subclassed
    builder.addModifiers( Modifier.FINAL );

    // Mark it as generated
    builder.addAnnotation( AnnotationSpec.builder( Generated.class ).
      addMember( "value", "$S", ReactProcessor.class.getName() ).
      build() );

    final TypeName superType =
      ParameterizedTypeName.get( ClassName.get( NativeAdapterComponent.class ),
                                 ClassName.get( descriptor.getPropsType().asType() ),
                                 ClassName.get( descriptor.getStateType().asType() ),
                                 ClassName.get( descriptor.getElement() ) );

    builder.superclass( superType );

    builder.addSuperinterface( descriptor.getNativeLifecycleInterfaceClassName() );

    // build the constructor
    {
      final ParameterSpec.Builder props =
        ParameterSpec.builder( ClassName.get( descriptor.getPropsType() ), "props", Modifier.FINAL ).
          addAnnotation( Nonnull.class );
      final MethodSpec.Builder method =
        MethodSpec.constructorBuilder().
          addAnnotation( JsConstructor.class ).
          addParameter( props.build() );
      method.addStatement( "super( props )" );
      builder.addMethod( method.build() );
    }

    // build createComponent
    {
      final MethodSpec.Builder method =
        MethodSpec.methodBuilder( "createComponent" ).
          addAnnotation( Override.class ).
          addModifiers( Modifier.PROTECTED ).
          returns( ClassName.get( descriptor.getElement() ) );
      final ClassName className = ClassName.get( descriptor.getElement() );
      final ArrayList<String> names = new ArrayList<>( className.simpleNames() );
      final String cname = ( descriptor.isArezComponent() ? "Arez_" : "" ) + element.getSimpleName();
      final ClassName target;
      if ( names.size() > 1 )
      {
        names.remove( names.size() - 1 );
        names.add( cname );
        target =
          ClassName.get( className.packageName(),
                         names.get( 0 ),
                         names.subList( 1, names.size() ).toArray( new String[ 0 ] ) );
      }
      else
      {
        target = ClassName.get( className.packageName(), cname );
      }
      method.addStatement( "return new $T()", target );
      builder.addMethod( method.build() );
    }

    // Lifecycle methods
    {

      for ( final MethodDescriptor lifecycleMethod : descriptor.getLifecycleMethods() )
      {
        final String methodName = lifecycleMethod.getMethod().getSimpleName().toString();
        final MethodSpec.Builder method =
          MethodSpec.methodBuilder( methodName ).
            addModifiers( Modifier.PUBLIC ).
            addAnnotation( Override.class ).
            returns( ClassName.get( lifecycleMethod.getMethodType().getReturnType() ) );

        ProcessorUtil.copyTypeParameters( lifecycleMethod.getMethodType(), method );

        final StringJoiner params = new StringJoiner( "," );
        final List<? extends VariableElement> sourceParameters = lifecycleMethod.getMethod().getParameters();
        final List<? extends TypeMirror> sourceParameterTypes = lifecycleMethod.getMethodType().getParameterTypes();
        final int parameterCount = sourceParameters.size();
        for ( int i = 0; i < parameterCount; i++ )
        {
          final VariableElement parameter = sourceParameters.get( i );
          final TypeMirror parameterType = sourceParameterTypes.get( i );
          final String parameterName = parameter.getSimpleName().toString();
          final ParameterSpec.Builder parameterSpec =
            ParameterSpec.builder( TypeName.get( parameterType ), parameterName, Modifier.FINAL ).
              addAnnotation( Nonnull.class );
          method.addParameter( parameterSpec.build() );
          params.add( parameterName );
        }

        final StringBuilder sb = new StringBuilder();
        if ( TypeKind.VOID != lifecycleMethod.getMethodType().getReturnType().getKind() )
        {
          sb.append( "return " );
        }

        sb.append( "perform" );
        sb.append( Character.toUpperCase( methodName.charAt( 0 ) ) );
        sb.append( methodName.substring( 1 ) );
        sb.append( "(" );
        sb.append( params.toString() );
        sb.append( ")" );

        method.addStatement( sb.toString() );
        builder.addMethod( method.build() );
      }
    }

    return builder.build();
  }

  @Nonnull
  static TypeSpec buildNativeLifecycleInterface( @Nonnull final ComponentDescriptor descriptor )
  {
    final String name = descriptor.getNestedClassPrefix() + descriptor.getNativeLifecycleInterfaceName();
    final TypeSpec.Builder builder = TypeSpec.interfaceBuilder( name );

    // Mark it as generated
    builder.addAnnotation( AnnotationSpec.builder( Generated.class ).
      addMember( "value", "$S", ReactProcessor.class.getName() ).
      build() );

    builder.addAnnotation( AnnotationSpec.builder( JsType.class ).
      addMember( "isNative", "true" ).
      build() );
    // Lifecycle methods
    {

      for ( final MethodDescriptor lifecycleMethod : descriptor.getLifecycleMethods() )
      {
        final String methodName = lifecycleMethod.getMethod().getSimpleName().toString();
        final MethodSpec.Builder method =
          MethodSpec.methodBuilder( methodName ).
            addModifiers( Modifier.ABSTRACT, Modifier.PUBLIC ).
            returns( ClassName.get( lifecycleMethod.getMethodType().getReturnType() ) );

        ProcessorUtil.copyTypeParameters( lifecycleMethod.getMethodType(), method );

        final List<? extends VariableElement> sourceParameters = lifecycleMethod.getMethod().getParameters();
        final List<? extends TypeMirror> sourceParameterTypes = lifecycleMethod.getMethodType().getParameterTypes();
        final int parameterCount = sourceParameters.size();
        for ( int i = 0; i < parameterCount; i++ )
        {
          final VariableElement parameter = sourceParameters.get( i );
          final TypeMirror parameterType = sourceParameterTypes.get( i );
          final String parameterName = parameter.getSimpleName().toString();
          final ParameterSpec.Builder parameterSpec =
            ParameterSpec.builder( TypeName.get( parameterType ), parameterName ).addAnnotation( Nonnull.class );
          method.addParameter( parameterSpec.build() );
        }

        builder.addMethod( method.build() );
      }
    }

    return builder.build();
  }
}
