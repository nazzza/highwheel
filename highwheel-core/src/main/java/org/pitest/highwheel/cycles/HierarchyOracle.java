package org.pitest.highwheel.cycles;

import java.util.Set;

import org.pitest.highwheel.model.ElementName;

public interface HierarchyOracle {

  Set<ElementName> findParents(ElementName element);

  Set<ElementName> findChildren(ElementName element);

}
