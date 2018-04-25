package com.cm55.kanhira;

import java.util.*;

/**
 * 最初の文字が同じ漢字の熟語について、その漢字の長さ順に{@link KanjiYomi}を格納したもの。
 * 例えば、最初が「悪」の場合には、
 * <ul>
 * <li>徳不動産屋
 * <li>天候時
 * <li>代官
 * </ul>
 * の順序で格納されている。この順で処理ができるように{@link Iterable}を実装する。
 * つまり、最長一致の方針になっている。
 * 例えば対称文字列が「悪い」であった場合、「わる」よりも「わるい」が先に一致する。
 */
public class KanjiYomiList implements Iterable<KanjiYomi> {

  private List<KanjiYomi>list;

  /** 何も格納されていない{@link KanjiYomiList}を作成する */
  public KanjiYomiList() {
    this.list = null;
  }

  /** {@link KanjiYomi}のリストをあたえて作成する。
   * リストは{@link KanjiYomi#wholeLength()}の大きい順にソートされる。 */
  public KanjiYomiList(List<KanjiYomi>list) {
    Collections.sort(list);
    this.list = list;
  }
  
  /** 何も格納されていないことを示す */
  public boolean isEmpty() {
    return list == null || list.size() == 0;
  }
  
  /**
   * このリストに含まれる{@link KanjiYomi}の{@link KanjiYomi#wholeLength()}のうちの最大長を返す。
   * @return
   */
  public int maxWholeLength() {
    return list.get(0).wholeLength();
  }

  /**
   * リストに含まれる{@link KanjiYomi}を順に返すイテレータを返す。
   */
  @Override
  public Iterator<KanjiYomi> iterator() {
    
    return new Iterator<KanjiYomi>() {
      int index = 0;
      
      @Override
      public boolean hasNext() {
        return index < list.size();
      }

      @Override
      public KanjiYomi next() {
        return list.get(index++);
      }   
    };
  }


}