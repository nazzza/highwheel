package org.pitest.highwheel.cycles;

import static org.pitest.highwheel.model.AccessType.USES;

import org.pitest.highwheel.classpath.AccessVisitor;
import org.pitest.highwheel.model.AccessPoint;
import org.pitest.highwheel.model.AccessType;
import org.pitest.highwheel.model.ElementName;

import edu.uci.ics.jung.graph.DirectedGraph;

public class MethodDependencyGraphBuildingVisitor implements AccessVisitor {

  private final DirectedGraph<AccessPoint, Integer> g;

  public MethodDependencyGraphBuildingVisitor(
      final DirectedGraph<AccessPoint, Integer> g) {
    this.g = g;
  }

  @Override
  public void apply(final AccessPoint source, final AccessPoint dest,
      final AccessType type) {
    if (type.equals(USES)) {
      int edge = g.getEdgeCount() + 1;
      this.g.addEdge(edge, source, dest);
    }
  }

  public DirectedGraph<AccessPoint, Integer> getGraph() {
    return this.g;
  }

  @Override
  public void newNode(final ElementName clazz) {

  }

  @Override
  public void newEntryPoint(final AccessPoint ap) {

  }

  @Override
  public void newAccessPoint(AccessPoint ap) {
    this.g.addVertex(ap);
  }

}