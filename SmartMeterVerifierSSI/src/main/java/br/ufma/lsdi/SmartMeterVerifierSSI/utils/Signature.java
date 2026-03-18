package br.ufma.lsdi.SmartMeterVerifierSSI.utils;

import br.ufma.lsdi.SmartMeterVerifierSSI.services.AuthenticationService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
@Component
public class Signature {
    private static final Logger log = LoggerFactory.getLogger(Signature.class);
    @Value("${private.key.path}")
    private String privateKeyPath;

    public String getSignature() {
        try {
            PrivateKey privateKey = loadPrivateKey(privateKeyPath);

            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                    .keyID("")
                    .build();

            Payload payload = new Payload("");

            JWSObject jwsObject = new JWSObject(header, payload);

            JWSSigner signer = new ECDSASigner((ECPrivateKey) privateKey);
            jwsObject.sign(signer);

            return jwsObject.serialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static PrivateKey loadPrivateKey(String resourcePath) throws Exception {
        try (InputStream is = Files.newInputStream(Paths.get(resourcePath))) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }

            String pem = new String(is.readAllBytes());

            pem = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] keyBytes = Base64.getDecoder().decode(pem);

            // 5. Convert from PKCS#8 to PrivateKey
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("EC");
            return kf.generatePrivate(keySpec);
        }
    }

}
