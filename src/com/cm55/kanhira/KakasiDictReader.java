package com.cm55.kanhira;

import java.io.*;
import java.util.*;
import java.util.stream.*;

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
      addLine(map, line);
    }
    return map;
  }

  static void addLine(KanjiYomiMap map, String line) {
    int length = line.length();
    if (length == 0) {
      return;
    }
    Character.UnicodeBlock yomiBlock = Character.UnicodeBlock.of(line.charAt(0));
    if (!yomiBlock.equals(Character.UnicodeBlock.HIRAGANA) && !yomiBlock.equals(Character.UnicodeBlock.KATAKANA)) {
      return;
    }
    
    String[]splited =
        Arrays.stream(line.split("[ ,\t]")).filter(s->s.length() > 0)
        .collect(Collectors.toList()).toArray(new String[0]);
    if (splited.length < 2) {
      System.err.println("KanwaDictionary: Ignored line: " + line);
      return;
    }
    String kanji = splited[1];
    String yomi = splited[0];
    
    // 行の先頭によみがあり、それは空白、カンマ、タブのいずれかで区切られている。
    YomiOkuri yomiOkuri = new YomiOkuri(yomi);
    try {
      addItem(map, kanji, yomiOkuri.yomi, yomiOkuri.okurigana);   
 //     map.add(kanji.charAt(0),  kanjiYomi);
    } catch (IllegalArgumentException ex) {
      System.err.println("KanwaDictionary:" + ex.getMessage());
    }
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
  
  /*
   * (non-Javadoc)
   * 
   * @see com.kawao.kakasi.KanwaDict#addItem(java.lang.String, java.lang.String,
   * char)
   */
  static void addItem(KanjiYomiMap map, String kanji, String yomi, Optional<Character>okurigana) {
    
    // 漢字の先頭文字を調べる
    Character.UnicodeBlock kanjiBlock = Character.UnicodeBlock.of(kanji.charAt(0));
    if (!kanjiBlock.equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)) {
      throw new IllegalArgumentException("First character not Kanji:" + kanji);
    }
    
    int kanjiLength = kanji.length();
    StringBuffer kanjiBuffer = new StringBuffer(kanjiLength);

    kanjiBuffer.append(ItaijiTable.getInstance().convert(kanji));
    Character key = new Character(kanjiBuffer.charAt(0));

    kanji = kanjiBuffer.substring(1);


    yomi = convertYomi(yomi);

    map.add(key, new KanjiYomi(kanji, yomi, okurigana));
  }

  static String convertYomi(String yomi) {
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
