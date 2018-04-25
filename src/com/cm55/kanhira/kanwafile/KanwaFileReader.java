/*
 * $Id: KanwaDictionary.java,v 1.6 2003/01/01 08:54:30 kawao Exp $
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

package com.cm55.kanhira.kanwafile;

import java.io.*;
import java.util.*;

import com.cm55.kanhira.*;

/**
 * This class represents the Kanwa dictionary.
 * 
 * @see Kanhira#getKanwaDictionary()
 * @author Kawao, Tomoyuki (kawao@kawao.com)
 * @version $Revision: 1.6 $ $Date: 2003/01/01 08:54:30 $
 */
public class KanwaFileReader implements KanwaDict {

  private RandomAccessFile file;

  private Map<Character, KanwaFileEntry> entryTable;

  private final Map<Character, KanjiYomiList> contentsTable = new HashMap<Character, KanjiYomiList>(
      8192);

  /**
   * Looks up the specified character.
   * 
   * @param k
   *          the character to look up.
   * @return the iterator of KanjiYomi obujects.
   * @exception IOException
   *              if an error occurred when reading kanwa dictionary file.
   */
  public synchronized KanjiYomiList lookup(char key) {

    initialize();

    KanjiYomiList kanjiYomiList = contentsTable.get(key);
    if (kanjiYomiList != null) {
      return kanjiYomiList;
    }

    KanwaFileEntry entry = entryTable.get(key);
    if (entry == null) {
      contentsTable.put(key, kanjiYomiList = new KanjiYomiList());
      return kanjiYomiList;
    }

    contentsTable.put(key, kanjiYomiList = createList(entry));
    return kanjiYomiList;
  }

  KanjiYomiList createList(KanwaFileEntry entry) {
    assert entry != null;
    List<KanjiYomi> list = new ArrayList<KanjiYomi>();
    try {
      file.seek(entry.getOffset());
      int numWords = entry.getNumberOfWords();
      for (int index = 0; index < numWords; index++) {
        String kanji = file.readUTF();
        String yomi = file.readUTF();
        char okurigana = (char) file.readByte();
        KanjiYomi k = new KanjiYomi(kanji, yomi, okurigana);
        // ystem.out.println("" + k);
        list.add(k);
      }
    } catch (IOException ex) {
      throw new KanhiraException(ex);
    }
    Collections.sort(list);
    return new KanjiYomiList(list);
  }

  /**
   * Initializes this object.
   * 
   * @exception IOException
   *              if an error occurred when reading kanwa dictionary file.
   */
  private void initialize() {
    if (entryTable != null)
      return;
    String path = System.getProperty("kakasi.kanwaDictionary");
    if (path == null) {
      String home = System.getProperty("kakasi.home");
      path = home + "/lib/kanwadict";
    }

    try {
      file = new RandomAccessFile("dict/kanwadict", "r");
      int numKanji = file.readInt();
      entryTable = new HashMap<Character, KanwaFileEntry>(numKanji);

      for (int index = 0; index < numKanji; index++) {
        Character key = new Character(file.readChar());
        int offset = file.readInt();
        int numWords = file.readShort();
        entryTable.put(key, new KanwaFileEntry(offset, numWords));
      }
    } catch (IOException ex) {
      throw new KanhiraException(ex);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.kawao.kakasi.KanwaDict#close()
   */

  public synchronized void close() {
    if (file != null) {
      try {
        file.close();
      } catch (IOException ex) {
        throw new KanhiraException(ex);
      }
      file = null;
    }
  }
}
