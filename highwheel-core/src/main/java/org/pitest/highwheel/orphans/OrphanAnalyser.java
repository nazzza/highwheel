package org.pitest.highwheel.orphans;

import java.util.ArrayList;
import java.util.List;

import edu.uci.ics.jung.graph.DirectedGraph;

public class OrphanAnalyser<V, E> {

  public List<V> findOrphans(DirectedGraph<V, E> methodCalls,
      List<V> entryPoints) {
    List<V> orphans = new ArrayList<V>();
    List<V> nonEntryPoints = seperateEntryPointsFromNonEntryPoints(entryPoints,
        methodCalls);

    return analyseChain(methodCalls, entryPoints, orphans, nonEntryPoints);
  }

  private List<V> analyseChain(DirectedGraph<V, E> methodCalls,
      List<V> entryPoints, List<V> orphans, List<V> nonEntryPoints) {
    for (V nonEntryPoint : nonEntryPoints) {
      if (methodCalls.getPredecessors(nonEntryPoint).isEmpty()) {
        orphans.add(nonEntryPoint);
      } else {
        // if (entryPoints.contains(methodCalls.getPredecessors(nonEntryPoint)))
        // ;
        return orphans;
      }
    }
    return orphans;
  }

  List<V> seperateEntryPointsFromNonEntryPoints(List<V> entryPoints,
      DirectedGraph<V, E> methodCalls) {
    List<V> nonEntryPoints = new ArrayList<V>();
    nonEntryPoints.addAll(methodCalls.getVertices());
    nonEntryPoints.removeAll(entryPoints);
    return nonEntryPoints;
  }

}
