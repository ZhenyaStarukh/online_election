package com.example.election.classes.auxiliaryClasses;

public class CaesarCipher {
    public static String cipher(String message, int offset, boolean decipher){
        StringBuilder result = new StringBuilder();
        for (char character:message.toCharArray()){
            char firstLetter;
            if (character!=' '){
                if (isUpperCase(character)) firstLetter = 'A';
                else if (isNum(character)) firstLetter = '0';
                else firstLetter = 'a';

                int originalAlphabetPosition = character - firstLetter;

                int newAlphabetPosition;
                if(isNum(character))
                {
                    if(decipher) {
                        newAlphabetPosition = (originalAlphabetPosition + 10 - (offset*2)+1) % 10;
                    }
                    else newAlphabetPosition = (originalAlphabetPosition + (offset*2)-1) % 10;
                }
                else {
                    if(decipher) {
                        newAlphabetPosition = (originalAlphabetPosition + 26 - offset) % 26;
                    }
                    else newAlphabetPosition = (originalAlphabetPosition + offset) % 26;
                }

                char newCharacter = (char)(firstLetter+newAlphabetPosition);

                result.append(newCharacter);
            }
            else
                result.append(character);
        }
        return result.toString();
    }


    private static boolean isUpperCase(char c){
        return c >= 'A' && c <= 'Z';
    }

    private static boolean isNum(char c){
        return c >= '0' && c<= '9';
    }
}
