package com.cm55.kanhira;

import java.io.*;

import com.cm55.kanhira.kanwafile.*;

public class StdTest {

  /**
   * Main program of 'kakasi_j'.
   *
   * @param args command line arguments.
   */
  public static void main(String[] args) throws Throwable {

    KanwaFileReader dict = new KanwaFileReader();
      Kanhira kakasi = new Kanhira(dict);
      KanjiInput input = new KanjiInput(new BufferedReader(new InputStreamReader(System.in)));
      KanjiOutput output = new KanjiOutput(new OutputStreamWriter(System.out));
      kakasi.convert(input, output);
      dict.close();
  }
}
