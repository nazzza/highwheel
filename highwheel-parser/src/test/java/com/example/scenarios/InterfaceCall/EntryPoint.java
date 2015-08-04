package com.example.scenarios.InterfaceCall;

public class EntryPoint {

  public void entryPoint() {
    AnInterfaceWithAMethod f = new ImplementsAnInterfaceWithAMethod();
    f.aMethodToImplement();
  }
}
