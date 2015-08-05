package com.example.scenarios.InterfaceCall;

public class EntryPoint {

  public void entryPoint() {
    // When the parent class is in the declaration (parent c = new child()), the
    // method inside the child class is being recognised as an orphan, despite
    // being called from the entry point
    // When the child class is in the declaration (child c = new child()), the
    // method inside the parent class is being recognised as an orphan

    ImplementsAnInterfaceWithAMethod f = new ImplementsAnInterfaceWithAMethod();
    f.aMethodToImplement();
  }
}
