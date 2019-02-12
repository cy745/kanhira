package com.cm55.kanhira;

import java.util.*;
import java.util.stream.*;

/**
 * 最初の文字が同じ漢字の熟語について、その漢字の長さ順に{@link KanjiYomi}を格納したもの。
 * 例えば、最初が「悪」の場合には、
 * <ul>
 * <li>徳不動産屋
 * <li>天候時
 * <li>代官
 * </ul>
 * の順序で格納されている。
 * つまり、最長一致の方針になっている。
 * 例えば対象文字列が「悪い」であった場合、「わる」よりも「わるい」が先に一致する。
 */
public class KanjiYomiList  {

  /**
   * この{@link TreeSet}は{@link KanjiYomi#compareTo(KanjiYomi)}によって順序付けられる。
   * {@link KanjiYomi}はその{@link KanjiYomi#wholeLength()}の逆順に順序付けされる。
   */
  private TreeSet<KanjiYomi>list = new TreeSet<>();

  /** このリストに含まれる{@link KanjiYomi}の{@link KanjiYomi#wholeLength()}のうちの最大長 */
  private Integer maxWholeLength = null;
  
  /** {@link KanjiYomi}を追加する */
  public void add(KanjiYomi kanjiYomi) {
    list.add(kanjiYomi);
    maxWholeLength = null;
  }
  
  public List<KanjiYomi>getList() {
    return new ArrayList<KanjiYomi>(list);
  }
  
  public void setList(Collection<KanjiYomi>list) {
    this.list = new TreeSet<KanjiYomi>(list);
  }

  /**
   * このリストに含まれる{@link KanjiYomi}の{@link KanjiYomi#wholeLength()}のうちの最大長を返す。
   * @return
   */
  public int maxWholeLength() {
    if (maxWholeLength == null) {
      OptionalInt i = list.stream().mapToInt(e->e.wholeLength()).max();      
      maxWholeLength = i.orElse(0);
    }
    return maxWholeLength;
  }

  /** 処理用のストリームを取得 */
  public Stream<KanjiYomi>stream() {
    return list.stream();
  }
  
  /**
   * デバッグ用。文字列化
   */
  @Override
  public String toString() {
    return list.stream().map(s->s.toString()).collect(Collectors.joining("\n"));
  }
}