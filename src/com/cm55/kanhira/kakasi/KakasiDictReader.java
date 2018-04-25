package com.cm55.kanhira.kakasi;

import java.io.*;

import com.cm55.kanhira.*;
import com.cm55.kanhira.dict.*;
import com.cm55.kanhira.itaiji.*;

/**
 * This class represents the Kanwa dictionary.
 *
 * @see Kanhira#getKanwaDictionary()
 * @author Kawao, Tomoyuki (kawao@kawao.com)
 * @version $Revision: 1.6 $ $Date: 2003/01/01 08:54:30 $
 */
public class KakasiDictReader {

  private final KanjiYomiMap map = new KanjiYomiMap();

  public KanjiYomiMap getMap() {
    return map;
  }
  

  /*
   * (non-Javadoc)
   * 
   * @see com.kawao.kakasi.KanwaDict#load(java.lang.String)
   */

  public KakasiDictReader(String filename) throws IOException {
    this(filename, "JISAutoDetect");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.kawao.kakasi.KanwaDict#load(java.lang.String, java.lang.String)
   */
  public KakasiDictReader(String filename, String encoding) throws IOException {
    InputStream in = new FileInputStream(filename);
    try {
      Reader reader = new InputStreamReader(in, encoding);
      try {
        load(reader);
      } finally {
        try {
          reader.close();
          in = null;
        } catch (IOException exception) {
          exception.printStackTrace();
        }
      }
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException exception) {
          exception.printStackTrace();
        }
      }
    }
  }

  public KakasiDictReader(Reader reader) throws IOException {
    load(reader);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kawao.kakasi.KanwaDict#load(java.io.Reader)
   */
  private  void load(Reader reader) throws IOException {
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
      char okurigana = '\u0000';
      char yomiLast = yomiBuffer.charAt(index - 1);
      if (yomiLast >= 'a' && yomiLast <= 'z') {
        okurigana = yomiLast;
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
          addItem(kanji.toString(), yomi, okurigana);
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
        addItem(kanji.toString(), yomi, okurigana);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.kawao.kakasi.KanwaDict#addItem(java.lang.String, java.lang.String,
   * char)
   */
  void addItem(String kanji, String yomi, char okurigana) {
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
