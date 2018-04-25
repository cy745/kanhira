package com.cm55.kanhira.dict;

import java.util.*;

public class KanjiYomiMap implements KanwaDict {

  private Map<Character, KanjiYomiList>map = new HashMap<Character, KanjiYomiList>(8192);

  public void add(char key, KanjiYomi kanjiYomi) {
    KanjiYomiList list = map.get(key);
    if (list == null) {
      list = new KanjiYomiList();
      map.put(key, list);
    }
    list.add(kanjiYomi);
  }

  @Override
  public KanjiYomiList lookup(char k) {
    return map.get(k);
  }
}
