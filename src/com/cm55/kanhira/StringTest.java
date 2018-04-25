package com.cm55.kanhira;

import java.io.*;

import com.cm55.kanhira.kanwafile.*;

public class StringTest {

  public static void main(String[]args) {

    KanwaFileReader dict = new KanwaFileReader();
    
    Kanhira kakasi = new Kanhira(dict);
    
    //String string = "寿限無のソラは京都に晴れる。spice効かせてgoodなソング";
    String string = "悪名高い悪代官の悪巧みによって、貴社の記者が汽車で帰社した";
    // String string = "悪巧み";
    KanjiInput input = new KanjiInput(new StringReader(string));

    StringWriter writer = new StringWriter(string.length() * 2);
    KanjiOutput output = new KanjiOutput(writer);
    kakasi.convert(input, output);
    String result = writer.toString();
    System.out.println("" + result);
    dict.close();
  }
}
