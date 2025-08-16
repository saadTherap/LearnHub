package net.therap.secureFileServer.service;

import net.therap.secureFileServer.entity.primary.StoredFile;
import net.therap.secureFileServer.util.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileSignatureService {

    @Value("${file.hmac.secret}")
    private String hmacSecret;

    public String generateSignature(StoredFile file) {
        String payload = buildPayload(file);

        return HmacUtils.hmacSHA256(hmacSecret, payload);
    }

    public boolean verifySignature(StoredFile file, String signature) {
        String expected = buildPayload(file);
        String generated = HmacUtils.hmacSHA256(hmacSecret, expected);

        return generated.equals(signature);
    }

    private String buildPayload(StoredFile file) {

        return file.getId() + "|" +
                file.getUploaderId() + "|" +
                file.getUploaderRole() + "|" +
                file.getContextId();
    }
}