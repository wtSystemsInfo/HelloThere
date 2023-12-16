package com.wtsystems.hallothere.Helper;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

public class Base64Custom {
    public static String code64Base(String text){
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)", "");
    }

    public static String decode64Base(String text){
        return new String(Base64.decode(text, Base64.DEFAULT));
    }

}
