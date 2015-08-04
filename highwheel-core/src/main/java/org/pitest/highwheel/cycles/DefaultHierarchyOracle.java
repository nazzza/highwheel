package org.pitest.highwheel.cycles;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.pitest.highwheel.model.ElementName;

public class DefaultHierarchyOracle implements HierarchyOracle {

  private final Set<ElementName>                   parents = new LinkedHashSet<ElementName>();
  private final Map<ElementName, Set<ElementName>> m;

  public DefaultHierarchyOracle(final Map<ElementName, Set<ElementName>> m) {
    this.m = m;
  }

  @Override
  public Set<ElementName> findParents(final ElementName element) {
    if (m.containsKey(element)) {
      for (ElementName parent : m.get(element)) {
        parents.add(parent);
        findParents(parent);
      }
    }
    return parents;
  }

}
