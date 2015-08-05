package org.pitest.highwheel.cycles;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.pitest.highwheel.model.ElementName;

public class DefaultHierarchyOracle implements HierarchyOracle {

  private final Map<ElementName, Set<ElementName>> m;

  public DefaultHierarchyOracle(final Map<ElementName, Set<ElementName>> m) {
    this.m = m;
  }

  public Map<ElementName, Set<ElementName>> getMap() {
    return m;
  }

  @Override
  public Set<ElementName> findParents(final ElementName element) {
    Set<ElementName> parents = new LinkedHashSet<ElementName>();
    if (m.containsKey(element)) {
      for (ElementName parent : m.get(element)) {
        parents.add(parent);
        parents.addAll(findParents(parent));
      }
    }
    return parents;
  }

  @Override
  public Set<ElementName> findChildren(final ElementName element) {
    Set<ElementName> children = new LinkedHashSet<ElementName>();
    Set<Map.Entry<ElementName, Set<ElementName>>> entrySet = m.entrySet();
    for (Map.Entry<ElementName, Set<ElementName>> entry : entrySet) {
      Set<ElementName> parents = entry.getValue();
      if (parents.contains(element)) {
        children.add(entry.getKey());
        children.addAll(findChildren(entry.getKey()));
      }
    }
    return children;
  }

}
