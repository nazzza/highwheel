package org.pitest.highwheel.bytecodeparser;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.objectweb.asm.Type;
import org.pitest.highwheel.bytecodeparser.classpath.ClassLoaderClassPathRoot;
import org.pitest.highwheel.classpath.AccessVisitor;
import org.pitest.highwheel.classpath.ClasspathRoot;
import org.pitest.highwheel.cycles.Filter;
import org.pitest.highwheel.cycles.MethodDependencyGraphBuildingVisitor;
import org.pitest.highwheel.model.AccessPoint;
import org.pitest.highwheel.model.AccessPointName;
import org.pitest.highwheel.model.ElementName;
import org.pitest.highwheel.orphans.OrphanAnalyser;

import com.example.CallsFooMethod;
import com.example.Foo;
import com.example.HasFooAsMember;
import com.example.HasFooAsParameter;
import com.example.Unconnected;

import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class OrphanAnalyserSystemTest {

  private OrphanAnalyser<AccessPoint, Integer> testee = new OrphanAnalyser<AccessPoint, Integer>();
  private MethodDependencyGraphBuildingVisitor mdgbv  = new MethodDependencyGraphBuildingVisitor(
      new DirectedSparseGraph<AccessPoint, Integer>());

  private ClassPathParser cpp;

  @Mock
  private AccessVisitor v;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void shouldReturnEmptyGraphWhenNoClass() {
    parseClassPath();
    assertThat(mdgbv.getGraph().getVertexCount()).isEqualTo(0);
  }

  @Test
  public void shouldReturnUncalledMethodWhenSingleClass() {
    parseClassPath(Foo.class);
    List<AccessPoint> ep = new ArrayList<AccessPoint>();
    assertThat(testee.findOrphans(mdgbv.getGraph(), ep))
        .contains(access(Foo.class, method("aMethod", Object.class)));
  }

  @Test
  public void shouldNotReturnUncalledMethodWhenSingleClassWithEntryPoint() {
    parseClassPath(Foo.class);
    List<AccessPoint> ep = new ArrayList<AccessPoint>();
    ep.add(access(Foo.class, method("aMethod", Object.class)));
    assertThat(testee.findOrphans(mdgbv.getGraph(), ep))
        .doesNotContain(access(Foo.class, method("aMethod", Object.class)));
  }

  @Test
  public void shouldReturnNoMethodsWhenClassEmpty() {
    parseClassPath(Unconnected.class);
    List<AccessPoint> ep = new ArrayList<AccessPoint>();
    assertThat(testee.findOrphans(mdgbv.getGraph(), ep)).hasSize(1);
  }

  @Test
  public void shouldReturnUnconnectedMethodsWhenClasessWithNoEntryPoint() {
    parseClassPath(Foo.class, HasFooAsParameter.class);
    List<AccessPoint> ep = new ArrayList<AccessPoint>();
    assertThat(testee.findOrphans(mdgbv.getGraph(), ep)).contains(
        access(HasFooAsParameter.class, methodWithParameter("foo", Foo.class)),
        access(Foo.class, method("aMethod", Object.class)));
  }

  @Test
  public void shouldNotReturnUnconnectedMethodsWhenClasessWithEntryPoint() {
    parseClassPath(Foo.class, CallsFooMethod.class);
    List<AccessPoint> ep = new ArrayList<AccessPoint>();
    ep.add(access(CallsFooMethod.class, method("foo", Object.class)));
    assertThat(testee.findOrphans(mdgbv.getGraph(), ep)).hasSize(2);
  }

  @Test
  public void shouldReturnNoMethodsWhenClassWithFieldsNoMethods() {
    parseClassPath(HasFooAsMember.class);
    List<AccessPoint> ep = new ArrayList<AccessPoint>();
    assertThat(testee.findOrphans(mdgbv.getGraph(), ep)).hasSize(1);
  }

  private Filter matchOnlyExampleDotCom() {
    return new Filter() {

      @Override
      public boolean include(final ElementName item) {
        return item.asJavaName().startsWith("com.example");
      }

    };
  }

  private void parseClassPath(final Class<?>... classes) {
    try {
      this.cpp = makeToSeeOnlyExampleDotCom();
      this.cpp.parse(createRootFor(classes), this.mdgbv);
    } catch (final IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private ClassPathParser makeToSeeOnlyExampleDotCom() {
    return new ClassPathParser(matchOnlyExampleDotCom());
  }

  private ClasspathRoot createRootFor(final Class<?>[] classes) {
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

  private AccessPoint accessAType(final Class<?> type) {
    return AccessPoint.create(ElementName.fromClass(type));
  }

  private AccessPoint access(final Class<?> type,
      final AccessPointName method) {
    return AccessPoint.create(ElementName.fromClass(type), method);
  }

  private AccessPointName methodWithParameter(String name, Class<?> paramType) {
    return AccessPointName.create(name,
        Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(paramType)));
  }

  private AccessPointName method(String name, String desc) {
    return AccessPointName.create(name, desc);
  }

  private AccessPointName method(String name, Class<?> retType) {
    return AccessPointName.create(name,
        Type.getMethodDescriptor(Type.getType(retType)));
  }

}
