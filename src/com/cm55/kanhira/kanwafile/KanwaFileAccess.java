package com.cm55.kanhira.kanwafile;

import java.io.*;

public interface KanwaFileAccess {

  public void seek(long position) throws IOException;
  public String readUTF() throws IOException ;
  public byte readByte() throws IOException ;
  public char readChar() throws IOException ;
  public int readInt() throws IOException ;
  public short readShort() throws IOException ;
  public void close() throws IOException ;
  
  public static class RandomFile implements KanwaFileAccess {
    RandomAccessFile file;
    public RandomFile(String path) throws IOException {
      file = new RandomAccessFile(path, "r");
    }
    @Override
    public void seek(long position) throws IOException {
      file.seek(position);
    }
    
    @Override
    public String readUTF() throws IOException {
      return file.readUTF();
    }
    
    @Override
    public byte readByte()  throws IOException {
      return file.readByte();
    }
    @Override
    public char readChar()  throws IOException {
      return file.readChar();
    }
    @Override
    public int readInt()  throws IOException {
      return file.readInt();
    }
    @Override
    public short readShort()  throws IOException {
      return file.readShort();
    }
    @Override
    public void close()  throws IOException {
      file.close();      
    }
  }
}
