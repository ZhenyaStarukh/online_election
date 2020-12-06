package com.example.election.services;

import com.example.election.classes.auxiliaryClasses.CaesarCipher;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

public class AuxiliaryService {
    public static String cipher(String password) throws NoSuchAlgorithmException {
        String salt = "elec";
        if(password.length()>4) salt = password.substring(password.length()-3);
        password = CaesarCipher.cipher(password+salt,3,false);
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        String myHash = DatatypeConverter.printHexBinary(digest);
//        System.out.println(password+" : "+myHash);
        return myHash;
    }

//    public static String cipher(String password, String name, boolean decipher) throws NoSuchAlgorithmException {
//        return CaesarCipher.cipher(password+name,3,decipher);
//    }



    public static String encodeId(String id) throws NoSuchAlgorithmException{
        return CaesarCipher.cipher(id,3,false);
    }

    public static String decodeId(String id) throws NoSuchAlgorithmException{
        return CaesarCipher.cipher(id,3,true);
    }

    public static boolean timeIsOk(Timestamp open, Timestamp close){
        return open.before(close);
    }
}
