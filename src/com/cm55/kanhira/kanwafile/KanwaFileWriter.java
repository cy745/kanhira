package com.cm55.kanhira.kanwafile;


import java.io.*;
import java.util.*;

import com.cm55.kanhira.*;

/**
 * This class represents the Kanwa dictionary.
 *
 * @see Kanhira#getKanwaDictionary()
 * @author  Kawao, Tomoyuki (kawao@kawao.com)
 * @version $Revision: 1.6 $ $Date: 2003/01/01 08:54:30 $
 */
public class KanwaFileWriter {

    private final Map<Character,TreeSet<KanjiYomi>> contentsTable =
        new HashMap<Character,TreeSet<KanjiYomi>>(8192);

    private Map<Character,KanwaFileEntry> entryTable;


    private RandomAccessFile file;
    
    public KanwaFileWriter() {
    }
    
    /* (non-Javadoc)
     * @see com.kawao.kakasi.KanwaDict#load(java.lang.String)
     */

    public void load(String filename) throws IOException {
        load(filename, "JISAutoDetect");
    }

    /* (non-Javadoc)
     * @see com.kawao.kakasi.KanwaDict#load(java.lang.String, java.lang.String)
     */
    public void load(String filename, String encoding) throws IOException {
        InputStream in = new FileInputStream(filename);
        try {
            Reader reader = new InputStreamReader(in, encoding);
            try {
                load(reader);
            } finally {
                try {
                    reader.close();
                    in = null;
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see com.kawao.kakasi.KanwaDict#load(java.io.Reader)
     */
    public void load(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        while (true) {
            String line = in.readLine();
            if (line == null) {
                break;
            }
            int length = line.length();
            if (length == 0) {
                continue;
            }
            Character.UnicodeBlock yomiBlock =
                Character.UnicodeBlock.of(line.charAt(0));
            if (!yomiBlock.equals(Character.UnicodeBlock.HIRAGANA) &&
                !yomiBlock.equals(Character.UnicodeBlock.KATAKANA)) {
                continue;
            }
            StringBuffer yomiBuffer = new StringBuffer();
            yomiBuffer.append(line.charAt(0));
            int index = 1;
            for (; index < length; index++) {
                char ch = line.charAt(index);
                if (" ,\t".indexOf(ch) >= 0) {
                    break;
                }
                yomiBuffer.append(ch);
            }
            if (index >= length) {
                System.err.println("KanwaDictionary: Ignored line: " + line);
                continue;
            }
            char okurigana = '\u0000';
            char yomiLast = yomiBuffer.charAt(index - 1);
            if (yomiLast >= 'a' && yomiLast <= 'z') {
                okurigana = yomiLast;
                yomiBuffer.setLength(index - 1);
            }
            String yomi = yomiBuffer.toString();
            for (++index; index < length; index++) {
                char ch = line.charAt(index);
                if (" ,\t".indexOf(ch) < 0) {
                    break;
                }
            }
            if (index >= length) {
                System.err.println("KanwaDictionary: Ignored line: " + line);
                continue;
            }
            if (line.charAt(index) == '/') {
            SKK_LOOP:
                while (true) {
                    StringBuffer kanji = new StringBuffer();
                    for (++index; index < length; index++) {
                        char ch = line.charAt(index);
                        if (ch == '/') {
                            break;
                        }
                        if (ch == ';') {
                            index = length - 1;
                            break;
                        }
                        if (ch == '[') {
                            break SKK_LOOP;
                        }
                        kanji.append(ch);
                    }
                    if (index >= length) {
                        break;
                    }
                    addItem(kanji.toString(), yomi, okurigana);
                }
            } else {
                StringBuffer kanji = new StringBuffer();
                kanji.append(line.charAt(index));
                for (++index; index < length; index++) {
                    char ch = line.charAt(index);
                    if (" ,\t".indexOf(ch) >= 0) {
                        break;
                    }
                    kanji.append(ch);
                }
                addItem(kanji.toString(), yomi, okurigana);
            }
        }
    }

    /* (non-Javadoc)
     * @see com.kawao.kakasi.KanwaDict#addItem(java.lang.String, java.lang.String, char)
     */
    public synchronized void addItem(String kanji,
                                     String yomi,
                                     char okurigana) {
        Character.UnicodeBlock kanjiBlock =
            Character.UnicodeBlock.of(kanji.charAt(0));
        if (!kanjiBlock.equals(
                Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)) {
            //System.err.println("KanwaDictionary: Ignored item:" +
            //                   " kanji=" + kanji + " yomi=" + yomi);
            return;
        }
        int kanjiLength = kanji.length();
        StringBuffer kanjiBuffer = new StringBuffer(kanjiLength);
        /*
        for (int index = 0; index < kanjiLength; index++) {
            char ch = kanji.charAt(index);
            //if (ch < '\u0100') {
            //    System.err.println("KanwaDictionary: Ignored item:" +
            //                       " kanji=" + kanji + " yomi=" + yomi);
            //    return;
            //}
            kanjiBuffer.append(ItaijiTable.getInstance().get(ch));
        }
        */
        kanjiBuffer.append(ItaijiTable.getInstance().convert(kanji));
        Character key = new Character(kanjiBuffer.charAt(0));
        kanji = kanjiBuffer.substring(1);

        int yomiLength = yomi.length();
        StringBuffer yomiBuffer = new StringBuffer(yomiLength);
        for (int index = 0; index < yomiLength; index++) {
            char ch = yomi.charAt(index);
            Character.UnicodeBlock block = Character.UnicodeBlock.of(ch);
            if (!block.equals(Character.UnicodeBlock.HIRAGANA) &&
                !block.equals(Character.UnicodeBlock.KATAKANA)) {
                System.err.println("KanwaDictionary: Ignored item:" +
                                   " kanji=" + kanjiBuffer + " yomi=" + yomi);
                return;
            }
            if ((ch >= '\u30a1' && ch <= '\u30f3') ||
                ch == '\u30fd' || ch == '\u30fe') {
                yomiBuffer.append((char)(ch - 0x60));
            } else if (ch == '\u30f4') {  // 'vu'
                yomiBuffer.append('\u3046');
                yomiBuffer.append('\u309b');
            } else {
                yomiBuffer.append(ch);
            }
        }
        yomi = yomiBuffer.toString();   

        KanjiYomi kanjiYomi = new KanjiYomi(kanji, yomi, okurigana);
        TreeSet<KanjiYomi> list = contentsTable.get(key);
        if (list == null) {
            list = new TreeSet<KanjiYomi>();
            contentsTable.put(key, list);
        }
        list.add(kanjiYomi);
    }


    /* (non-Javadoc)
     * @see com.kawao.kakasi.KanwaDict#close()
     */
    public synchronized void close() throws IOException {
        if (file != null) {
            file.close();
            file = null;
        }
    }

    /**
     * Called when there are no more references to this object.
     *
     * @exception Throwable  the Exception raised by this method
     */
    public void finalize() throws Throwable {
        close();
        super.finalize();
    }

    /* (non-Javadoc)
     * @see com.kawao.kakasi.KanwaDict#save(java.io.RandomAccessFile)
     */
    public synchronized void save(RandomAccessFile file) throws IOException {
        int numKanji = contentsTable.size();
        entryTable = new HashMap<Character,KanwaFileEntry>(numKanji);
        file.writeInt(numKanji);
        file.seek(4 + 8 * numKanji);
        Iterator<Character> keys = contentsTable.keySet().iterator();
        while (keys.hasNext()) {
            int offset = (int)file.getFilePointer();
            Character key = keys.next();
            Set<KanjiYomi> list = contentsTable.get(key);
            Iterator<KanjiYomi> kanjiYomis = list.iterator();
            while (kanjiYomis.hasNext()) {
                KanjiYomi kanjiYomi = kanjiYomis.next();
                file.writeUTF(kanjiYomi.getKanji());
                file.writeUTF(kanjiYomi.getYomi());
                file.writeByte(kanjiYomi.getOkuriIni());
           }
            KanwaFileEntry entry = new KanwaFileEntry(offset, list.size());
            entryTable.put(key, entry);
        }
        file.seek(4);
        keys = entryTable.keySet().iterator();
        while (keys.hasNext()) {
            Character key = (Character)keys.next();
            file.writeChar(key.charValue());
            KanwaFileEntry entry = (KanwaFileEntry)entryTable.get(key);
            file.writeInt(entry.getOffset());
            file.writeShort(entry.getNumberOfWords());
        }
    }

    /**
     * Main program of 'mkkanwa_j'.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 2 || args[0].equals("-h")) {
            System.err.println("usage: mkkanwa_j kanwadict dict1 [dict2,,,]");
            System.exit(1);
        } 

        KanwaFileWriter dictionary = new KanwaFileWriter();
        for (int index = 1; index < args.length; index++) {
            dictionary.load(args[index]);
        }
        RandomAccessFile file = new RandomAccessFile(args[0], "rw");
        try {
            dictionary.save(file);
        } finally {
            try {
                file.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

}
