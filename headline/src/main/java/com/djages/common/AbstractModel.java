package com.djages.common;



import java.util.Random;

public abstract class AbstractModel implements Comparable<Object>{

    private String id;


    public AbstractModel(String id){
        this.id = id;
    }
    public AbstractModel(){
    }


    public void setId(String id){
        this.id = id;
    }

    public String getId() {
        if(id == null){
            id = genRandomId();
        }
        return id;
    }


    @Override
    public abstract int compareTo(Object model);


    public static String genRandomId(){
        char[] chars = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 12; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }
}