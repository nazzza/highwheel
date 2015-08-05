package com.example.InheritanceCall;

public class EntryPointForInheritanceCall {

  public void entryPoint() {
    AParentWithAMethod child = new AChildImplementingAParentWithAMethod();
    child.aMethod();

  }

}
