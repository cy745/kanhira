package com.cm55.kanhira;

import java.util.*;

/**
 * あるひらがな文字が漢字の送り仮名として適当かどうかをチェックするためのテーブル。
 * 例えば、「悪く」の「く」という送り仮名が適当かどうかは、「悪」という熟語の送り仮名イニシャルとして「k」が
 * あるかないかで決まる。
 * @author admin
 */
public class OkuriganaTable {

  
  /**
   * チェック対称の文字が送り仮名として適当であるかを調べる。
   * @param target チェック対称の文字
   * @param okurigana 送り仮名マーク
   * @return true:適当、false:不適当
   */
  public static boolean check(char target, char okurigana) {
    
    // その文字を送り仮名テーブルで探す。なければ不適当な送り仮名
    String okuriganaList = map.get(target);
    if (okuriganaList == null) return false;

    // この漢字の送り仮名としてありえなければエラー。
    if (okuriganaList.indexOf(okurigana) < 0) return false;
    
    // 送り仮名が適当
    return true;
  }
  
  private static final Object[] TABLE = {
    'ぁ', "aiueow", // 3041
    'あ', "aiueow", // 3042
    'ぃ', "aiueow", // 3043
    'い', "aiueow", // 3044
    'ぅ', "aiueow", // 3045
    'う', "aiueow", // 3046
    'ぇ', "aiueow", // 3047
    'え', "aiueow", // 3048
    'ぉ', "aiueow", // 3049
    'お', "aiueow", // 304a
    'か', "k", // 304b
    'が', "g", // 304c
    'き', "k", // 304d
    'ぎ', "g", // 304e
    'く', "k", // 304f
    'ぐ', "g", // 3050
    'け', "k", // 3051
    'げ', "g", // 3052
    'こ', "k", // 3053
    'ご', "g", // 3054
    'さ', "s", // 3055
    'ざ', "zj", // 3056
    'し', "s", // 3057
    'じ', "zj", // 3058
    'す', "s", // 3059
    'ず', "zj", // 305a
    'せ', "s", // 305b
    'ぜ', "zj", // 305c
    'そ', "s", // 305d
    'ぞ', "zj", // 305e
    'た', "t", // 305f
    'だ', "d", // 3060
    'ち', "tc", // 3061
    'ぢ', "d", // 3062
    'っ', "aiueokstchgzjfdbpw", // 3063
    'つ', "t", // 3064
    'づ', "d", // 3065
    'て', "t", // 3066
    'で', "d", // 3067
    'と', "t", // 3068
    'ど', "d", // 3069
    'な', "n", // 306a
    'に', "n", // 306b
    'ぬ', "n", // 306c
    'ね', "n", // 306d
    'の', "n", // 306e
    'は', "h", // 306f
    'ば', "b", // 3070
    'ぱ', "p", // 3071
    'ひ', "h", // 3072
    'び', "b", // 3073
    'ぴ', "p", // 3074
    'ふ', "hf", // 3075
    'ぶ', "b", // 3076
    'ぷ', "p", // 3077
    'へ', "h", // 3078
    'べ', "b", // 3079
    'ぺ', "p", // 307a
    'ほ', "h", // 307b
    'ぼ', "b", // 307c
    'ぽ', "p", // 307d
    'ま', "m", // 307e
    'み', "m", // 307f
    'む', "m", // 3080
    'め', "m", // 3081
    'も', "m", // 3082
    'ゃ', "y", // 3083
    'や', "y", // 3084
    'ゅ', "y", // 3085
    'ゆ', "y", // 3086
    'ょ', "y", // 3087
    'よ', "y", // 3088
    'ら', "rl", // 3089
    'り', "rl", // 308a
    'る', "rl", // 308b
    'れ', "rl", // 308c
    'ろ', "rl", // 308d
    'ゎ', "wiueo", // 308e
    'わ', "wiueo", // 308f
    'ゐ', "wiueo", // 3090
    'ゑ', "wiueo", // 3091
    'を', "w", // 3092
    'ん', "n", // 3093
    'ヵ', "k", // 30f5
    'ヶ', "k", // 30f6
  };
  
  private static final Map<Character, String> map = new HashMap<>();
  static {
    for (int i = 0; i < TABLE.length; i += 2) {
      map.put((Character)TABLE[i + 0], (String)TABLE[i + 1]);
    }    
  }

}
