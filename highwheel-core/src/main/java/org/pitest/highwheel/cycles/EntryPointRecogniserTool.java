package org.pitest.highwheel.cycles;

public interface EntryPointRecogniserTool {

  boolean isEntryPoint(int access, String name, String desc);

}
