package org.pitest.highwheel.cycles;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.pitest.highwheel.classpath.ClassParser;
import org.pitest.highwheel.classpath.ClasspathRoot;
import org.pitest.highwheel.model.AccessPoint;
import org.pitest.highwheel.model.ElementName;

import edu.uci.ics.jung.graph.DirectedGraph;

public class InheritanceConnector {

  private final ClassParser                          parser;
  private final MethodDependencyGraphBuildingVisitor mdgbv;

  public InheritanceConnector(final ClassParser parser,
      final MethodDependencyGraphBuildingVisitor mdgbv) {
    this.parser = parser;
    this.mdgbv = mdgbv;
  }

  public DirectedGraph<AccessPoint, Integer> connectInheritedNodes(
      final ClasspathRoot root) throws IOException {

    InheritanceDependencyMapBuildingVisitor idmbv = new InheritanceDependencyMapBuildingVisitor();

    this.parser.parse(root, mdgbv);
    this.parser.parse(root, idmbv);

    return inheritanceAnalysis(mdgbv.getGraph(), idmbv.getMap());
  }

  private DirectedGraph<AccessPoint, Integer> inheritanceAnalysis(
      final DirectedGraph<AccessPoint, Integer> messyGraph,
      final Map<ElementName, Set<ElementName>> map) {

    DefaultHierarchyOracle dho = new DefaultHierarchyOracle(map);
    // DirectedGraph<AccessPoint, Integer> connectedGraph = new
    // DirectedSparseGraph<AccessPoint, Integer>();

    for (AccessPoint element1 : messyGraph.getVertices()) {
      if (hasParents(element1.getElementName(), dho)) {
        for (ElementName parent : dho.findParents(element1.getElementName())) {
          // connect unconnected method to methods of parents found after
          // calling findParents
          for (AccessPoint element2 : messyGraph.getVertices()) {

            if (parent.equals(element2.getElementName())) {

              if (element1.getAttribute().equals(element2.getAttribute())) {

                messyGraph.addEdge(messyGraph.getEdgeCount() + 1, element1,

                element2);
              }
            }
          }
        }
      }
    }
    return messyGraph;
  }

  private boolean hasParents(final ElementName element,
      final DefaultHierarchyOracle dho) {
    return !dho.findParents(element).isEmpty();
  }

}
