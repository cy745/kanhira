package com.cm55.kanhira;

import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.runners.Suite.*;

@RunWith(Suite.class)
@SuiteClasses( { 
  KanjiYomiMapTest.class,
  MainTest.class,
  OkuriganaTableTest.class,
})
public class AllTest {
  public static void main(String[] args) {
    JUnitCore.main(AllTest.class.getName());
  }
}
