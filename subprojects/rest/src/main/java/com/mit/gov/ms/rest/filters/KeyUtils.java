/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.rest.filters;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


/**
 * Utility methods to read / write private / public keys
 */
public class KeyUtils {
    
    private KeyUtils() {
    }

    public static final String PUBLIC_KEY = "PUBLIC KEY";
    public static final String PRIVATE_KEY = "PRIVATE KEY";

    private static final String BEGIN = "-----BEGIN ";
    private static final String END = "-----END ";
    private static final String TRAILING = "-----";
    private static final String PEM_FILE_ENCODING = StandardCharsets.ISO_8859_1.name();

    /**
     * Generate a public/private key pair.
     * 
     * @return a key pair
     * @throws NoSuchAlgorithmException if the algorithm used for generating the
     *             key pair is not found
     */
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstanceStrong();
        keyGen.initialize(1024, random);
        KeyPair keyPair = keyGen.generateKeyPair();
        return keyPair;
    }

    /**
     * Read the private key from the given PEM file
     * 
     * @param fnamePEM the name of the PEM file
     * @return a private key
     * @throws Exception if anything goes wrong
     */
    public static PrivateKey readPrivateKeyPEM(String fnamePEM) throws Exception {
        byte[] encoded = readPEM(fnamePEM, PRIVATE_KEY);

        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey key = null;
        if (encoded.length > 0) {
            key = kf.generatePrivate(new PKCS8EncodedKeySpec(encoded));
        }
        return key;
    }

    /**
     * Read the public key from the given PEM file
     * 
     * @param fnamePEM the name of the PEM file
     * @return a public key
     * @throws Exception if anything goes wrong
     */
    public static PublicKey readPublicKeyPEM(String fnamePEM) throws Exception {
        byte[] encoded = readPEM(fnamePEM, PUBLIC_KEY);

        return readPublicKeyPEM(encoded);
    }

    /**
     * Read the public key from the given string which is in PEM format
     * 
     * @param pemStr the PEM string
     * @return a public key
     * @throws Exception if anything goes wrong
     */
    public static PublicKey readPublicKeyPEMString(String pemStr) throws Exception {
        byte[] encoded = readPEMString(pemStr, PUBLIC_KEY);

        return readPublicKeyPEM(encoded);
    }

    /**
     * Read the public key from the given bytes
     * 
     * @param encoded the encoded content of public key
     * @return a public key
     * @throws Exception if anything goes wrong
     */
    private static PublicKey readPublicKeyPEM(byte[] encoded) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey key = null;
        if (encoded.length > 0) {
            key = kf.generatePublic(new X509EncodedKeySpec(encoded));
        }
        return key;
    }

    /**
     * Write the public or private key key in the given PEM file.
     * 
     * @param fnamePEM the name of the PEM file
     * @param publicKey the public key
     * @throws Exception if anything goes wrong
     */
    public static void writePEM(String fnamePEM, Key key) throws Exception {
        if (key instanceof PublicKey) {
            writePEM(fnamePEM, PUBLIC_KEY, key.getEncoded());
        } else if (key instanceof PrivateKey) {
            writePEM(fnamePEM, PRIVATE_KEY, key.getEncoded());
        } else {
            throw new IllegalArgumentException("Error write PEM");
        }
    }

    /**
     * Read the content of the given label in the PEM file. PEM defines a
     * one-line header, consisting of "-----BEGIN ", a label, and "-----", the
     * content, and a one-line footer, consisting of "-----END ", a label, and
     * "-----". This method read the content between the header and footer.
     * 
     * @param fnamePEM the name of the file
     * @param label the label of the content
     * @return the content
     * @throws Exception if anything goes wrong
     */
    private static byte[] readPEM(String fnamePEM, String label) throws Exception {
        try (LineNumberReader lnr = new LineNumberReader(
                new InputStreamReader(new BufferedInputStream(new FileInputStream(fnamePEM)), PEM_FILE_ENCODING))) {
            return readPEM(label, lnr);
        }
    }

    /**
     * Read the content of the given label in the PEM string. PEM defines a
     * one-line header, consisting of "-----BEGIN ", a label, and "-----", the
     * content, and a one-line footer, consisting of "-----END ", a label, and
     * "-----". This method read the content between the header and footer.
     * 
     * @param pemStr the PEM string
     * @param label the label of the content
     * @return the content
     * @throws Exception if anything goes wrong
     */
    private static byte[] readPEMString(String pemStr, String label) throws Exception {
        try (LineNumberReader lnr = new LineNumberReader(new StringReader(pemStr))) {
            return readPEM(label, lnr);
        }
    }

    /**
     * Read the content between the begin and end lines from the reader
     * 
     * @param lineBegin the begin line
     * @param lineEnd the end line
     * @param lnr the reader
     * @return the content
     * @throws Exception if anything goes wrong
     */
    private static byte[] readPEM(String label, LineNumberReader lnr) throws Exception {
        String lineBegin = BEGIN + label + TRAILING;
        String lineEnd = END + label + TRAILING;

        StringBuilder builder = new StringBuilder();
        String line = lnr.readLine();
        boolean started = false;
        while (line != null) {
            if (started) {
                if (line.equals(lineEnd)) {
                    return Base64.getDecoder().decode(builder.toString());
                } else {
                    builder.append(line);
                }
            } else if (line.equals(lineBegin)) {
                started = true;
            }

            line = lnr.readLine();
        }
        throw new Exception("error readPEM");
    }

    /**
     * Write the content in PEM format. PEM defines a one-line header,
     * consisting of "-----BEGIN ", a label, and "-----", the content, and a
     * one-line footer, consisting of "-----END ", a label, and "-----". This
     * method write the content in such format.
     * 
     * @param fnamePEM the name of the PEM file
     * @param label the label
     * @param content the content
     */
    private static void writePEM(String fnamePEM, String label, byte[] content)
            throws UnsupportedEncodingException, IOException, FileNotFoundException {
        String lineBegin = BEGIN + label + TRAILING + System.lineSeparator();
        String lineEnd = System.lineSeparator() + END + label + TRAILING;
        Base64.Encoder encoder = Base64.getMimeEncoder(64, getBytesForPEM(System.lineSeparator()));
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fnamePEM))) {
            bos.write(getBytesForPEM(lineBegin));
            bos.write(encoder.encode(content));
            bos.write(getBytesForPEM(lineEnd));
        }
    }

    /**
     * Get the properly encoded bytes of the string for writing in PEM file
     * 
     * @param str the string
     * @return the encoded bytes
     */
    private static byte[] getBytesForPEM(String str) throws UnsupportedEncodingException {
        return str.getBytes(PEM_FILE_ENCODING);
    }
}