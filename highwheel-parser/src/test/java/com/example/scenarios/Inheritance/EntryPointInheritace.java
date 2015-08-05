package com.example.scenarios.Inheritance;

public class EntryPointInheritace {
  // When the parent class is in the declaration (parent c = new child()), the
  // method inside the child class is being recognised as an orphan, despite
  // being called from the entry point
  // When the child class is in the declaration (child c = new child()), the
  // method inside the parent class is being recognised as an orphan

  public void entryPoint() {
    ChildClassExtendsParentClass c = new ChildClassExtendsParentClass();
    c.parentMethod();
  }

}
