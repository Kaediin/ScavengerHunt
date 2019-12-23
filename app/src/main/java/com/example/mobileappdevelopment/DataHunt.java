package com.example.mobileappdevelopment;

class DataHunt {

    private static String title = null;

    static void setTitleHunt(String titleInput){
        title = titleInput;
    }

    static String getTitleHunt(){
        return title;
    }
}
