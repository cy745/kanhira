package com.cm55.kanhira;

import java.io.*;

import com.cm55.kanhira.dict.*;
import com.cm55.kanhira.kanwafile.*;

public class StringTest {

  public static void main(String[]args) throws IOException  {

    KanwaFileReader dict = new KanwaFileReader(
      new KanwaFileAccess.RandomFile("dict/kanwadict")
    );

    
    Kanhira kakasi = new Kanhira((KanwaDict)dict);
    
    String string = "寿限無のソラは京都に晴れる。spice効かせてgoodなソング";
    //String string = "悪名高い悪代官の悪巧みによって、貴社の記者が汽車で帰社した";
    System.out.println("" +  kakasi.convert(string));
    /*
    dict.allEntries().forEach(e-> {
      System.out.println("---------------" + e.key);
      System.out.println("" + dict.getContent(e));
    });
    */
    
    dict.close();
  }
}
