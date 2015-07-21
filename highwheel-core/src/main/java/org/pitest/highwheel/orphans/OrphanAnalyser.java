package org.pitest.highwheel.orphans;

import java.util.ArrayList;
import java.util.List;

import edu.uci.ics.jung.graph.DirectedGraph;

public class OrphanAnalyser<V, E> {

  public List<V> findsDeadMethods(DirectedGraph<V, E> methodCalls,
      List<V> entryPoints) {
    List<V> orphans = new ArrayList<V>();
    List<V> nonEntryPoints = seperateEntryPointsFromNonEntryPoints(entryPoints,
        methodCalls);

    if (areThereAnyEntryPoints(entryPoints)) {
      // if (areAllMethodsEntryPoints(methodCalls, entryPoints)) {
      // return orphans;
      // }
      return analyseChain(methodCalls, entryPoints, orphans, nonEntryPoints);
    }
    orphans.addAll(methodCalls.getVertices());
    return orphans;
  }

  private List<V> analyseChain(DirectedGraph<V, E> methodCalls,
      List<V> entryPoints, List<V> orphans, List<V> nonEntryPoints) {
    for (V nonEntryPoint : nonEntryPoints) {
      if (methodCalls.getNeighbors(nonEntryPoint).isEmpty()) {
        orphans.add(nonEntryPoint);
      } else {
        if (methodCalls.getPredecessors(nonEntryPoint).isEmpty()) {
          orphans.add(nonEntryPoint);
        } else {
          for (V predecessor : methodCalls.getPredecessors(nonEntryPoint)) {
            if (entryPoints.contains(predecessor)) {
              return orphans;
            }
          }
        }
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

  boolean areAllMethodsEntryPoints(DirectedGraph<V, E> methodCalls,
      List<V> entryPoints) {
    return (methodCalls.getVertexCount() == entryPoints.size());
  }

  boolean areThereAnyEntryPoints(List<V> entryPoints) {
    return (!entryPoints.isEmpty());
  }

  boolean areThereAnyEdges(DirectedGraph<V, E> methodCalls) {
    return (methodCalls.getEdgeCount() > 0);
  }

}
