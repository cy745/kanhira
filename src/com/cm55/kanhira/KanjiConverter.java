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

/**
 * This class implements conversion methods that converts a Kanji word.
 * 
 * @author Kawao, Tomoyuki (kawao@kawao.com)
 * @version $Revision: 1.2 $ $Date: 2003/01/01 08:18:44 $
 */
public class KanjiConverter implements Converter {

  /** 漢字辞書 */
  private final KanwaDict kanwaDict;

  /**
   * 使用する漢字辞書を指定する
   */
  public KanjiConverter(KanwaDict kanwaDict) {
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

    // 先頭漢字用のKanjiYomiListを取得する
    return kanwaDict.lookup(key).map(kanjiYomiList -> {

      // KanjiYomiListの最大長を取得し、それをチェック用文字列とする。これは先頭漢字以外の分
      // 異体字を普通字に変換しておく。
      String checkString = key + ItaijiTable.convert(input.read(kanjiYomiList.maxWholeLength() - 1));

      // KanjiYomiListは長い順にされているので、最長一致のために順に比較していく
      Optional<KanjiYomi> opt = kanjiYomiList.stream().filter(ky -> ky.getYomiFor(checkString).isPresent()).findFirst();
      if (opt.isPresent()) {
        // consumeする。1は最初の漢字の分
        input.consume(opt.get().wholeLength());

        // よみを返す
        return opt.get().getYomiFor(checkString);
      }

      // 変換されなかった。
      return Optional.<String>empty();
      
    }).orElse(Optional.<String>empty());
  }
}
