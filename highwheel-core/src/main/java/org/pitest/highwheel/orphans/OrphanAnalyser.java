package org.pitest.highwheel.orphans;

import java.util.ArrayList;
import java.util.List;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedGraph;

public class OrphanAnalyser<V, E> {

  public List<V> findOrphans(DirectedGraph<V, E> methodCalls,
      List<V> entryPoints) {
    List<V> orphans = new ArrayList<V>();
    List<V> nonEntryPoints = seperateEntryPointsFromNonEntryPoints(entryPoints,
        methodCalls);
    DijkstraShortestPath<V, E> dsp = new DijkstraShortestPath<V, E>(
        methodCalls);
    if (entryPoints.isEmpty()) {
      orphans.addAll(methodCalls.getVertices());
      return orphans;
    }
    return analyseChain(methodCalls, entryPoints, orphans, nonEntryPoints, dsp);
  }

  private List<V> analyseChain(DirectedGraph<V, E> methodCalls,
      List<V> entryPoints, List<V> orphans, List<V> nonEntryPoints,
      DijkstraShortestPath<V, E> dsp) {
    for (V nonEntryPoint : nonEntryPoints) {
      for (V entryPoint : entryPoints) {
        if (!isConnected(orphans, dsp, nonEntryPoint, entryPoint)) {
          orphans.add(nonEntryPoint);
        }
      }
    }
    return orphans;
  }

  boolean isConnected(List<V> orphans, DijkstraShortestPath<V, E> dsp,
      V nonEntryPoint, V entryPoint) {

    if (dsp.getDistance(entryPoint, nonEntryPoint) != null) {
      return true;
    }
    return false;
  }

  List<V> seperateEntryPointsFromNonEntryPoints(List<V> entryPoints,
      DirectedGraph<V, E> methodCalls) {
    List<V> nonEntryPoints = new ArrayList<V>();
    nonEntryPoints.addAll(methodCalls.getVertices());
    nonEntryPoints.removeAll(entryPoints);
    return nonEntryPoints;
  }

  // // Is b reachable from a
  // boolean isReachable(String a, String b) {
  // // Base case
  // if (a.equalsIgnoreCase(b))
  // return true;
  //
  // // Mark all vertices as not visited
  // LinkedList<String> visited = new LinkedList();
  // visited.add(a);
  //
  // }
  //
  // void breadthFirst(DirectedGraph<V, E> methodCalls,
  // LinkedList<String> visited) {
  // LinkedList<String> nodes =
  //
  // }

}
