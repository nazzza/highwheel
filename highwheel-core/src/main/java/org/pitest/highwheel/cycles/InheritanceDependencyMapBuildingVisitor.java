package org.pitest.highwheel.cycles;

import static org.pitest.highwheel.model.AccessType.IMPLEMENTS;
import static org.pitest.highwheel.model.AccessType.INHERITANCE;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.pitest.highwheel.classpath.AccessVisitor;
import org.pitest.highwheel.model.AccessPoint;
import org.pitest.highwheel.model.AccessType;
import org.pitest.highwheel.model.ElementName;

public class InheritanceDependencyMapBuildingVisitor implements AccessVisitor {

  // Map <Child, SetOfParents>
  private final Map<ElementName, Set<ElementName>> m = new LinkedHashMap<ElementName, Set<ElementName>>();

  public Map<ElementName, Set<ElementName>> getMap() {
    return m;
  }

  @Override
  public void apply(final AccessPoint source, final AccessPoint dest,
      final AccessType type) {
    final ElementName sourceClass = source.getElementName();
    final ElementName destClass = dest.getElementName();
    if (type.equals(INHERITANCE) || type.equals(IMPLEMENTS)) {
      LinkedHashSet<ElementName> p = new LinkedHashSet<ElementName>();
      p.add(destClass);
      m.put(sourceClass, p);
    }
  }

  @Override
  public void newNode(final ElementName clazz) {

  }

  @Override
  public void newAccessPoint(final AccessPoint ap) {

  }

  @Override
  public void newEntryPoint(final AccessPoint ap) {

  }

}
