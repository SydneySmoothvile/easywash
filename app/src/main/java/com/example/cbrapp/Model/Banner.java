package com.example.cbrapp.Model;

public class Banner {
    //lookbook and banner is the same
    private String image;

    public Banner(){

    }

    public Banner (String image){
        this.image=image;
    }

    public String getImage(){
        return image;
    }

    public void setImage(String image){
        this.image = image;
    }
}
