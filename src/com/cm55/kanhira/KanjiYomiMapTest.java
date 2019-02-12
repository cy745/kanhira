package com.cm55.kanhira;

import static org.junit.Assert.*;

import org.junit.*;

public class KanjiYomiMapTest {

  @Test
  public void test() {
    KanjiYomiMap map = new KanjiYomiMap();
    map.add("悪名高", "あくめいたかi");
    map.add("悪名高", "あくめいたかk");
    map.add("悪代官", "あくだいかん");
    Kanhira kakasi = new Kanhira(map);
    assertEquals("あくめいたかくあくめいたかいあくだいかん",
        kakasi.convert("悪名高く悪名高い悪代官"));
  }

}
