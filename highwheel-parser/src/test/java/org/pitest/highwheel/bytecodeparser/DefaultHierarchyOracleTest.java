package org.pitest.highwheel.bytecodeparser;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.fest.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.pitest.highwheel.bytecodeparser.classpath.ClassLoaderClassPathRoot;
import org.pitest.highwheel.classpath.ClasspathRoot;
import org.pitest.highwheel.cycles.DefaultHierarchyOracle;
import org.pitest.highwheel.cycles.Filter;
import org.pitest.highwheel.cycles.InheritanceDependencyMapBuildingVisitor;
import org.pitest.highwheel.model.ElementName;

import com.example.ExtendsFoo;
import com.example.ExtendsMoo;
import com.example.Foo;
import com.example.Moo;
import com.example.scenarios.DoubleInheritance.ExtendsFoo2ndTime;
import com.example.scenarios.DoubleInheritance.ExtendsMoo2ndTime;
import com.example.scenarios.TripleInheritance.ExtendsFoo3rdTime;

public class DefaultHierarchyOracleTest {

  private DefaultHierarchyOracle                  testee;
  private ClassPathParser                         cpp;
  private InheritanceDependencyMapBuildingVisitor idmbv;

  @Before
  public void setUp() throws Exception {
    idmbv = new InheritanceDependencyMapBuildingVisitor();
  }

  @Test
  public void shouldReturnASetWithTwoParentsWhenDoubleInheritance() {
    parseClassPath(Foo.class, ExtendsFoo.class, ExtendsFoo2ndTime.class);
    Map<ElementName, Set<ElementName>> m = new LinkedHashMap<ElementName, Set<ElementName>>();
    m.put(element(ExtendsFoo2ndTime.class),
        Collections.set(element(ExtendsFoo.class)));
    m.put(element(ExtendsFoo.class), Collections.set(element(Foo.class)));
    testee = new DefaultHierarchyOracle(m);
    assertThat(testee.findParents(element(ExtendsFoo2ndTime.class)))
        .contains(element(ExtendsFoo.class));
  }

  @Test
  public void shouldReturnASetWithThreeParentsWhenTripleInheritance() {
    parseClassPath(Foo.class, ExtendsFoo.class, ExtendsFoo2ndTime.class,
        ExtendsFoo3rdTime.class);
    Map<ElementName, Set<ElementName>> m = new LinkedHashMap<ElementName, Set<ElementName>>();
    m.put(element(ExtendsFoo3rdTime.class),
        Collections.set(element(ExtendsFoo2ndTime.class)));
    m.put(element(ExtendsFoo2ndTime.class),
        Collections.set(element(ExtendsFoo.class)));
    m.put(element(ExtendsFoo.class), Collections.set(element(Foo.class)));
    testee = new DefaultHierarchyOracle(m);
    assertThat(testee.findParents(element(ExtendsFoo3rdTime.class)))
        .contains(element(ExtendsFoo2ndTime.class));
  }

  @Test
  public void shouldReturnDifferentSetOfParentsForDifferentChildren() {
    parseClassPath(Foo.class, ExtendsFoo.class, ExtendsFoo2ndTime.class,
        Moo.class, ExtendsMoo.class, ExtendsMoo2ndTime.class);
    Map<ElementName, Set<ElementName>> m = new LinkedHashMap<ElementName, Set<ElementName>>();
    m.put(element(ExtendsFoo2ndTime.class),
        Collections.set(element(ExtendsFoo.class)));
    m.put(element(ExtendsFoo.class), Collections.set(element(Foo.class)));
    m.put(element(ExtendsMoo2ndTime.class),
        Collections.set(element(ExtendsMoo.class)));
    m.put(element(ExtendsMoo.class), Collections.set(element(Moo.class)));
    testee = new DefaultHierarchyOracle(m);
    assertThat(testee.findParents(element(ExtendsFoo2ndTime.class)))
        .containsOnly(element(ExtendsFoo.class));
    assertThat(testee.findParents(element(ExtendsMoo2ndTime.class)))
        .containsOnly(element(ExtendsMoo.class));
  }

  @Test
  public void shouldReturnASetWithTwoChildrenWhenDoubleInheritance() {
    parseClassPath(Foo.class, ExtendsFoo.class, ExtendsFoo2ndTime.class);
    Map<ElementName, Set<ElementName>> m = new LinkedHashMap<ElementName, Set<ElementName>>();
    m.put(element(ExtendsFoo2ndTime.class),
        Collections.set(element(ExtendsFoo.class)));
    m.put(element(ExtendsFoo.class), Collections.set(element(Foo.class)));
    testee = new DefaultHierarchyOracle(m);
    assertThat(testee.findChildren(element(Foo.class)))
        .contains(element(ExtendsFoo.class), element(ExtendsFoo2ndTime.class));
  }

  @Test
  public void shouldReturnASetWithThreeChildrenWhenTripleInheritance() {
    parseClassPath(Foo.class, ExtendsFoo.class, ExtendsFoo2ndTime.class,
        ExtendsFoo3rdTime.class);
    Map<ElementName, Set<ElementName>> m = new LinkedHashMap<ElementName, Set<ElementName>>();
    m.put(element(ExtendsFoo3rdTime.class),
        Collections.set(element(ExtendsFoo2ndTime.class)));
    m.put(element(ExtendsFoo2ndTime.class),
        Collections.set(element(ExtendsFoo.class)));
    m.put(element(ExtendsFoo.class), Collections.set(element(Foo.class)));
    testee = new DefaultHierarchyOracle(m);
    assertThat(testee.findChildren(element(Foo.class))).contains(
        element(ExtendsFoo2ndTime.class), element(ExtendsFoo.class),
        element(ExtendsFoo3rdTime.class));
  }

  @Test
  public void shouldReturnDifferentSetOfChildrenForDifferentParents() {
    parseClassPath(Foo.class, ExtendsFoo.class, ExtendsFoo2ndTime.class,
        Moo.class, ExtendsMoo.class, ExtendsMoo2ndTime.class);
    Map<ElementName, Set<ElementName>> m = new LinkedHashMap<ElementName, Set<ElementName>>();
    m.put(element(ExtendsFoo2ndTime.class),
        Collections.set(element(ExtendsFoo.class)));
    m.put(element(ExtendsFoo.class), Collections.set(element(Foo.class)));
    m.put(element(ExtendsMoo2ndTime.class),
        Collections.set(element(ExtendsMoo.class)));
    m.put(element(ExtendsMoo.class), Collections.set(element(Moo.class)));
    testee = new DefaultHierarchyOracle(m);
    assertThat(testee.findChildren(element(Foo.class))).containsOnly(
        element(ExtendsFoo.class), element(ExtendsFoo2ndTime.class));
    assertThat(testee.findChildren(element(Moo.class))).containsOnly(
        element(ExtendsMoo.class), element(ExtendsMoo2ndTime.class));
  }

  private ElementName element(final Class<?> c) {
    return ElementName.fromClass(c);
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
      this.cpp.parse(createRootFor(classes), this.idmbv);
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

}
