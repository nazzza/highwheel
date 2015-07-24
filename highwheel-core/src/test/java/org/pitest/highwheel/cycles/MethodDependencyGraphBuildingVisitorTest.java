package org.pitest.highwheel.cycles;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.pitest.highwheel.model.AccessType.COMPOSED;
import static org.pitest.highwheel.model.AccessType.USES;

import org.junit.Before;
import org.junit.Test;
import org.pitest.highwheel.model.AccessPoint;
import org.pitest.highwheel.model.AccessPointName;
import org.pitest.highwheel.model.ElementName;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class MethodDependencyGraphBuildingVisitorTest {

  MethodDependencyGraphBuildingVisitor testee;
  DirectedGraph<AccessPoint, Integer>  g;

  @Before
  public void setUp() {
    g = new DirectedSparseGraph<AccessPoint, Integer>();
    testee = new MethodDependencyGraphBuildingVisitor(g);
  }

  @Test
  public void shouldReturnEmptyGraph() {
    assertThat(testee.getGraph().getVertices()).isEmpty();
  }

  @Test
  public void shouldCreateEdgesBetweenElementsOfTypeUSES() {
    testee.apply(access("foo", accessPoint("a")),
        access("notFoo", accessPoint("b")), USES);
    assertThat(g.findEdge(access("foo", accessPoint("a")),
        access("notFoo", accessPoint("b")))).isNotNull();
  }

  @Test
  public void shouldNotCreateEdgesBetweenElementsOfTypeNotUSES() {
    testee.apply(access("foo", accessPoint("a")),
        access("notFoo", accessPoint("b")), COMPOSED);
    assertThat(g.findEdge(access("foo", accessPoint("a")),
        access("notFoo", accessPoint("b")))).isNull();
  }

  @Test
  public void shouldReturnGraphWithNodes() {
    testee.apply(access("foo", accessPoint("a")),
        access("notFoo", accessPoint("b")), USES);
    assertThat(testee.getGraph().getVertices()).isNotEmpty();
  }

  private AccessPoint access(String element, final AccessPointName point) {
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
