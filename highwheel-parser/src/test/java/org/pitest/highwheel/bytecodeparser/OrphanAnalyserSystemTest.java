package org.pitest.highwheel.bytecodeparser;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.objectweb.asm.Type;
import org.pitest.highwheel.bytecodeparser.classpath.ClassLoaderClassPathRoot;
import org.pitest.highwheel.classpath.ClasspathRoot;
import org.pitest.highwheel.cycles.EntryPointRecogniser;
import org.pitest.highwheel.cycles.Filter;
import org.pitest.highwheel.model.AccessPoint;
import org.pitest.highwheel.model.AccessPointName;
import org.pitest.highwheel.model.ElementName;
import org.pitest.highwheel.orphans.OrphanAnalysis;

import com.example.CallsFooMethod;
import com.example.CallsMethodFromKoo;
import com.example.ClassWithAPrivateEnum;
import com.example.ExtendsKoo;
import com.example.ExtendsMooWithAMethod;
import com.example.Foo;
import com.example.FooWithAVoidMethod;
import com.example.HasAMisleadingMethodName;
import com.example.HasFooAsMember;
import com.example.HasFooAsParameter;
import com.example.Koo;
import com.example.MooWithAMethod;
import com.example.Unconnected;
import com.example.InheritanceCall.AChildImplementingAParentWithAMethod;
import com.example.InheritanceCall.AParentWithAMethod;
import com.example.InheritanceCall.EntryPointForInheritanceCall;
import com.example.enums.FooEnum;
import com.example.interfaces.ExtendsTop;
import com.example.interfaces.ImplementsExtendsTop;
import com.example.interfaces.Top;
import com.example.scenarios.MemberOfCycle1;
import com.example.scenarios.MemberOfCycle2;
import com.example.scenarios.Inheritance.ChildClassExtendsParentClass;
import com.example.scenarios.Inheritance.EntryPointInheritace;
import com.example.scenarios.Inheritance.ParentClass;
import com.example.scenarios.InterfaceCall.AnInterfaceWithAMethod;
import com.example.scenarios.InterfaceCall.EntryPoint;
import com.example.scenarios.InterfaceCall.ImplementsAnInterfaceWithAMethod;

public class OrphanAnalyserSystemTest {

  private OrphanAnalysis testee;

