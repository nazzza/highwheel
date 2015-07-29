package com.example.scenarios;

public class MemberOfCycle2 {

  public static void foo(boolean recurse) {
    if (!recurse) {
      new MemberOfCycle1().entry();
      recurse = true;
    }
  }
}
