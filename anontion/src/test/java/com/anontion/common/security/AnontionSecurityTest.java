package com.anontion.common.security;

import org.junit.Test;
import static org.junit.Assert.*;

public class AnontionSecurityTest {

  @Test
  public void testAddition() {

    System.out.println("hello");
  }

  @Test
  public void testNotNull() {
    String str = "hello";
    assertNotNull("String should not be null", str);
  }

}
