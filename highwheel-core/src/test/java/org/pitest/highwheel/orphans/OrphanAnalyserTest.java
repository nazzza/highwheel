package org.pitest.highwheel.orphans;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Ignore;
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
    // printAllInfo("shouldReturnNoOrphansWhenGraphEmpty");
    assertThat(testee.findsDeadMethods(graph, entries)).isEmpty();
  }

  @Test
  public void shouldReturnNoOrphansWhenAllAreEntryPoints() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = new ArrayList<String>();
    entries.add("foo");
    entries.add("bar");
    graph.addVertex("foo");
    graph.addVertex("bar");
    // printAllInfo("shouldReturnNoOrphansWhenAllAreEntryPoints");
    assertThat(testee.findsDeadMethods(graph, entries)).isEmpty();
  }

  @Test
  public void shouldReturnAllOrphansWhenNoEntryPoints() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = Collections.emptyList();
    graph.addVertex("foo");
    // printAllInfo("shouldReturnAllOrphansWhenNoEntryPoints");
    assertThat(testee.findsDeadMethods(graph, entries))
        .containsAll(graph.getVertices());
  }

  @Test
  public void shouldReturnNoOrphansWhenAllAreConnectedToAnEntryPoint() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = new ArrayList<String>();
    entries.add("foo");
    graph.addEdge(1, "foo", "bar");
    graph.addEdge(2, "foo", "kro");
    graph.addEdge(3, "foo", "car");
    graph.addEdge(4, "moo", "foo");
    // System.out.println("edges for foo: " + graph.getIncidentEdges("foo"));
    // System.out.println("get neighbors for foo: " +
    // graph.getNeighbors("foo"));
    // System.out
    // .println("getPredecessors for foo: " + graph.getPredecessors("foo"));
    // System.out.println("getSuccessors for foo: " +
    // graph.getSuccessors("foo"));
    // printAllInfo("shouldReturnNoOrphansWhenAllAreConnectedToAnEntryPoint");
    assertThat(testee.findsDeadMethods(graph, entries)).isEmpty();
  }

  @Test
  public void shouldReturnnAnOrphanWhenEntryPointIsDestNotSource() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = new ArrayList<String>();
    entries.add("foo");
    graph.addEdge(1, "bar", "foo");
    // System.out.println("get neighbors: " + graph.getNeighbors("foo"));
    // printAllInfo("shouldReturnnAnOrphanWhenEntryPointIsDestNotSource");
    assertThat(testee.findsDeadMethods(graph, entries)).containsOnly("bar");
  }

  @Test
  public void shouldReturnnNoOrphansWhenAllEntryPointsNoMatterEdge() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = new ArrayList<String>();
    entries.add("foo");
    entries.add("bar");
    graph.addEdge(1, "bar", "foo");
    // System.out.println("get neighbors: " + graph.getNeighbors("foo"));
    // printAllInfo("shouldReturnnAnOrphanWhenEntryPointIsDestNotSource");
    assertThat(testee.findsDeadMethods(graph, entries)).isEmpty();
  }

  @Test
  public void shouldReturnnNoOrphansWhenAll2ConnectedToAnEntryPoint() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = new ArrayList<String>();
    entries.add("foo");
    graph.addEdge(1, "foo", "bar");
    graph.addEdge(2, "bar", "moo");
    // System.out.println("get neighbors: " + graph.getNeighbors("foo"));
    // printAllInfo("shouldReturnnAnOrphanWhenEntryPointIsDestNotSource");
    assertThat(testee.findsDeadMethods(graph, entries)).isEmpty();
  }

  @Test
  public void shouldReturnnNoOrphansWhenAll3ConnectedToAnEntryPoint() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = new ArrayList<String>();
    entries.add("foo");
    graph.addEdge(1, "foo", "bar");
    graph.addEdge(2, "bar", "moo");
    graph.addEdge(3, "bar", "koo");
    // System.out.println("get neighbors: " + graph.getNeighbors("foo"));
    // printAllInfo("shouldReturnnAnOrphanWhenEntryPointIsDestNotSource");
    assertThat(testee.findsDeadMethods(graph, entries)).isEmpty();
  }

  @Test
  public void shouldReturnnNoOrphansWhenAll3ConnectedToAnEntryPointInACircle() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = new ArrayList<String>();
    entries.add("foo");
    graph.addEdge(1, "bar", "foo");
    graph.addEdge(2, "foo", "moo");
    graph.addEdge(3, "bar", "moo");
    // System.out.println("get neighbors: " + graph.getNeighbors("foo"));
    // printAllInfo("shouldReturnnAnOrphanWhenEntryPointIsDestNotSource");
    assertThat(testee.findsDeadMethods(graph, entries)).isEmpty();
  }

  @Ignore
  @Test
  public void shouldReturnOrphansWhen2EntryPointsConnectedTo1NonEntryPoint() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = new ArrayList<String>();
    entries.add("foo");
    entries.add("bar");
    graph.addEdge(1, "foo", "moo");
    graph.addEdge(2, "bar", "moo");
    // System.out.println("get neighbors for moo: " +
    // graph.getNeighbors("moo"));
    // System.out.println("entry points: " + entries);
    printAllInfo(
        "shouldReturnOrphansWhen2EntryPointsConnectedTo1NonEntryPoint");
    assertThat(testee.findsDeadMethods(graph, entries)).isNotEmpty();
  }

  @Ignore
  @Test
  public void shouldReturnOrphansWhenOrphanGroupPresent() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = new ArrayList<String>();
    entries.add("foo");
    graph.addEdge(1, "foo", "bar");
    graph.addEdge(2, "bar", "moo");
    graph.addEdge(3, "bar", "koo");
    //
    graph.addEdge(4, "boo", "loo");
    graph.addEdge(5, "loo", "car");
    graph.addEdge(6, "loo", "mar");
    // System.out.println(testee.findsDeadMethods(graph, entries));
    assertThat(testee.findsDeadMethods(graph, entries)).containsOnly("boo",
        "loo", "car", "mar");
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
        + testee.findsDeadMethods(graph, entries) + "\n---");
  }

  private void printBooleanResults() {
    System.out.println("\nboolean results--->");
    // System.out.println(
    // "areThereAnyEntryPoints " + testee.areThereAnyEntryPoints(entries));
    // System.out.println("areAllMethodsEntryPoints "
    // + testee.areAllMethodsEntryPoints(graph, entries));
    // System.out
    // .println("areThereAnyEdges " + testee.areThereAnyEdges(graph) + "\n");
  }

}
