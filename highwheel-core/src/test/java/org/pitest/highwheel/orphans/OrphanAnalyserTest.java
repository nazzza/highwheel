package org.pitest.highwheel.orphans;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
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
    graph = fillGraph();
    entries = fillEntries();
    assertThat(testee.findOrphans(graph, entries)).isEmpty();
  }

  @Test
  public void shouldReturnNoOrphansWhenAllAreEntryPoints() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = fillGraph("foo->bar");
    entries = fillEntries("foo", "bar");
    assertThat(testee.findOrphans(graph, entries)).isEmpty();
  }

  @Test
  public void shouldReturnAllOrphansWhen2OrphanGroups() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = fillGraph("foo->bar", "loo->poo");
    entries = fillEntries();
    assertThat(testee.findOrphans(graph, entries)).contains("foo", "bar", "loo",
        "poo");
  }

  @Test
  public void shouldReturnAllOrphansWhenNoEdgesNoEntryPoints() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = fillGraph("foo", "bar");
    entries = fillEntries();
    assertThat(testee.findOrphans(graph, entries)).contains("foo", "bar");
  }

  @Test
  public void shouldReturnAllOrphansWhenNoEntryPoints() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = fillGraph("foo->bar");
    entries = fillEntries();
    assertThat(testee.findOrphans(graph, entries))
        .containsAll(graph.getVertices());
  }

  @Test
  public void shouldReturnNoOrphansWhenAllAreConnectedToAnEntryPoint() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = fillGraph("foo->bar", "foo->kro", "foo->car", "foo->moo");
    entries = fillEntries("foo");
    assertThat(testee.findOrphans(graph, entries)).isEmpty();
  }

  @Test
  public void shouldReturnnAnOrphanWhenEntryPointIsDestNotSource() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = fillGraph("bar->foo");
    entries = fillEntries("foo");
    assertThat(testee.findOrphans(graph, entries)).containsOnly("bar");
  }

  @Test
  public void shouldReturnNoOrphansWhenAllEntryPoints() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = fillGraph("bar->foo");
    // System.out.println("shouldReturnNoOrphansWhenAllEntryPoints" + graph);
    entries = fillEntries("foo", "bar");
    assertThat(testee.findOrphans(graph, entries)).isEmpty();
  }

  @Test
  public void shouldReturnnNoOrphansWhenAll2ConnectedToAnEntryPoint() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = fillGraph("foo->bar->moo");
    entries = fillEntries("foo");
    assertThat(testee.findOrphans(graph, entries)).isEmpty();
  }

  @Test
  public void shouldReturnnNoOrphansWhenAll3ConnectedToAnEntryPoint() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = fillGraph("foo->bar->moo", "bar->koo");
    entries = fillEntries("foo");
    assertThat(testee.findOrphans(graph, entries)).isEmpty();
  }

  @Test
  public void shouldReturnnNoOrphansWhenAll3ConnectedToAnEntryPointInACircle() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = fillGraph("foo->bar", "foo->moo", "bar->moo");
    entries = fillEntries("foo");
    assertThat(testee.findOrphans(graph, entries)).isEmpty();
  }

  @Test
  public void shouldReturnNoOrphansWhen2EntryPointsConnectedTo1NonEntryPoint() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = fillGraph("foo->moo", "bar->moo");
    entries = fillEntries("foo", "bar");
    assertThat(testee.findOrphans(graph, entries)).isEmpty();
  }

  @Test
  public void shouldReturnOrphansWhenOrphanGroupPresent() {
    testee = new OrphanAnalyser<String, Integer>();
    graph = fillGraph("foo->bar->moo", "bar->koo", "boo->loo->car", "loo->mar");
    entries = fillEntries("foo");
    assertThat(testee.findOrphans(graph, entries)).containsOnly("boo", "loo",
        "car", "mar");
  }

  DirectedGraph<String, Integer> fillGraph(String... s) {
    graph = new DirectedSparseGraph<String, Integer>();

    for (String line : s) {

      if (line.contains("->")) {
        List<String> nodes = Arrays.asList(line.split("->"));

        String firstNode = nodes.get(0);

        int edge = graph.getEdgeCount();

        graph.addEdge(edge, firstNode, nodes.get(1));

        for (int i = 1; i < nodes.size() - 1;) {
          graph.addEdge(++edge, nodes.get(i), nodes.get(++i));
        }
      } else {
        graph.addVertex(line);
      }
    }
    return graph;
  }

  List<String> fillEntries(String... e) {
    entries = new ArrayList<String>();
    List<String> list = new ArrayList<String>(Arrays.asList(e));
    entries.addAll(list);
    return entries;
  }

}
