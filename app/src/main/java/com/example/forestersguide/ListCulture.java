package com.example.forestersguide;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.util.BitSet;

public class ListCulture {
    private int id;
    private String name;
    private String description;
    private Bitmap img=null;

    public ListCulture(int id, String name, String description, Bitmap img){
        this.id = id;
        this.name = name;
        this.description=description;
        this.img = img;
    }

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getName(){
        return name;
    }
    public void setName(){
        this.name = name;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(){
        this.description = description;
    }
    public Bitmap getImage(){
        return img;
    }
    public void setImage(Bitmap img){
        this.img = img;
    }

    public byte[] getImageByteArray() {
        if (img==null) return null;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
       return outputStream.toByteArray();
    }
}
