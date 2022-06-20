/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.test.standalone;

import java.io.File;
import java.security.interfaces.RSAPrivateKey;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class TokenGenerator {

    private static final String OPT_KEYSTORE = "--keystore=";

    private static final String OPT_SUBJECT = "--subject=";

    private static final String OPT_ROLES = "--roles=";

    private static final String OPT_GROUPS = "--groups=";

    private static final String OPT_EXPIRY_PERIOD_SEC = "--expiry-period-sec=";

    private static final String DEFAULT_SUBJECT = "Unknown";

    private static final int DEFAULT_EXPIRY_PERIOD_SEC = 60 * 60; // 1 hour
    
    private final static String INSIGHTS_BASE_DIR = TokenGenerator.class.getProtectionDomain().getCodeSource()
            .getLocation().getPath().split("subprojects", 2)[0];    

    // gradlew genToken
    public static void main(String[] args) {
        genToken(args);
        //readToken("<token>");
    }
    
    public static void genToken(String[] args) {
        try {
            // read configuration
            File keystoreFile = new File(INSIGHTS_BASE_DIR, "config/liberty/config/tokenservice.keystore");
            System.out.println("keystoreFile=" + keystoreFile.getAbsolutePath());
            String subject = DEFAULT_SUBJECT;
            String[] roles = new String[] {};
            String[] groups = new String[] {};
            int expiryPeriodSec = DEFAULT_EXPIRY_PERIOD_SEC;
            for (int i = 0; i < args.length; ++i) {
                if (args[i].startsWith(OPT_KEYSTORE)) {
                    keystoreFile = new File(args[i].substring(OPT_KEYSTORE.length()));
                } else if (args[i].startsWith(OPT_SUBJECT)) {
                    subject = args[i].substring(OPT_SUBJECT.length());
                } else if (args[i].startsWith(OPT_ROLES)) {
                    roles = args[i].substring(OPT_ROLES.length()).split(",");
                } else if (args[i].startsWith(OPT_GROUPS)) {
                    groups = args[i].substring(OPT_GROUPS.length()).split(",");
                } else if (args[i].startsWith(OPT_EXPIRY_PERIOD_SEC)) {
                    String str = args[i].substring(OPT_EXPIRY_PERIOD_SEC.length());
                    try {
                        expiryPeriodSec = Integer.parseInt(str);
                    } catch (NumberFormatException e) {
                        String message =
                            "Failed to parse expiry period argument, falling back to the default value of %d";
                        System.err.println(String.format(message, expiryPeriodSec));
                    }
                }
            }

            // generate token
//            JWTSigningKeyManager keyManager = new JWTSigningKeyManager(keystoreFile.toURI().toURL());
//            RSAPrivateKey signingKey = keyManager.getSigningKey();
//            Algorithm algorithm = Algorithm.RSA256(null, signingKey);
//            Date issuedAt = new Date();
//            Date expiresAt = new Date(issuedAt.getTime() + expiryPeriodSec * 1000);
            //TODO
            String accessToken = null;
//            		JWT
//                .create()
//                .withJWTId(UUID.randomUUID().toString())
//                .withIssuer(JWTConsts.ISSUER_IIS)
//                .withSubject(subject)
//                .withArrayClaim(JWTConsts.USER_ROLES, roles)
//                .withArrayClaim(JWTConsts.USER_GROUPS, groups)
//                .withExpiresAt(expiresAt)
//                .withIssuedAt(issuedAt)
//                .sign(algorithm);
            System.out.println(accessToken);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }

}
