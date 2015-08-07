package org.pitest.highwheel.orphans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.pitest.highwheel.classpath.ClassParser;
import org.pitest.highwheel.classpath.ClasspathRoot;
import org.pitest.highwheel.cycles.DefaultHierarchyOracle;
import org.pitest.highwheel.cycles.InheritanceDependencyMapBuildingVisitor;
import org.pitest.highwheel.cycles.MethodDependencyGraphBuildingVisitor;
import org.pitest.highwheel.model.AccessPoint;
import org.pitest.highwheel.model.ElementName;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class OrphanAnalysis {

  private final ClassParser parser;
  // private DirectedGraph<AccessPoint, Integer> graph;
  // graph with all the methods including inits
  // private MethodDependencyGraphBuildingVisitor mdgbv;

  public OrphanAnalysis(final ClassParser parser) {
    this.parser = parser;
  }

  public DirectedGraph<AccessPoint, Integer> getGraph() {
    return new DirectedSparseGraph<AccessPoint, Integer>();
    // return graph;
  }

  public Collection<AccessPoint> findOrphans(final ClasspathRoot root)
      throws IOException {

    // Parsing
    MethodDependencyGraphBuildingVisitor mdgbv = new MethodDependencyGraphBuildingVisitor(
        new DirectedSparseGraph<AccessPoint, Integer>());

    InheritanceDependencyMapBuildingVisitor idmbv = new InheritanceDependencyMapBuildingVisitor();

    this.parser.parse(root, mdgbv);
    this.parser.parse(root, idmbv);
    DirectedGraph<AccessPoint, Integer> graph = mdgbv.getGraph();
    modifyGraphToShowInheritance(graph, idmbv.getMap());

    // Orphan Analyser
    OrphanAnalyser<AccessPoint, Integer> oa = new OrphanAnalyser<AccessPoint, Integer>();
    Collection<AccessPoint> orphans = oa.findOrphans(graph,
        mdgbv.getEntryPoints());

    return orphansWithoutInitConstructors(orphans);

  }

  private Collection<AccessPoint> orphansWithoutInitConstructors(
      final Collection<AccessPoint> orphans) {
    Collection<AccessPoint> cleanOrphans = new ArrayList<AccessPoint>();
    for (AccessPoint o : orphans) {
      if (!"(init)".equals(o.getAttribute().toString())) {
        cleanOrphans.add(o);
      }

    }
    return cleanOrphans;
  }

  private void modifyGraphToShowInheritance(
      final DirectedGraph<AccessPoint, Integer> methodGraph,
      final Map<ElementName, Set<ElementName>> map) {

    DefaultHierarchyOracle dho = new DefaultHierarchyOracle(map);

    for (AccessPoint element1 : methodGraph.getVertices()) {
      if (hasParents(element1.getElementName(), dho)) {
        for (ElementName parent : dho.findParents(element1.getElementName())) {
          // connect unconnected method to methods of parents found after
          // calling findParents
          for (AccessPoint element2 : methodGraph.getVertices()) {

            if (parent.equals(element2.getElementName())) {

              if (element1.getAttribute().equals(element2.getAttribute())) {

                methodGraph.addEdge(methodGraph.getEdgeCount() + 1, element2,

                element1);
              }
            }
          }
        }
      }
    }

  }

  private boolean hasParents(final ElementName element,
      final DefaultHierarchyOracle dho) {
    return !dho.findParents(element).isEmpty();
  }

  // private Collection<AccessPoint> orphansAfterInheritanceAnalysis(
  // final Collection<AccessPoint> orphans,
  // final Map<ElementName, Set<ElementName>> map,
  // final MethodDependencyGraphBuildingVisitor mdgbv) {
  //
  // if (noEntryPoints(orphans, mdgbv)) {
  // return orphans;
  // }
  // return inheritanceAnalysis(orphans, map, mdgbv);
  // }
  //
  // private Collection<AccessPoint> inheritanceAnalysis(
  // final Collection<AccessPoint> orphans,
  // final Map<ElementName, Set<ElementName>> map,
  // final MethodDependencyGraphBuildingVisitor mdgbv) {
  //
  // DefaultHierarchyOracle dho = new DefaultHierarchyOracle(map);
  // Collection<AccessPoint> cleanOrphans = new LinkedHashSet<AccessPoint>();
  //
  // for (AccessPoint o : orphans) {
  // if (hasParents(o.getElementName(), dho)) {
  // for (AccessPoint method : mdgbv.getGraph().getVertices()) {
  // parentsAnalysis(dho, cleanOrphans, o, method);
  // }
  // } else {
  // cleanOrphans.add(o);
  // }
  // }
  // return cleanOrphans;
  // }
  //
  // private void parentsAnalysis(final DefaultHierarchyOracle dho,
  // final Collection<AccessPoint> cleanOrphans, final AccessPoint o,
  // final AccessPoint method) {
  // for (ElementName parent : dho.findParents(o.getElementName())) {
  // if (method.getElementName().equals(parent)) {
  // if (!method.getAttribute().equals(o.getAttribute())) {
  // cleanOrphans.add(o);
  // }
  // }
  // }
  // }

  // private boolean noEntryPoints(final Collection<AccessPoint> orphans,
  // final MethodDependencyGraphBuildingVisitor mdgbv) {
  // return mdgbv.getEntryPoints().isEmpty();
  // }

  // private boolean hasParents(final ElementName element,
  // final DefaultHierarchyOracle dho) {
  // return !dho.findParents(element).isEmpty();
  // }

}
