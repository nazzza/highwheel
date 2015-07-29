package org.pitest.highwheel.cycles;

public interface EntryPointRecogniser {

  boolean isEntryPoint(int access, String name, String desc);

}
