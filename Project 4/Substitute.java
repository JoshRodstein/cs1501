
import java.util.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Substitution encryption
 * @author joshuarodstein
 */
public class Substitute implements SymCipher {

    // 2 byte arrays to hold key, and transposed key for decoding
    private byte[] key;
    private byte[] deKey;

    // Prameterless constructor
    public Substitute() {
        // New ArrayList<Byte> to allow for shuffling with Collections
        ArrayList<Byte> permByte = new ArrayList<Byte>(); 
        key = new byte[256];  // initialize key and dekey to size of 256
        deKey = new byte[256];

        // load ArrayList with byte representations of 0-255
        for (int i = 0; i < 256; i++) {
            permByte.add((byte) i);
        }

        // shuffle ArrayList to randomize the key
        Collections.shuffle(permByte);

        // loop to copy elements from ArrayList to key, and transposition to deKey
        for (int i = 0; i < 256; i++) {
            key[i] = permByte.get(i);
            deKey[key[i] & 0xFF] = (byte) i;
        }

    }

    // Parameterized coonstructor, accepts byte array as argument/key
    // no need to randomize. Simply loop to copy transposed elements into deKey
    public Substitute(byte[] b) {
        if (b.length != 256) {
            throw new IllegalArgumentException("Illegal key parameters:");
        }

        this.key = b.clone();
        deKey = new byte[256];

        for (int i = 0; i < 256; i++) {
            deKey[b[i] & 0xFF] = (byte) i;
        }
    }

    // return copy fo the key. 
    @Override
    public byte[] getKey() {
        return key.clone();
    }

    // Encode method. 
    @Override
    public byte[] encode(String S) {
        byte[] byteStr = S.getBytes();
        byte[] codeStr = new byte[S.length()];
        // loop through key and substitute key elements for message elements
        for (int i = 0; i < byteStr.length; i++) {
            codeStr[i] = key[byteStr[i] & 0xFF];
        }

        // return encrypted message as array of bytes
        return codeStr.clone();
    }

    // decode method
    // uses transposed key array to decrypt message
    @Override
    public String decode(byte[] bytes) {
        byte[] deBytes = new byte[bytes.length];

        // load transposed key array elements into deBytes
        for (int i = 0; i < bytes.length; i++) {
            deBytes[i] = (deKey[bytes[i] & 0xFF]);
        }
        
        // return decrypted message as a String object
        return new String(deBytes);
    }

    

}
