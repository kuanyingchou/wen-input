package com.android.inputmethodcommon;

import java.io.*;
import java.util.*;

public class WordTable {
    private final Map<String, List<Word>> words =
            new HashMap<String, List<Word>>();
    private final Map<String, String> keys =
            new HashMap<String, String>();

    public WordTable(String path) {
        parse(path);
    }
    
    public WordTable(InputStream is) {
        parse(is);
    }
    
    private void parse(InputStream is) {
        final String data = readTextFile(is);
        parse(data);
    }
    
    private enum ParsingState { START, KEYNAME, CHARDEF }
    
    private void parse(String data) {
        System.out.println("start parsing...");
        
        final BufferedReader reader = new BufferedReader(
                new StringReader(data));
        try { 
            String line = reader.readLine();
            ParsingState state = ParsingState.START;
            while(line != null) {
                switch(state) {
                case START:
                    if(line.startsWith("%chardef  begin")) {
                        state = ParsingState.CHARDEF;
                    } else if(line.startsWith("%keyname  begin")) {
                        state = ParsingState.KEYNAME;
                    }
                    break;
                case KEYNAME:
                    if(line.startsWith("%keyname  end")) {
                        state = ParsingState.START;
                        break;
                    }
                    final String[] k = line.trim().split("\\s+");
                    keys.put(k[0], k[1]);
                    System.out.println(k[0] + " -> \"" + k[1]+"\"");
                    break;
                case CHARDEF:
                    if(line.startsWith("%chardef  end")) {
                        state = ParsingState.START;
                        break;
                    }
                    final String trimmedLine = line.trim();
                    final String[] kv = trimmedLine.split(" ");
                    if(words.containsKey(kv[0])) {
                        words.get(kv[0]).add(new Word(kv[1]));
                    } else {
                        final List<Word> a = new ArrayList<Word>();
                        a.add(new Word(kv[1]));
                        words.put(kv[0], a);
                    }
                    //System.out.println("add "+kv[1] + " to " +kv[0]);                    
                    break;
                }
                line = reader.readLine();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        System.out.println("done");
    }
    public List<Word> get(String keyName) {

        final String key = getKeyCode(keyName);
        if(key == null) return new ArrayList<Word>();

        System.out.println("get words for "+keyName+" : "+key);        
        
        if(words.containsKey(key)) {
            return words.get(key);
        } else {
            return new ArrayList<Word>();
            //throw new RuntimeException("key not found:  "+ key);
        }
    }
    public String getKeyName(String key) {
        if(keys.containsKey(key)) {
            return keys.get(key);
        } else {
            throw new IllegalArgumentException();
        }
    }
    public String getKeyCode(String keyName) {
        final StringBuilder sb = new StringBuilder();
        for(int i=0; i<keyName.length(); i++) {
            for(String k : keys.keySet()) {
                if(keys.get(k).equals(String.valueOf(keyName.charAt(i)))) {
                    sb.append(k);
                }
            }
        }
        return sb.toString();
    }

    //===========================================
    public static void testBasic() {
        final WordTable t = new WordTable("./assets/phone.cin");

        System.out.println(t.get("ji3").get(0));  
        System.out.println(t.get("g4").get(0)); 
        System.out.println(t.get("5.").get(0));   
        System.out.println(t.get("ej04").get(0)); 
        System.out.println(t.get("u/3").get(0));  
    }
    public static void testArgs(String[] keys) {
        final WordTable t = new WordTable("./assets/phone.cin");
        for(int i=0; i<keys.length; i++) {
            //System.out.println(join(t.get(keys[i]), ", "));
            //System.out.print(t.get(keys[i]).get(0));
            System.out.println(join(subList(t.get(keys[i]), 10), ", "));
        }
        System.out.println();
    }
    public static void testInterpreter() throws IOException {
        final WordTable table = new WordTable("./assets/phone.cin");
        final BufferedReader br = new BufferedReader(
                new InputStreamReader(System.in));
        while(true) {
            final String input = br.readLine();
            System.out.println(table.get(input.trim()));
        }
    }
    public static void main(String[] args) throws IOException {
        //testBasic();
        //testArgs(args);
        testInterpreter();
    }

    //===============================
    public static <T> List<T> subList(List<T> target, int size) {
        if(target.size() <= size) {
            return new ArrayList<T>(target);
        } else {
            return target.subList(0, size);
        }
    }

    public static String join(List<?> objs, String del) {
        return join(objs.toArray(), del);
    }
    public static <T> String join(T[] objs, String del) {
        StringBuilder sb = new StringBuilder();
        if(objs.length > 1) {
            sb.append(objs[0].toString());
        }
        for(int i=1; i<objs.length; i++) {
            sb.append(del);
            sb.append(objs[i]);
        }
        return sb.toString();
    }

    public static String readTextFile(InputStream is) {
        String everything="";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append('\n');
                line = br.readLine();
            }
            everything = sb.toString();
            br.close();
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
        }
        return everything;
    }
    
    public static String readTextFile(String path) {
        String everything="";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append('\n');
                line = br.readLine();
            }
            everything = sb.toString();
            br.close();
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
        }
        return everything;
    }
}


