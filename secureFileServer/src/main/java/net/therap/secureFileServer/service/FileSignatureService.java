package net.therap.secureFileServer.service;

import net.therap.secureFileServer.entity.primary.StoredFile;
import net.therap.secureFileServer.util.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileSignatureService {

    @Value("${file.hmac.secret}")
    private String hmacSecret;

    private static final String DOWNLOAD_PATH_PREFIX = "/api/secure-file-server/files/download";

    public String generateSignature(StoredFile file) {
        String payload = buildPayload(file);

        return HmacUtils.hmacSHA256(hmacSecret, payload);
    }

    public boolean verifySignature(StoredFile file, String signature) {

        return file.getFileSecret().equals(signature);
    }

    private String buildPayload(StoredFile file) {

        String downloadUrl = DOWNLOAD_PATH_PREFIX +"?formId="+ file.getFormId();

        return file.getUploaderEmail() + "|" +
                file.getContentType() + "|" +
                file.getOriginalFilename() + "|" +
                downloadUrl;
    }
}