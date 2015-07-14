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
          for (V entryPoint : entryPoints) {
            nonEntryPoints.remove(entryPoint);
          }
          for (V nonEntryPoint : nonEntryPoints) {
            for (V neighbor : methodCalls.getNeighbors(nonEntryPoint)) {
              if (!entryPoints.contains(neighbor)) {
                orphans.add(nonEntryPoint);
              }
            }
          }
        }
        return orphans;
      }
      orphans.addAll(methodCalls.getVertices());
    }

    // if (!isEmptyGraph(methodCalls)) {
    // // do something with what's inside the graph
    // if (areThereAnyEntryPoints(entryPoints)) {
    // // only if there are entry points, this is not an orphan group so go on
    // if (areEntryPointsInsideAGraph(methodCalls, entryPoints)) {
    // // only if they are valid entry points, taken off the actual graph
    // if (!areAllMethodsEntryPoints(methodCalls, entryPoints)) {
    // // if all methods are entry points, there are no orphans
    //
    // if (areThereAnyEdges(methodCalls)) {
    // // if there are edges, Vs are connected
    // for (V entryPoint : entryPoints) {
    // if (methodCalls.outDegree(entryPoint) == 0) {
    // // entry point has no out edges = is not connected to anything
    // // but still an entry point so won't be an orphan
    // // remove from graph
    // methodCalls.removeVertex(entryPoint);
    // }
    //
    // orphans.addAll(methodCalls.getVertices());
    //
    // }
    // } else {
    // // Vs are not connected so every non-entry-point V is an orphan
    // // got through all entry points and remove them from the graph
    // for (V entryPoint : entryPoints) {
    // methodCalls.removeVertex(entryPoint);
    // }
    // // after removal, the graph contains only orphan vertices
    // orphans.addAll(methodCalls.getVertices());
    // }
    //
    // }
    // }
    // }
    // }

    return orphans;
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
