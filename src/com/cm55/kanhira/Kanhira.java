/*
 * $Id: Kakasi.java,v 1.15 2003/03/01 12:52:26 kawao Exp $
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

import java.io.*;

/**
 * This class is the KAKASI/JAVA main class.
 * 
 * @author Kawao, Tomoyuki (kawao@kawao.com)
 * @version $Revision: 1.15 $ $Date: 2003/03/01 12:52:26 $
 */
public class Kanhira {

  private static final Converter defaultConverter = new DefaultConverter();
  private static final Converter hiraganaConverter = new HiraganaConverter();
  private static final Converter katakanaConverter = new KatakanaConverter();
  private final Converter kanjiConverter;
  
  /**
   * Constructs a Kakasi object with the specified kanwa dictionary.
   * 
   * @param kanwaDictionary
   *          the KanwaDictionary object.
   */
  public Kanhira(KanwaDict kanwaDict) {
    assert kanwaDict != null;
    kanjiConverter = new KanjiConverter(kanwaDict);
  }

  /**
   * Runs the conversion process.
   * 
   * @exception IOException
   *              if an I/O error occurred.
   */
  public String convert(Converter.Input input) {
    StringBuilder s = new StringBuilder();
    while (true) {
      int ch = input.first();
      if (ch < 0) {
        break;
      }
      String converted = getConverter((char)ch).convert(input);
      if (converted == null) {
        input.consume(1);
      } else {
        s.append(converted);
      }
    }
    return s.toString();
  }
  
  Converter getConverter(char ch) {
    Character.UnicodeBlock block = CharKind.unicodeBlock((char)ch);
    if (CharKind.isKanji(block))    return kanjiConverter;
    if (CharKind.isHiragana(block)) return hiraganaConverter;
    if (CharKind.isKatakana(block)) return katakanaConverter;
    return defaultConverter;
  }
  
  public String convert(String string) {
    Converter.Input input = new Converter.Input(new StringReader(string));
    return this.convert(input);
  }
}