  @Mock
  private EntryPointRecogniser epr;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    testee = new OrphanAnalysis(makeToSeeOnlyExampleDotCom());

  }

  @Test
  public void shouldReturnUncalledMethodWhenSingleClass() throws IOException {
    setUpNoEntryPoints();
    assertThat(testee.findOrphans(createRootFor(Foo.class)))
        .contains(access(Foo.class, method("aMethod", Object.class)));
  }

  @Test
  public void shouldReturnNoMethodsWhenClassEmpty() throws IOException {
    assertThat(testee.findOrphans(createRootFor(Unconnected.class))).isEmpty();
  }

  @Test
  public void shouldReturnUnconnectedMethodsWhenClasessWithNoEntryPoint()
      throws IOException {
    setUpNoEntryPoints();
    assertThat(
        testee.findOrphans(createRootFor(Foo.class, HasFooAsParameter.class)))
            .contains(
                access(HasFooAsParameter.class,
                    methodWithParameter("foo", Foo.class)),
                access(Foo.class, method("aMethod", Object.class)));
  }

  @Test
  public void shouldReturnNoUncalledMethodWhenSingleClassWithEntryPoint()
      throws IOException {
    when(this.epr.isEntryPoint(anyInt(), eq("aMethod"), anyString()))
        .thenReturn(true);
    assertThat(testee.findOrphans(createRootFor(Foo.class)))
        .doesNotContain(access(Foo.class, method("aMethod", Object.class)));
  }

  @Test
  public void shouldNotReturnUnconnectedMethodsWhenClasessWithEntryPoint()
      throws IOException {
    when(this.epr.isEntryPoint(anyInt(), eq("foo"), anyString()))
        .thenReturn(true);
    assertThat(
        testee.findOrphans(createRootFor(Foo.class, CallsFooMethod.class)))
            .isEmpty();
  }

  @Test
  public void shouldReturnNoMethodsWhenClassWithFieldsNoMethods()
      throws IOException {
    assertThat(testee.findOrphans(createRootFor(HasFooAsMember.class)))
        .isEmpty();
  }

  @Test
  public void shouldReturnNoMethodsWhenConnectedToEntryPointInCircle()
      throws IOException {
    when(this.epr.isEntryPoint(anyInt(), eq("entry"), anyString()))
        .thenReturn(true);
    assertThat(testee
        .findOrphans(createRootFor(MemberOfCycle1.class, MemberOfCycle2.class)))
            .isEmpty();
  }

  @Test
  public void shouldReturnUnconnectedMethodCalledInit() throws IOException {
    assertThat(
        testee.findOrphans(createRootFor(HasAMisleadingMethodName.class)))
            .isNotEmpty();
  }

  @Test
  public void shouldReturnAVoidOrphanMethodWhenSingleClass()
      throws IOException {
    assertThat(testee.findOrphans(createRootFor(FooWithAVoidMethod.class)))
        .isNotEmpty();
  }

  @Test
  public void shouldReturnAMethodImplementedFromAnInterfaceIfNeverCalledThroughAnInterface()
      throws IOException {
    when(this.epr.isEntryPoint(anyInt(), eq("entryPoint"), anyString()))
        .thenReturn(true);
    assertThat(
        testee.findOrphans(createRootFor(ImplementsAnInterfaceWithAMethod.class,
            EntryPoint.class, AnInterfaceWithAMethod.class)))
                .contains(access(AnInterfaceWithAMethod.class,
                    method("aMethodToImplement", Object.class)));
  }

  @Test
  public void shouldReturnAMethodFromAParentClassIfNeverCalledThroughAParentClass()
      throws IOException {
    when(this.epr.isEntryPoint(anyInt(), eq("entryPoint"), anyString()))
        .thenReturn(true);
    assertThat(testee.findOrphans(createRootFor(ParentClass.class,
        ChildClassExtendsParentClass.class, EntryPointInheritace.class)))
            .contains(access(ParentClass.class,
                method("parentMethod", Object.class)));
  }

  @Test
  public void shouldReturnOrphanMethodsWhenNoEntryPointInheritance()
      throws IOException {
    setUpNoEntryPoints();
    assertThat(testee.findOrphans(createRootFor(AParentWithAMethod.class,
        AChildImplementingAParentWithAMethod.class,
        EntryPointForInheritanceCall.class))).hasSize(3);
  }

  private void setUpNoEntryPoints() {
    when(this.epr.isEntryPoint(anyInt(), anyString(), anyString()))
        .thenReturn(false);
  }

  @Test
  public void shouldReturnOrphanMethodsWhenNoEntryPointInterface()
      throws IOException {
    setUpNoEntryPoints();
    assertThat(
        testee.findOrphans(createRootFor(ImplementsAnInterfaceWithAMethod.class,
            EntryPoint.class, AnInterfaceWithAMethod.class))).hasSize(3);
  }

  @Test
  public void shouldReturnOrphanMethodsWhenNoEntryPointUnrelatedMethodsAndInheritance()
      throws IOException {
    when(this.epr.isEntryPoint(anyInt(), eq("aMethod"), anyString()))
        .thenReturn(true);
    assertThat(testee.findOrphans(createRootFor(Foo.class, MooWithAMethod.class,
        ExtendsMooWithAMethod.class))).isEmpty();
  }

  @Test
  public void shouldReturnOrphanMethodWhenEntryPointInInheritance()
      throws IOException {
    when(this.epr.isEntryPoint(anyInt(), eq("entryPoint"), anyString()))
        .thenReturn(true);
    assertThat(testee.findOrphans(createRootFor(Foo.class, Koo.class,
        ExtendsKoo.class, CallsMethodFromKoo.class)))
            .containsOnly(access(Foo.class, method("aMethod", Object.class)));
  }

  @Test
  public void shouldNotNullPointerWhenEnumsOnClasspath() throws IOException {
    try {
      testee.findOrphans(createRootFor(FooEnum.class));
      // pass
    } catch (NullPointerException x) {
      fail();
    }
  }

  // ??
  @Test
  public void shouldNotBeEmptyWhenPrivateEnumInClass() throws IOException {
    assertThat(testee.findOrphans(createRootFor(ClassWithAPrivateEnum.class,
        ClassWithAPrivateEnum.ClassType.class))).isNotEmpty();
  }

  @Test
  public void shouldDoStuff() throws IOException {
    assertThat(testee.findOrphans(
        createRootFor(Top.class, ExtendsTop.class, ImplementsExtendsTop.class)))
            .isNotEmpty();
  }

  private Filter matchOnlyExampleDotCom() {
    return new Filter() {

      @Override
      public boolean include(final ElementName item) {
        return item.asJavaName().startsWith("com.example");
      }

    };
  }

  private ClassPathParser makeToSeeOnlyExampleDotCom() {
    return new ClassPathParser(matchOnlyExampleDotCom(), epr);
  }

  private ClasspathRoot createRootFor(final Class<?>... classes) {
    final Collection<ElementName> elements = new ArrayList<ElementName>();
    final ClassLoaderClassPathRoot data = new ClassLoaderClassPathRoot(
        Thread.currentThread().getContextClassLoader());

    for (final Class<?> each : classes) {
      final ElementName element = ElementName.fromClass(each);
      elements.add(element);
      elements.addAll(first3InnerClassesIfPresent(element, data));
    }

    return new ClasspathRoot() {
      @Override
      public InputStream getData(final ElementName name) throws IOException {
        return data.getData(name);
      }

      @Override
      public Collection<ElementName> classNames() {
        return elements;
      }

      @Override
      public InputStream getResource(final String name) throws IOException {
        return data.getResource(name);
      }

    };

  }

  private Collection<? extends ElementName> first3InnerClassesIfPresent(
      final ElementName element, final ClassLoaderClassPathRoot data) {
    final Collection<ElementName> innerClasses = new ArrayList<ElementName>();
    try {
      for (int i = 1; i != 4; i++) {
        final ElementName innerClass = ElementName
            .fromString(element.asJavaName() + "$" + i);
        if (data.getData(innerClass) != null) {
          innerClasses.add(innerClass);
        }
      }
      return innerClasses;
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  private AccessPoint access(final Class<?> type,
      final AccessPointName method) {
    return AccessPoint.create(ElementName.fromClass(type), method);
  }

  private AccessPointName methodWithParameter(final String name,
      final Class<?> paramType) {
    return AccessPointName.create(name,
        Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(paramType)));
  }

  private AccessPointName method(final String name, final Class<?> retType) {
    return AccessPointName.create(name,
        Type.getMethodDescriptor(Type.getType(retType)));
  }

}
