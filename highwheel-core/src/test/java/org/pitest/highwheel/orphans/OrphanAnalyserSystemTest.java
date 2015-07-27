package org.pitest.highwheel.orphans;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pitest.highwheel.cycles.MethodDependencyGraphBuildingVisitor;

public class OrphanAnalyserSystemTest {

  private OrphanAnalyser testee;

  @Mock
  private MethodDependencyGraphBuildingVisitor mdgbv;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

}
