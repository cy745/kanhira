package com.cm55.kanhira;

import java.util.*;
import java.util.stream.*;

public class KanjiYomiMap implements KanwaDict {

  private Map<Character, KanjiYomiList>map = 
      new HashMap<Character, KanjiYomiList>(8192);

  /**
   * 漢字とそのよみを登録する。
   * 送り仮名の無い場合には"悪代官", "あくだいかん"のように登録すればよいが、送り仮名の
   * ある場合にはそれを半角小文字アルファベットにする必要がある。
   * 例えば、"悪名高い"の場合には"あくめいたかi"
   * @param kanji 漢字。例えば"悪名高"
   * @param yomi よみ。例えば"あくめいたかi"
   */
  public void add(String kanji, String yomi) {
    Parsed p = new Parsed(kanji, yomi);
    add(p.key, new KanjiYomi(p.kanji, p.yomi, p.okurigana));
  }

  
  /** 
   * 漢字の最初の文字と{@link KanjiYomi}を辞書に追加する
   * @param key '悪名高い'の場合は'悪'
   * @param kanjiYomi 上記漢字の残りの部分の読みがな。{@link KanjiYomi}を参照のこと。
   */
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

  public Stream<Map.Entry<Character, KanjiYomiList>>stream() {
    return map.entrySet().stream();
  }
  
  /** デバッグ用。文字列化 */
  @Override
  public String toString() {  
    return map.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey))
      .map(e->e.getKey() + "----\n" + e.getValue().toString() + "\n")
      .collect(Collectors.joining());    
  }
}
