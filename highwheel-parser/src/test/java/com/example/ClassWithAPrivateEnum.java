package com.example;

public class ClassWithAPrivateEnum {
  public enum ClassType {
    WITHENUM, WITHOUTENUM
  }

  // private final ClassType type = ClassType.WITHENUM;
  // private final ClassType notType = ClassType.WITHOUTENUM;

}
