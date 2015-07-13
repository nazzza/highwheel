package org.pitest.highwheel.orphans;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class OrphanAnalyserTest {

  private OrphanAnalyser<String, Integer> testee;

  private DirectedGraph<String, Integer> graph;

  private List<String> entries;

  // private List<AccessPoint> methods;

  @Test
  public void shouldReturnNoOrphansWhenGraphEmpty() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = Collections.emptyList();
    assertThat(testee.findsDeadMethods(graph, entries)).isEmpty();
  }

  @Test
  public void shouldReturnAnOrphanWhenMethodHasNoDependencies() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = Collections.emptyList(); // no entry points detected
    graph.addVertex("foo");
    assertThat(testee.findsDeadMethods(graph, entries)).containsOnly("foo");
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
    // System.out.println(graph.getVertexCount());
    // System.out.println(entries.size());
    assertThat(testee.findsDeadMethods(graph, entries)).isEmpty();
  }

  @Test
  public void shouldReturnNoOrphansWhenAllAreConnectedToAnEntryPoint() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = new ArrayList<String>();
    entries.add("foo");
    graph.addEdge(1, "foo", "bar");
    // System.out.println(graph.getVertexCount());
    // System.out.println(entries.size());
    // System.out
    // .println("shouldReturnNoOrphansWhenAllAreConnectedToAnEntryPoint");
    // System.out.println("all vertices: " + graph.getVertices());
    // System.out.println("all edges: " + graph.getEdges());
    // System.out.println("so the no of all edges: " + graph.getEdgeCount());
    // System.out.println("entry points: " + entries);
    // System.out.println("all edges: " + graph.getEdges());
    // System.out.println("number of out edges for entry at index 0: "
    // + graph.outDegree(entries.get(0)));
    // System.out
    // .println("number of edges connected to foo: " + graph.degree("foo"));
    // System.out.println("destination of edge 1: " + graph.getDest(1));
    // System.out.println("---------");
    assertThat(testee.findsDeadMethods(graph, entries)).isEmpty();
  }

  // @Test
  public void shouldReturnnAnOrphanWhenEntryPointIsDestNotSource() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = new ArrayList<String>();
    entries.add("foo");
    graph.addEdge(1, "bar", "foo");
    // graph.addEdge(2, "loo", "car");
    // System.out.println(graph.getVertexCount());
    // System.out.println(entries.size())
    System.out.println("shouldReturnnAnOrphanWhenEntryPointIsDestNotSource");
    System.out.println("all vertices: " + graph.getVertices());
    System.out.println("all edges: " + graph.getEdges());
    System.out.println("so the no of all edges: " + graph.getEdgeCount());
    System.out.println("entry points: " + entries);
    System.out.println("all edges: " + graph.getEdges());
    System.out.println("number of out edges for entry at index 0: "
        + graph.outDegree(entries.get(0)));
    System.out
        .println("number of edges connected to foo: " + graph.degree("foo"));
    System.out.println("destination of edge 1: " + graph.getDest(1));
    assertThat(testee.findsDeadMethods(graph, entries)).containsOnly("bar");
  }

}
