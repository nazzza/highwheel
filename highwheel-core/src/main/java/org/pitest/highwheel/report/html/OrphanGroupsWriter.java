package org.pitest.highwheel.report.html;

import org.pitest.highwheel.cycles.CodeStats;
import org.pitest.highwheel.model.AccessPoint;
import org.pitest.highwheel.model.Dependency;
import org.pitest.highwheel.model.ElementName;
import org.pitest.highwheel.report.StreamFactory;

import edu.uci.ics.jung.graph.DirectedGraph;

class OrphanGroupsWriter extends BaseWriter {

  public final static String FILENAME = "orphan_groups.html";

  OrphanGroupsWriter(final StreamFactory streams) {
    super(streams);
  }

  @Override
  public void start(final CodeStats stats) {
    writeHeader(FILENAME);

    write(FILENAME, "<section class ='deps'>");
    write(FILENAME,
        "<table id=\"sorttable\" class=\"tablesorter\"><thead><tr><th>class</th><th>method</th></tr></thead>");
    write(FILENAME, "<tbody>");
    for (final AccessPoint each : stats.getMethods()) {
      write(FILENAME, "<tr><td>" + each.getElementName() + "</td><td>"
          + each.getAttribute() + "</td></tr>");
    }
    write(FILENAME, "</tbody>");
    write(FILENAME, "</table>");
    write(FILENAME, "</section");
    writeFooter(FILENAME);
  }

  @Override
  public void visitClassSubCycle(
      final DirectedGraph<ElementName, Dependency> each) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visitSubCycle(
      final DirectedGraph<ElementName, Dependency> cycle) {
    // TODO Auto-generated method stub

  }

  @Override
  public void endPackageStronglyConnectedComponent(
      final DirectedGraph<ElementName, Dependency> scc) {
    // TODO Auto-generated method stub

  }

  @Override
  public void endClassStronglyConnectedComponent(
      final DirectedGraph<ElementName, Dependency> scc) {
    // TODO Auto-generated method stub

  }

  @Override
  public void end() {
    // TODO Auto-generated method stub

  }

  @Override
  protected void visitClassScc(
      final DirectedGraph<ElementName, Dependency> scc) {
    // TODO Auto-generated method stub

  }

  @Override
  protected void visitPackageScc(
      final DirectedGraph<ElementName, Dependency> scc) {
    // TODO Auto-generated method stub

  }

  @Override
  public void endClassCycles() {
    // TODO Auto-generated method stub

  }

}
