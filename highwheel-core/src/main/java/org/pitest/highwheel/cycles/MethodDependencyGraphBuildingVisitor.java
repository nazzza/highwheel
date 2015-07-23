package org.pitest.highwheel.cycles;

import org.pitest.highwheel.classpath.AccessVisitor;
import org.pitest.highwheel.model.AccessPoint;
import org.pitest.highwheel.model.AccessType;
import org.pitest.highwheel.model.Dependency;
import org.pitest.highwheel.model.ElementName;

import edu.uci.ics.jung.graph.DirectedGraph;

public class MethodDependencyGraphBuildingVisitor implements AccessVisitor {

  private final DirectedGraph<ElementName, Dependency> g;


  public MethodDependencyGraphBuildingVisitor(
      final DirectedGraph<ElementName, Dependency> g) {
    this.g = g;
  }

  public void apply(final AccessPoint source, final AccessPoint dest,
      final AccessType type) {
    final ElementName sourceMethod = source.getElementName();
    final ElementName destMethod = dest.getElementName();

    if (!sourceMethod.equals(destMethod)) {
      Dependency edge = this.g.findEdge(sourceMethod, destMethod);
      if (edge == null) {
        edge = new Dependency();
        this.g.addEdge(edge, sourceMethod, destMethod);
      }
      edge.addDependency(source, dest, type);
    }

    // update edge here
  }

  public DirectedGraph<ElementName, Dependency> getGraph() {
    return this.g;
  }

  public void newNode(final ElementName clazz) {
    this.g.addVertex(clazz);
  }

  public void newEntryPoint(final ElementName clazz) {

  }

}