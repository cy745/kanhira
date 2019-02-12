package com.cm55.kanhira;

import java.util.*;

/**
 * 漢字・ひらがなペアを読み込みパースし、{@link KanjiYomi}作成に必要なデータ、
 * {@link KanjiYomiMap}登録に必要なデータを取得する。
 * 漢字でない、かなではない等のエラー時には{@link IllegalArgumentException}が発生する。
 * @author ysugimura
 */
class Parsed {

  /** 漢字先頭の一文字 */
  final char key;
  
  /** 漢字二文字目以降 */
  final String kanji;
  
  /** よみ */
  final String yomi;
  
  /** 送り仮名 */
  final Optional<Character>okurigana;
  
  Parsed(String kanjiInput, String yomiInput) {

    Character.UnicodeBlock kanjiBlock = Character.UnicodeBlock.of(kanjiInput.charAt(0));
    if (!kanjiBlock.equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)) {
      throw new IllegalArgumentException("First character not Kanji:" + kanjiInput);
    }
    
    String kanjiConverted = ItaijiTable.getInstance().convert(kanjiInput);
    key = kanjiConverted.charAt(0);
    kanji = kanjiConverted;
    
    YomiOkuri yomiOkuri = new YomiOkuri(yomiInput);
    yomi = convertYomi(yomiOkuri.yomi);
    okurigana = yomiOkuri.okurigana;
  }

  static class YomiOkuri {
    final String yomi;
    final Optional<Character>okurigana;
    YomiOkuri(String yomiIn) {
      int yomiLength = yomiIn.length();
      char yomiLast = yomiIn.charAt(yomiLength - 1);
      if (CharKind.isOkurigana(yomiLast)) {
        okurigana = Optional.of(yomiLast);
        yomi = yomiIn.substring(0, yomiLength - 1);
        return;
      }
      yomi = yomiIn;
      okurigana = Optional.empty();    
    }
  }
  
  String convertYomi(String yomi) {
    int yomiLength = yomi.length();
    StringBuffer yomiBuffer = new StringBuffer(yomiLength);
    
    for (char ch: yomi.toCharArray()) {
      Character.UnicodeBlock block = Character.UnicodeBlock.of(ch);
      if (!block.equals(Character.UnicodeBlock.HIRAGANA) && !block.equals(Character.UnicodeBlock.KATAKANA)) {
        throw new IllegalArgumentException("Not Hiragana or Katakana:" + yomi);
      }
      if ((ch >= '\u30a1' && ch <= '\u30f3') || ch == '\u30fd' || ch == '\u30fe') {
        yomiBuffer.append((char) (ch - 0x60));
      } else if (ch == '\u30f4') { // 'vu'
        yomiBuffer.append('\u3046');
        yomiBuffer.append('\u309b');
      } else {
        yomiBuffer.append(ch);
      }
    }
    return yomiBuffer.toString();    
  }
}
