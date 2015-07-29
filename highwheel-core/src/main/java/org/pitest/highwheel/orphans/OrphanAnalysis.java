package org.pitest.highwheel.orphans;

import java.io.IOException;
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
    OrphanAnalyser<AccessPoint, Integer> oa = new OrphanAnalyser<AccessPoint, Integer>();
    return oa.findOrphans(mdgbv.getGraph(), mdgbv.getEntryPoints());
    // Collection<AccessPoint> c = Collections.EMPTY_LIST;
    // return c;
  }

}
