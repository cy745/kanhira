/*
 * $Id: KanjiOutput.java,v 1.2 2003/01/01 08:18:44 kawao Exp $
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
 * 文字列出力インターフェース
 * @author admin
 */
public class KanjiOutput {

  private Writer writer;

  /**
   * {@link Writer}を指定する。
   */
  public KanjiOutput(Writer writer) {
    this.writer = writer;
  }

  /** 文字列を書き込む */
  public void write(String s)  {
    try {
      writer.write(s);
    } catch (IOException ex) {
      throw new KanhiraException(ex);
    }
  }
  
  /**
   * 一文字を書き込む
   */
  public void write(int c)  {
    assert writer != null;
    try {
      writer.write(c);
    } catch (IOException ex) {
      throw new KanhiraException(ex);
    }
  }

  /**
   * フラッシュする
   */
  public synchronized void flush() {
    if (writer != null) {
      try {
        writer.flush();
      } catch (IOException ex) {
        throw new KanhiraException(ex);
      }
    }
  }

  /**
   * クローズする
   */
  public void close() {
    if (writer != null) {
      try {
        writer.close();
      } catch (IOException ex) {
        throw new KanhiraException(ex);
      }
    }
  }
}
