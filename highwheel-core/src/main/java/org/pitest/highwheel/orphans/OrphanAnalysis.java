package org.pitest.highwheel.orphans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.pitest.highwheel.classpath.ClassParser;
import org.pitest.highwheel.classpath.ClasspathRoot;
import org.pitest.highwheel.cycles.DefaultHierarchyOracle;
import org.pitest.highwheel.cycles.InheritanceDependencyMapBuildingVisitor;
import org.pitest.highwheel.cycles.MethodDependencyGraphBuildingVisitor;
import org.pitest.highwheel.model.AccessPoint;
import org.pitest.highwheel.model.AccessPointName;
import org.pitest.highwheel.model.ElementName;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class OrphanAnalysis {

  private final ClassParser parser;

  public OrphanAnalysis(final ClassParser parser) {
    this.parser = parser;
  }

  public Collection<AccessPoint> findOrphans(final ClasspathRoot root)
      throws IOException {

    // Parsing
    InheritanceDependencyMapBuildingVisitor idmbv = new InheritanceDependencyMapBuildingVisitor();
    MethodDependencyGraphBuildingVisitor mdgbv = new MethodDependencyGraphBuildingVisitor(
        new DirectedSparseGraph<AccessPoint, Integer>());
    this.parser.parse(root, mdgbv);
    this.parser.parse(root, idmbv);

    // Orphan Analyser
    OrphanAnalyser<AccessPoint, Integer> oa = new OrphanAnalyser<AccessPoint, Integer>();
    Collection<AccessPoint> orphans = oa.findOrphans(mdgbv.getGraph(),
        mdgbv.getEntryPoints());

    return orphansAfterInheritanceAnalysis(
        orphansWithoutInitConstructors(orphans), idmbv.getMap(),
        mdgbv.getGraph());
    // return orphansWithoutInitConstructors(orphans);

  }

  private Collection<AccessPoint> orphansWithoutInitConstructors(
      final Collection<AccessPoint> orphans) {
    Collection<AccessPoint> cleanOrphans = new ArrayList<AccessPoint>();
    for (AccessPoint o : orphans) {
      if (!o.getAttribute().toString().equals("(init)")) {
        cleanOrphans.add(o);
      }

    }
    return cleanOrphans;
  }

  private Collection<AccessPoint> orphansAfterInheritanceAnalysis(
      final Collection<AccessPoint> orphans,
      final Map<ElementName, Set<ElementName>> map,
      final DirectedGraph<AccessPoint, Integer> graph) {

    DefaultHierarchyOracle dho = new DefaultHierarchyOracle(map);
    return inheritanceAnalysis(orphans, graph, dho);
  }

  private Collection<AccessPoint> inheritanceAnalysis(
      final Collection<AccessPoint> orphans,
      final DirectedGraph<AccessPoint, Integer> graph,
      final DefaultHierarchyOracle dho) {

    Collection<AccessPoint> cleanOrphans = new LinkedHashSet<AccessPoint>();

    for (AccessPoint o : orphans) {

      ElementName orphanHoldingClass = o.getElementName();
      AccessPointName orphanMethod = o.getAttribute();

      if (dho.findParents(orphanHoldingClass).isEmpty()
          && dho.findChildren(orphanHoldingClass).isEmpty()) {
        return orphans;
      }

      for (ElementName parent : dho.findParents(orphanHoldingClass)) {

        for (AccessPoint method : graph.getVertices()) {

          ElementName currentMethodHoldingClass = method.getElementName();
          AccessPointName methodInsideCurrentClass = method.getAttribute();

          // if method belongs to the parent and it has the same signature as
          // the orphan method, this method is not an orphan
          if (currentMethodHoldingClass.equals(parent)) {
            if (!methodInsideCurrentClass.equals(orphanMethod)) {
              cleanOrphans.add(o);
            }
          }

        }
      }

      for (ElementName child : dho.findChildren(orphanHoldingClass)) {

        for (AccessPoint method : graph.getVertices()) {

          ElementName currentMethodHoldingClass = method.getElementName();
          AccessPointName methodInsideCurrentClass = method.getAttribute();

          if (currentMethodHoldingClass.equals(child)) {
            if (!methodInsideCurrentClass.equals(orphanMethod)
                && !methodInsideCurrentClass.toString().equals("(init)")) {
              cleanOrphans.add(o);
            }
          }

        }
      }
    }
    return cleanOrphans;
  }

}
