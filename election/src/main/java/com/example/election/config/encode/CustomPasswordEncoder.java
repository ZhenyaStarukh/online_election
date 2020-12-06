package com.example.election.config.encode;

import com.example.election.classes.auxiliaryClasses.CaesarCipher;
import com.example.election.services.AuxiliaryService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.NoSuchAlgorithmException;

public class CustomPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence charSequence) {
        String hashed;
        hashed = BCrypt.hashpw(CaesarCipher.cipher(charSequence.toString(),3,false),
                BCrypt.gensalt(4));
        try {
            return AuxiliaryService.cipher(charSequence.toString());
        } catch (NoSuchAlgorithmException e) {
            return hashed;
        }
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return encode(charSequence.toString()).equals(s);
    }
}
