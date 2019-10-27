package com.falconssoft.woodysystem;

import android.net.Uri;

import java.net.URI;

public class SettingsFile {

    /**
     *  AuthenticationFailedException
     *  less secure apps
     *  solution:
     *     Gmail- Host: smtp.gmail.com , Port: 587
     *     Hotmail- Host: smtp.live.com , Port: 587
     *     Yahoo- Host: smtp.mail.yahoo.com , Port: 587
     * */

    public static String hostName = "smtp.gmail.com";

    public static String senderName = "1234developersteam@gmail.com";

    public static String senderPassword = "androiddevelopers4";

    public static String recipientName = "rawanfalcons2017@gmail.com";

    public static String emailTitle = "Wood App";

    public static String emailContent;

//    public static Uri emailContent;

    public static String ipAddress;

    public static String Location;

    public static String area;

}
