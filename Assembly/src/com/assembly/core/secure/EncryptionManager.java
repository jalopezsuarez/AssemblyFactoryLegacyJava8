/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.core.secure;

import java.util.Base64;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.MessageDigest;

import com.assembly.core.trace.Trace;

/**
 *
 * @author Administrator
 */
public class EncryptionManager
{

    private static EncryptionManager INSTANCE = null;

    public static EncryptionManager instance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new EncryptionManager();
        }
        return INSTANCE;
    }

    // =======================================================
    private static final String AES_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final byte[] PRIVATE_KEY_BYTES = new byte[]
    {
        0x3d,
        0x4f,
        0x35,
        0x7e,
        0x48,
        0x7b,
        0x29,
        0x71,
        0x2a,
        0x4f,
        0x63,
        0x57,
        0x3b,
        0x3a,
        0x68,
        0x3c
    };

    private Cipher cipher = null;
    private SecretKeySpec key = null;

    private EncryptionManager()
    {
        try
        {
            key = new SecretKeySpec(PRIVATE_KEY_BYTES, "AES");
            cipher = Cipher.getInstance(AES_ALGORITHM);
        }
        catch (Exception | Error ex)
        {
            Trace.printStackTrace(ex);
        }
    }

    public String encrypt(String data) throws Exception
    {
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] cipherValue = cipher.doFinal(data.getBytes());
        String encodeValue = new String(Base64.getEncoder().encode(cipherValue), "UTF-8");
        String escapeValue = URLEncoder.encode(encodeValue, "UTF-8");

        return escapeValue;
    }

    public String decrypt(String data) throws Exception
    {
        cipher.init(Cipher.DECRYPT_MODE, key);

        String unescapeValue = URLDecoder.decode(data, "UTF-8");
        byte[] decodeValue = Base64.getDecoder().decode(unescapeValue);
        byte[] cipherValue = cipher.doFinal(decodeValue);
        String decryptValue = new String(cipherValue, "UTF-8");

        return decryptValue;
    }

    public String encode(String data, Encryption encode) throws Exception
    {
        String escapeValue = "";
        if (encode.equals(Encryption.MD5))
        {
            MessageDigest encoder = MessageDigest.getInstance("MD5");
            encoder.update(data.getBytes(Charset.forName("UTF-8")));
            escapeValue = String.format("%032x", new BigInteger(1, encoder.digest()));
        }
        else if (encode.equals(Encryption.SHA256))
        {
            MessageDigest encoder = MessageDigest.getInstance("SHA-256");
            encoder.update(data.getBytes("utf8"));
            escapeValue = String.format("%064x", new BigInteger(1, encoder.digest()));
        }
        return escapeValue;
    }
}
