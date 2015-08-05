package org.pitest.highwheel.cycles;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.pitest.highwheel.model.AccessType.IMPLEMENTS;
import static org.pitest.highwheel.model.AccessType.INHERITANCE;
import static org.pitest.highwheel.model.AccessType.USES;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.pitest.highwheel.model.AccessPoint;
import org.pitest.highwheel.model.AccessPointName;
import org.pitest.highwheel.model.ElementName;

public class InheritanceDependencyMapBuildingVisitorTest {

  private InheritanceDependencyMapBuildingVisitor testee;

  @Before
  public void setUp() {
    testee = new InheritanceDependencyMapBuildingVisitor();
  }

  @Test
  public void shouldReturnAnEmptyMapWhenApplyNotCalled() {
    assertThat(testee.getMap()).isEmpty();
  }

  @Test
  public void shouldReturnAnEmptyMapWhenGivenClassesOfTypeNotINHERITANCEnorIMPLEMENTS() {
    testee.apply(access("Foo", accessPoint("a")),
        access("Bar", accessPoint("b")), USES);
    assertThat(testee.getMap()).isEmpty();
  }

  @Test
  public void shouldReturnAMapWithSingleChildSingleParentWhenSingleImplements() {
    testee.apply(access("Foo", accessPoint("a")),
        access("Bar", accessPoint("b")), IMPLEMENTS);
    final Map<ElementName, Set<ElementName>> expected = new LinkedHashMap<ElementName, Set<ElementName>>();
    final Set<ElementName> p = new LinkedHashSet<ElementName>();
    p.add(new ElementName("Bar"));
    expected.put(new ElementName("Foo"), p);
    assertThat(testee.getMap()).isEqualTo(expected);
  }

  @Test
  public void shouldReturnAMapWithSingleChildSingleParentWhenSingleInheritance() {
    testee.apply(access("Foo", accessPoint("a")),
        access("Bar", accessPoint("b")), INHERITANCE);
    final Map<ElementName, Set<ElementName>> expected = new LinkedHashMap<ElementName, Set<ElementName>>();
    final Set<ElementName> p = new LinkedHashSet<ElementName>();
    p.add(new ElementName("Bar"));
    expected.put(new ElementName("Foo"), p);
    assertThat(testee.getMap()).isEqualTo(expected);
  }

  private AccessPoint access(final String element,
      final AccessPointName point) {
    return access(element(element), point);
  }

  private AccessPoint access(final ElementName element,
      final AccessPointName point) {
    return AccessPoint.create(element, point);
  }

  private AccessPointName accessPoint(final String name) {
    return AccessPointName.create(name, "");
  }

  private ElementName element(final String type) {
    return ElementName.fromString(type);
  }

}
