/*
 * $Id: KanjiConverterImpl.java,v 1.2 2003/01/01 08:18:44 kawao Exp $
 *
 * KAKASI/JAVA
 *  Copyright (C) 2002-2003  KAWAO, Tomoyuki (kawao@kawao.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.cm55.kanhira;

import java.util.*;
import java.util.stream.*;

/**
 * This class implements conversion methods that converts a Kanji word.
 * 
 * @author Kawao, Tomoyuki (kawao@kawao.com)
 * @version $Revision: 1.2 $ $Date: 2003/01/01 08:18:44 $
 */
public class KanjiConverter implements Converter {

  /** 漢字辞書 */
  private final KanwaDict[]kanwaDict;

  /**
   * 使用する漢字辞書を指定する
   */
  public KanjiConverter(KanwaDict[] kanwaDict) {
    this.kanwaDict = kanwaDict;
  }

  /**
   * Converts the Kanji word into the Hiragana word.
   * 
   * @param input 変換対称入力
   * @param output 結果出力
   * @return true:変換が行われた。false:変換は行われなかった。
   */
  public Optional<String>convert(Input input) {

    //　先頭漢字を取得する。異体字であれば変換しておく
    char key = ItaijiTable.convert((char)input.first());
    
    // 先頭漢字用のKanjiYomiListのリストを取得する
    List<KanjiYomiList> list = Arrays.stream(kanwaDict)
        .map(dict->dict.lookup(key))
        .filter(Optional::isPresent).map(Optional::get)
        .collect(Collectors.toList());
    if (list.size() == 0) return Optional.empty();

    // 複数のKanjiYomiListの最大文字列長の最大値を先読み文字数とする
    int readAHead = list.stream().mapToInt(l->l.maxWholeLength()).max().getAsInt();

    // 先読みを行いチェック文字列を作成。先頭文字は読み込み済なので一文字減らす。
    String checkString = key + ItaijiTable.convert(input.read(readAHead - 1));

    // 各KanjiYomiListについて、最初に一致する複数のKanjiYomiオブジェクトを取得する
    return list.stream()
      .map(l->l.stream() // KanjiYomiListストリームについて処理する
        .filter(ky->ky.getYomiFor(checkString).isPresent()) // チェック対象文字列に一致するKanjiYomiだけを流す
        .findFirst() // 最初のものを取得する
      )
      .filter(Optional::isPresent).map(Optional::get) // 存在するものだけをストリームに流す
      .sorted(Comparator.comparing(KanjiYomi::wholeLength).reversed()) // 長い順にソート
      .findFirst() // 最初のものを選択
      .map(ky-> { // もしあればそれを処理
        // consumeする
        input.consume(ky.wholeLength());

        // よみを返す
        return ky.getYomiFor(checkString);
      }).orElse(Optional.empty());
  }
}
