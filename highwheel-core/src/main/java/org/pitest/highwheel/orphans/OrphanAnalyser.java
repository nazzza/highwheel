package org.pitest.highwheel.orphans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedGraph;

public class OrphanAnalyser<V, E> {

  public Collection<V> findOrphans(DirectedGraph<V, E> methodCalls,
      List<V> entryPoints) {
    if (entryPoints.isEmpty()) {
      return methodCalls.getVertices();
    }
    return analyseChain(methodCalls, entryPoints);
  }

  private List<V> analyseChain(DirectedGraph<V, E> methodCalls,
      List<V> entryPoints) {
    List<V> orphans = new ArrayList<V>();

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

  boolean isConnected(DijkstraShortestPath<V, E> dsp, V nonEntryPoint,
      V entryPoint) {

    return (dsp.getDistance(entryPoint, nonEntryPoint) != null);
  }

  List<V> nonEntryPoints(List<V> entryPoints, DirectedGraph<V, E> methodCalls) {
    List<V> nonEntryPoints = new ArrayList<V>();
    nonEntryPoints.addAll(methodCalls.getVertices());
    nonEntryPoints.removeAll(entryPoints);
    return nonEntryPoints;
  }

}
