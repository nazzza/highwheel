package org.pitest.highwheel.orphans;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class OrphanAnalyserTest<V, E> {

  private OrphanAnalyser<String, Integer> testee;

  private DirectedGraph<String, Integer> graph;

  private List<String> entries;

  // private List<AccessPoint> methods;

  @Test
  public void shouldReturnNoOrphansWhenGraphEmpty() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = Collections.emptyList();
    // printAllInfo("1 shouldReturnNoOrphansWhenGraphEmpty");
    assertThat(testee.findsDeadMethods(graph, entries)).isEmpty();
  }

  @Test
  public void shouldReturnAllOrphansWhenNoEntryPoints() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = Collections.emptyList();
    graph.addVertex("foo");
    // printAllInfo("2 shouldReturnAllOrphansWhenNoEntryPoints");
    assertThat(testee.findsDeadMethods(graph, entries))
        .containsAll(graph.getVertices());
  }

  // @Test
  // To be modified: possibilities:
  // 1. Go through the list of entry points, get rid of those that can't be
  // found in the graph
  // 2. Throw error - not sensible, will stop the workflow?
  // 3. Deal with this when analysing data, when collecting entry points
  // This test is temporary, just to make sure we though about it
  // public void shouldRx1eturnAllOrphansWhenEntryPointsNotInsideGraph() {
  // testee = new OrphanAnalyser<String, Integer>();
  // graph = new DirectedSparseGraph<String, Integer>();
  // entries = new ArrayList<String>();
  // entries.add("boo");
  // graph.addVertex("foo");
  // printAllInfo("3 shouldReturnAllOrphansWhenEntryPointsNotInsideGraph");
  // assertThat(testee.findsDeadMethods(graph, entries))
  // .containsAll(graph.getVertices());
  // }

  // @Test
  // public void shouldReturnAnOrphanWhenMethodHasNoDependencies() {
  // testee = new OrphanAnalyser<String, Integer>();
  // graph = new DirectedSparseGraph<String, Integer>();
  // entries = Collections.emptyList(); // no entry points detected
  // graph.addVertex("foo");
  // printAllInfo("3 shouldReturnAnOrphanWhenMethodHasNoDependencies");
  // assertThat(testee.findsDeadMethods(graph, entries)).containsOnly("foo");
  // }

  @Test
  public void shouldReturnNoOrphansWhenAllAreEntryPoints() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = new ArrayList<String>();
    entries.add("foo");
    entries.add("bar");
    graph.addVertex("foo");
    graph.addVertex("bar");
    // printAllInfo("4 shouldReturnNoOrphansWhenAllAreEntryPoints");
    assertThat(testee.findsDeadMethods(graph, entries)).isEmpty();
  }

  @Test
  public void shouldReturnNoOrphansWhenAllAreConnectedToAnEntryPoint() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = new ArrayList<String>();
    entries.add("foo");
    graph.addEdge(1, "foo", "bar");
    System.out.println("get neighbors: " + graph.getNeighbors("foo"));
    printAllInfo("5 shouldReturnNoOrphansWhenAllAreConnectedToAnEntryPoint");
    assertThat(testee.findsDeadMethods(graph, entries)).isEmpty();
  }

  // @Test
  public void shouldReturnnAnOrphanWhenEntryPointIsDestNotSource() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = new ArrayList<String>();
    entries.add("foo");
    graph.addEdge(1, "bar", "foo");
    printAllInfo("6 shouldReturnnAnOrphanWhenEntryPointIsDestNotSource");

    for (String entry : entries) {
      if (graph.outDegree(entry) == 0) {
        System.out.println("Entry point " + entry + " has no edges\n");
      }
    }

    // assertThat(testee.findsDeadMethods(graph, entries)).containsOnly("bar");

    assertThat(testee.findsDeadMethods(graph, entries)).isNotEmpty();

  }

  private void printAllInfo(String testName) {
    System.out.println("\n" + testName);
    printBooleanResults();
    System.out.println("all vertices: " + graph.getVertices());
    System.out.println("all edges: " + graph.getEdges());
    System.out.println("so the no of all edges: " + graph.getEdgeCount());
    System.out.println("entry points: " + entries);
    // if (!testee.isEmptyGraph(graph) &&
    // testee.areThereAnyEntryPoints(entries)) {
    // System.out.println("number of out edges for entry at index 0: "
    // + graph.outDegree(entries.get(0)));
    // System.out
    // .println("number of edges connected to foo: " + graph.degree("foo"));
    // System.out.println("destination of edge 1: " + graph.getDest(1));
    // System.out.println("---------");
    // } else {
    // System.out.println("\n*Graph empty or no entry points detected*");
    // }
    System.out.println("\n" + "method returns: "
        + testee.findsDeadMethods(graph, entries) + "---");
  }

  private void printBooleanResults() {
    System.out.println("\nboolean results--->");
    System.out.println("isEmptyGraph " + testee.isEmptyGraph(graph));
    System.out.println(
        "areThereAnyEntryPoints " + testee.areThereAnyEntryPoints(entries));
    System.out.println("areEntryPointsInsideAGraph "
        + testee.areEntryPointsInsideAGraph(graph, entries));
    System.out.println("areAllMethodsEntryPoints "
        + testee.areAllMethodsEntryPoints(graph, entries));
    System.out
        .println("areThereAnyEdges " + testee.areThereAnyEdges(graph) + "\n");
  }

}
