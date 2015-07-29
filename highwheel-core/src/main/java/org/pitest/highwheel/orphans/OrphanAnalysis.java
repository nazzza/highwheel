package org.pitest.highwheel.orphans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.pitest.highwheel.classpath.ClassParser;
import org.pitest.highwheel.classpath.ClasspathRoot;
import org.pitest.highwheel.cycles.MethodDependencyGraphBuildingVisitor;
import org.pitest.highwheel.model.AccessPoint;

import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class OrphanAnalysis {

  private final ClassParser parser;

  public OrphanAnalysis(ClassParser parser) {
    this.parser = parser;
  }

  public Collection<AccessPoint> findOrphans(ClasspathRoot root)
      throws IOException {
    MethodDependencyGraphBuildingVisitor mdgbv = new MethodDependencyGraphBuildingVisitor(
        new DirectedSparseGraph<AccessPoint, Integer>());
    this.parser.parse(root, mdgbv);
    return orphansWithoutInitConstructors(mdgbv);

  }

  private Collection<AccessPoint> orphansWithoutInitConstructors(
      MethodDependencyGraphBuildingVisitor mdgbv) {
    OrphanAnalyser<AccessPoint, Integer> oa = new OrphanAnalyser<AccessPoint, Integer>();
    Collection<AccessPoint> orphans = oa.findOrphans(mdgbv.getGraph(),
        mdgbv.getEntryPoints());
    Collection<AccessPoint> cleanOrphans = new ArrayList<AccessPoint>();
    cleanOrphans.addAll(orphans);
    for (AccessPoint o : orphans) {
      if (o.toString().contains("init"))
        cleanOrphans.remove(o);
    }
    return cleanOrphans;
  }

}
