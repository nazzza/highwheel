package org.pitest.highwheel.orphans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedGraph;

public class OrphanAnalyser<V, E> {

  public Collection<V> findOrphans(final DirectedGraph<V, E> methodCalls,
      final List<V> entryPoints) {
    if (entryPoints.isEmpty()) {
      return methodCalls.getVertices();
    }
    return analyseChain(methodCalls, entryPoints);
  }

  private Collection<V> analyseChain(final DirectedGraph<V, E> methodCalls,
      final List<V> entryPoints) {
    Set<V> orphans = new LinkedHashSet<V>();

    DijkstraShortestPath<V, E> dsp = new DijkstraShortestPath<V, E>(
        methodCalls);
    for (V nonEntryPoint : nonEntryPoints(entryPoints, methodCalls)) {
      for (V entryPoint : entryPoints) {
        if (!isConnected(dsp, nonEntryPoint, entryPoint)) {
          orphans.add(nonEntryPoint);
        }
      }
    }
    return orphans;
  }

  boolean isConnected(final DijkstraShortestPath<V, E> dsp,
      final V nonEntryPoint, final V entryPoint) {

    return (dsp.getDistance(entryPoint, nonEntryPoint) != null);
  }

  List<V> nonEntryPoints(final List<V> entryPoints,
      final DirectedGraph<V, E> methodCalls) {
    List<V> nonEntryPoints = new ArrayList<V>();
    nonEntryPoints.addAll(methodCalls.getVertices());
    nonEntryPoints.removeAll(entryPoints);
    return nonEntryPoints;
  }

}
