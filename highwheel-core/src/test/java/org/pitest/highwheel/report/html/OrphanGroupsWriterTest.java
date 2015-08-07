package org.pitest.highwheel.report.html;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pitest.highwheel.cycles.CodeGraphs;
import org.pitest.highwheel.cycles.CodeStats;
import org.pitest.highwheel.model.AccessPoint;
import org.pitest.highwheel.model.Dependency;
import org.pitest.highwheel.model.ElementName;
import org.pitest.highwheel.report.StreamFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import nu.validator.htmlparser.common.XmlViolationPolicy;
import nu.validator.htmlparser.dom.HtmlDocumentBuilder;

public class OrphanGroupsWriterTest {

  private OrphanGroupsWriter testee;

  @Mock
  private StreamFactory         streams;
  private ByteArrayOutputStream os;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    this.os = new ByteArrayOutputStream();
    when(this.streams.getStream(OrphanGroupsWriter.FILENAME))
        .thenReturn(this.os);

    this.testee = new OrphanGroupsWriter(streams);
  }

  @Test
  public void shouldGenerateAValidDocument() throws SAXException, IOException {
    this.testee.start(emptyCodeStats());
    final Document d = parseOutput();
    assertThat(d.getElementsByTagName("html").getLength()).isEqualTo(1);
    assertThat(d.getElementsByTagName("body").getLength()).isEqualTo(1);

  }

  @Test
  public void shouldGenerateATableHeader() throws Exception {
    this.testee.start(emptyCodeStats());
    final Document d = parseOutput();
    final NodeList header = d.getElementsByTagName("thead");
    assertThat(header.getLength()).isEqualTo(1);
    assertThat(header.item(0).getFirstChild().getNodeName()).isEqualTo("tr");
  }

  @Test
  public void shouldGenerateTableBody() throws Exception {
    this.testee.start(emptyCodeStats());
    final Document d = parseOutput();
    final NodeList header = d.getElementsByTagName("tbody");
    assertThat(header.getLength()).isEqualTo(1);
  }

  private CodeStats emptyCodeStats() {
    final CodeGraphs g = new CodeGraphs(
        new DirectedSparseGraph<ElementName, Dependency>(),
        new LinkedHashSet<AccessPoint>(),
        new DirectedSparseGraph<AccessPoint, Integer>());
    return new CodeStats(g);
  }

  private Document parseOutput() throws SAXException, IOException {
    final HtmlDocumentBuilder html = new HtmlDocumentBuilder(
        XmlViolationPolicy.FATAL);
    final ByteArrayInputStream bis = new ByteArrayInputStream(
        this.os.toByteArray());
    return html.parse(bis);
  }

}
