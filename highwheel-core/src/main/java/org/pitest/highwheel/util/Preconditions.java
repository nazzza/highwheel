package org.pitest.highwheel.util;

public class Preconditions {

  public static void checkState(final boolean expression) {
    if (!expression) {
      throw new IllegalStateException();
    }
  }

}
