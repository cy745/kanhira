package com.cm55.kanhira;

import java.io.*;
import java.util.*;

/**
 * This class represents the Kanwa dictionary.
 *
 * @see Kanhira#getKanwaDictionary()
 * @author Kawao, Tomoyuki (kawao@kawao.com)
 * @version $Revision: 1.6 $ $Date: 2003/01/01 08:54:30 $
 */
public class KakasiDictReader {

  private static final String DEFAULT_ENCODING = "JISAutoDetect";

  /*
   * (non-Javadoc)
   * 
   * @see com.kawao.kakasi.KanwaDict#load(java.lang.String)
   */
  public static KanjiYomiMap load(String filename) throws IOException {
    return load(filename, DEFAULT_ENCODING);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.kawao.kakasi.KanwaDict#load(java.lang.String, java.lang.String)
   */
  public static KanjiYomiMap load(String filename, String encoding) throws IOException {
    try (InputStream in = new FileInputStream(filename)) {
      return load(in, encoding);
    }
  }
  
  public static KanjiYomiMap load(InputStream in) throws IOException {
    return load(in,  DEFAULT_ENCODING);
  }
  
  public static KanjiYomiMap load(InputStream in, String encoding) throws IOException {
    try (Reader reader = new InputStreamReader(in, encoding)) {      
      return load(reader);
    }    
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.kawao.kakasi.KanwaDict#load(java.io.Reader)
   */
  public static KanjiYomiMap load(Reader reader) throws IOException {
    KanjiYomiMap map = new KanjiYomiMap();
    BufferedReader in = new BufferedReader(reader);
    while (true) {
      String line = in.readLine();
      if (line == null) {
        break;
      }
      int length = line.length();
      if (length == 0) {
        continue;
      }
      Character.UnicodeBlock yomiBlock = Character.UnicodeBlock.of(line.charAt(0));
      if (!yomiBlock.equals(Character.UnicodeBlock.HIRAGANA) && !yomiBlock.equals(Character.UnicodeBlock.KATAKANA)) {
        continue;
      }
      StringBuffer yomiBuffer = new StringBuffer();
      yomiBuffer.append(line.charAt(0));
      int index = 1;
      for (; index < length; index++) {
        char ch = line.charAt(index);
        if (" ,\t".indexOf(ch) >= 0) {
          break;
        }
        yomiBuffer.append(ch);
      }
      if (index >= length) {
        System.err.println("KanwaDictionary: Ignored line: " + line);
        continue;
      }
      Optional<Character>okurigana = Optional.empty();
      char yomiLast = yomiBuffer.charAt(index - 1);
      if (CharKind.isOkurigana(yomiLast)) {
        okurigana = Optional.of(yomiLast);
        yomiBuffer.setLength(index - 1);
      }
      String yomi = yomiBuffer.toString();
      for (++index; index < length; index++) {
        char ch = line.charAt(index);
        if (" ,\t".indexOf(ch) < 0) {
          break;
        }
      }
      if (index >= length) {
        System.err.println("KanwaDictionary: Ignored line: " + line);
        continue;
      }
      if (line.charAt(index) == '/') {
        SKK_LOOP: while (true) {
          StringBuffer kanji = new StringBuffer();
          for (++index; index < length; index++) {
            char ch = line.charAt(index);
            if (ch == '/') {
              break;
            }
            if (ch == ';') {
              index = length - 1;
              break;
            }
            if (ch == '[') {
              break SKK_LOOP;
            }
            kanji.append(ch);
          }
          if (index >= length) {
            break;
          }
          addItem(map, kanji.toString(), yomi, okurigana);
        }
      } else {
        StringBuffer kanji = new StringBuffer();
        kanji.append(line.charAt(index));
        for (++index; index < length; index++) {
          char ch = line.charAt(index);
          if (" ,\t".indexOf(ch) >= 0) {
            break;
          }
          kanji.append(ch);
        }
        addItem(map, kanji.toString(), yomi, okurigana);
      }
    }
    return map;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.kawao.kakasi.KanwaDict#addItem(java.lang.String, java.lang.String,
   * char)
   */
  static void addItem(KanjiYomiMap map, String kanji, String yomi, Optional<Character>okurigana) {
    Character.UnicodeBlock kanjiBlock = Character.UnicodeBlock.of(kanji.charAt(0));
    if (!kanjiBlock.equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)) {
      // System.err.println("KanwaDictionary: Ignored item:" +
      // " kanji=" + kanji + " yomi=" + yomi);
      return;
    }
    int kanjiLength = kanji.length();
    StringBuffer kanjiBuffer = new StringBuffer(kanjiLength);
    /*
     * for (int index = 0; index < kanjiLength; index++) { char ch =
     * kanji.charAt(index); //if (ch < '\u0100') { //
     * System.err.println("KanwaDictionary: Ignored item:" + // " kanji=" +
     * kanji + " yomi=" + yomi); // return; //}
     * kanjiBuffer.append(ItaijiTable.getInstance().get(ch)); }
     */
    kanjiBuffer.append(ItaijiTable.getInstance().convert(kanji));
    Character key = new Character(kanjiBuffer.charAt(0));
    kanji = kanjiBuffer.substring(1);

    int yomiLength = yomi.length();
    StringBuffer yomiBuffer = new StringBuffer(yomiLength);
    for (int index = 0; index < yomiLength; index++) {
      char ch = yomi.charAt(index);
      Character.UnicodeBlock block = Character.UnicodeBlock.of(ch);
      if (!block.equals(Character.UnicodeBlock.HIRAGANA) && !block.equals(Character.UnicodeBlock.KATAKANA)) {
        System.err.println("KanwaDictionary: Ignored item:" + " kanji=" + kanjiBuffer + " yomi=" + yomi);
        return;
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
    yomi = yomiBuffer.toString();

    map.add(key,  new KanjiYomi(kanji, yomi, okurigana));
  }

}
