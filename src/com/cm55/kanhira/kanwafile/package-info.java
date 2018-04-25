/**
 * 一つのファイルとして{@link KanwaDict}を扱う場合のクラス群。
 * {@link KanwaDict}インターフェースの実装としてはいろいろ考えられるが、このクラス群は単一の辞書ファイル
 * として扱うことを想定している。
 * つまり、kakasidictを{@link KanwaFileWriter}で変換してkanwadictを得て、
 * それを{@link KanwaFileReader}で読み込むことによりメモリ上に辞書を作成することを想定している。
 */
package com.cm55.kanhira.kanwafile;
