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

    if (!isEmptyGraph(methodCalls)) {
      if (areThereAnyEntryPoints(entryPoints)) {
        if (areAllMethodsEntryPoints(methodCalls, entryPoints)) {
          return orphans;
        } else {
          for (V nonEntryPoint : nonEntryPoints) {
            if (methodCalls.getNeighbors(nonEntryPoint).isEmpty()) {
              // or methodCalls.getIncidentEdges(nonEntryPoint).isEmpty()
              orphans.add(nonEntryPoint);
            } else {
              if (methodCalls.getPredecessors(nonEntryPoint).isEmpty()) {
                orphans.add(nonEntryPoint);
              } else {
                for (V predecessor : methodCalls
                    .getPredecessors(nonEntryPoint)) {
                  if (entryPoints.contains(predecessor)) {
                    return orphans;
                  }
                }
              }
            }
          }
          return orphans;
        }
      }
      orphans.addAll(methodCalls.getVertices());
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

  boolean isEmptyGraph(DirectedGraph<V, E> methodCalls) {
    return methodCalls.getVertexCount() == 0;
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
