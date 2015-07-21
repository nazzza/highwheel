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

  @Test
  public void shouldReturnNoOrphansWhenGraphEmpty() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = Collections.emptyList();
    assertThat(testee.findOrphans(graph, entries)).isEmpty();
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
    assertThat(testee.findOrphans(graph, entries)).isEmpty();
  }

  @Test
  public void shouldReturnAllOrphansWhenNoEntryPoints() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = Collections.emptyList();
    graph.addVertex("foo");
    graph.addVertex("boo");
    assertThat(testee.findOrphans(graph, entries))
        .containsAll(graph.getVertices());
  }

  @Test
  public void shouldReturnNoOrphansWhenAllAreConnectedToAnEntryPoint() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = new ArrayList<String>();
    entries.add("foo");
    // foo - > bar
    // foo -> kro
    // foo -> car
    // foo -> moo
    graph.addEdge(1, "foo", "bar");
    graph.addEdge(2, "foo", "kro");
    graph.addEdge(3, "foo", "car");
    graph.addEdge(4, "foo", "moo");
    assertThat(testee.findOrphans(graph, entries)).isEmpty();
  }

  @Test
  public void shouldReturnnAnOrphanWhenEntryPointIsDestNotSource() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = new ArrayList<String>();
    entries.add("foo");
    graph.addEdge(1, "bar", "foo");
    assertThat(testee.findOrphans(graph, entries)).containsOnly("bar");
  }

  @Test
  public void shouldReturnNoOrphansWhenAllEntryPoints() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = new ArrayList<String>();
    entries.add("foo");
    entries.add("bar");
    graph.addEdge(1, "bar", "foo");
    assertThat(testee.findOrphans(graph, entries)).isEmpty();
  }

  @Test
  public void shouldReturnnNoOrphansWhenAll2ConnectedToAnEntryPoint() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = new ArrayList<String>();
    entries.add("foo");
    graph.addEdge(1, "foo", "bar");
    graph.addEdge(2, "bar", "moo");
    assertThat(testee.findOrphans(graph, entries)).isEmpty();
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
    assertThat(testee.findOrphans(graph, entries)).isEmpty();
  }

  @Test
  public void shouldReturnnNoOrphansWhenAll3ConnectedToAnEntryPointInACircle() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = new ArrayList<String>();
    entries.add("foo");
    // bar -> foo
    // foo -> moo
    // bar -> moo
    graph.addEdge(1, "foo", "bar");
    graph.addEdge(2, "foo", "moo");
    graph.addEdge(3, "bar", "moo");
    assertThat(testee.findOrphans(graph, entries)).isEmpty();
  }

  @Test
  public void shouldReturnNoOrphansWhen2EntryPointsConnectedTo1NonEntryPoint() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = new DirectedSparseGraph<String, Integer>();
    entries = new ArrayList<String>();
    entries.add("foo");
    entries.add("bar");
    graph.addEdge(1, "foo", "moo");
    graph.addEdge(2, "bar", "moo");
    assertThat(testee.findOrphans(graph, entries)).isEmpty();
  }

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
    // System.out.println(testee.findOrphans(graph, entries));
    assertThat(testee.findOrphans(graph, entries)).containsOnly("boo", "loo",
        "car", "mar");
  }

}
