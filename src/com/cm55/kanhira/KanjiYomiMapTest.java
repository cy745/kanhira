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
    map.add("悪徳不動産屋",  "あくとくふどうさんや"); 
        
    assertEquals(
      "徳不動産屋,あくとくふどうさんや,,5\n" + 
      "名高,あくめいたか,i,3\n" + 
      "名高,あくめいたか,k,3\n" + 
      "代官,あくだいかん,,2", map.lookup('悪').toString());
    
    Kanhira kakasi = new Kanhira(map);
    assertEquals("あくめいたかくあくめいたかいあくだいかん",
        kakasi.convert("悪名高く悪名高い悪代官"));
  }

}
