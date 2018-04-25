package com.cm55.kanhira;

import java.io.*;

public class StringTest {

  public static void main(String[]args) throws IOException  {

    /*
    KanwaFileReader dict = new KanwaFileReader(
      new KanwaFileAccess.RandomFile("dict/kanwadict")
    );
    */
    KanjiYomiMap map  = KakasiDictReader.load("dict/kakasidict");
    
    Kanhira kakasi = new Kanhira(map);
    
//    String string = "果たし";
    String string = "海外での介入を終了し、アメリカ軍を国に帰還させるという彼の選挙公約を、トランプがなんらかの形で実行しているという、ありがちな誤解が存在しているように見える。";
//    String string = "ヨーロッパ全土、また、ヨーロッパとの関係を通じて他の大陸にも、われわれは騒乱と混乱と敵愾心を起こさなければならない";
  //  String string = "安心安全なヤフオク!実現に向けたYahoo!かんたん決済の仕様変更について（再掲）";
    //String string = "寿限無のソラは京都に晴れる。spice効かせてgoodなソング";
    //String string = "悪名高い悪代官の悪巧みによって、貴社の記者が汽車で帰社した";
    System.out.println("" +  kakasi.convert(string));
    /*
    dict.allEntries().forEach(e-> {
      System.out.println("---------------" + e.key);
      System.out.println("" + dict.getContent(e));
    });
    */
    
    //dict.close();
  }
}
