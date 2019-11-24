package com.falconssoft.woodysystem;

import android.net.Uri;

import com.falconssoft.woodysystem.models.Users;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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

    public static String serialNumber;

//    public static String companyName = "";

//    public static String ipAddress = "";//5.189.130.98:8085

//    public static String store = "";

//    public static String area;

    public static List<Users> usersList = new ArrayList<>();

}
