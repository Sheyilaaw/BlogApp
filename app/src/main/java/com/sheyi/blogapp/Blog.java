package com.sheyi.blogapp;

/**
 * Created by USER on 11/23/2017.
 */

public class Blog {
    private String Title, Desc, Image ;

    public Blog(){

    }

    public Blog(String title, String desc, String image) {
        Title = title;
        Desc = desc;
        Image = image;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }


}
