package com.example;

public class Jwxt {


    public static void main(String[] args)
    {

    }

    public Jwxt()
    {

        JSONObject obj =  connect("http://www.xingkong.us/home/index.php/Home/index/login?xh=" + et_username.getText() + "&pw=" + et_password.getText() + "&code=" + et_yzm.getText());
    }
}
