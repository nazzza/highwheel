package org.pitest.highwheel.orphans;

import java.util.ArrayList;
import java.util.List;

import edu.uci.ics.jung.graph.DirectedGraph;

public class OrphanAnalyser<V, E> {

  public List<V> findsDeadMethods(DirectedGraph<V, E> methodCalls,
      List<V> entryPoints) {
    List<V> orphans = new ArrayList<V>();
    List<V> nonEntryPoints = new ArrayList<V>();
    nonEntryPoints.addAll(methodCalls.getVertices());

    if (!isEmptyGraph(methodCalls)) {
      if (areThereAnyEntryPoints(entryPoints)) {
        if (areAllMethodsEntryPoints(methodCalls, entryPoints)) {
          return orphans;
        } else {
          seperateEntryPointsFromNonEntryPoints(entryPoints, nonEntryPoints);
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

  public void seperateEntryPointsFromNonEntryPoints(List<V> entryPoints,
      List<V> nonEntryPoints) {
    for (V entryPoint : entryPoints) {
      nonEntryPoints.remove(entryPoint);
    }
  }

  public boolean isEmptyGraph(DirectedGraph<V, E> methodCalls) {
    if (methodCalls.getVertexCount() == 0) {
      return true;
    }
    return false;
  }

  public boolean areAllMethodsEntryPoints(DirectedGraph<V, E> methodCalls,
      List<V> entryPoints) {
    if (methodCalls.getVertexCount() == entryPoints.size()) {
      return true;
    }
    return false;
  }

  public boolean areThereAnyEntryPoints(List<V> entryPoints) {
    if (!entryPoints.isEmpty()) {
      return true;
    }
    return false;
  }

  public boolean areThereAnyEdges(DirectedGraph<V, E> methodCalls) {
    if (methodCalls.getEdgeCount() > 0) {
      return true;
    }
    return false;
  }

  // Are there any elements which are inside entryPoints but not inside a Graph?
  public boolean areEntryPointsInsideAGraph(DirectedGraph<V, E> methodCalls,
      List<V> entryPoints) {
    for (V entryPoint : entryPoints) {
      if (!methodCalls.containsVertex(entryPoint)) {
        return false;
      }
    }
    return true;
  }

  // private boolean hasEntryPointEdges(DirectedGraph<V, E> methodCalls,
  // String entryPoint) {
  // if (methodCalls.degree(entryPoint) > 0) {
  // return true;
  // }
  // return false;
  //
  // }

  // if (!isEmptyGraph(methodCalls) && areThereAnyEntryPoints(entryPoints)
  // && areThereAnyEdges(methodCalls)
  // && areEntryPointsInsideAGraph(methodCalls, entryPoints)
  // && !areAllMethodsEntryPoints(methodCalls, entryPoints)) {
  // // check
  // }
  // return orphans;
  // }

  // && !areAllMethodsEntryPoints(methodCalls, entryPoints)) {
  // for (V entryPoint : entryPoints) {
  // methodCalls.
  // methodCalls.getIncidentVertices(edge)
  // }
  // orphans = new ArrayList<String>(
  // ((Collection<? extends String>) methodCalls.getVertices()));
  // }
  //
  // }

}
