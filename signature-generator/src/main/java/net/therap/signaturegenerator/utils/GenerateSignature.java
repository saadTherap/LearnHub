package net.therap.signaturegenerator.utils;

import net.therap.signaturegenerator.utils.utils.HmacUtils;

/**
 * @author tanvirhassan
 * @since 21/8/25
 */
public class GenerateSignature {

    private static final String DOWNLOAD_PATH_PREFIX = "/api/secure-file-server/files/download";

    public static String generateSignature(
            String formId,
            String uploaderEmail,
            String contentType,
            String originalFileName,
            String secret) {

        String payload = buildPayload(formId, uploaderEmail, contentType, originalFileName);

        return HmacUtils.hmacSHA256(secret, payload);
    }

    public static boolean verifySignature(String  fileSecret, String signature) {
        return fileSecret.equals(signature);
    }

    private static String buildPayload(String formId, String uploaderEmail, String contentType, String originalFileName) {
        String downloadUrl = DOWNLOAD_PATH_PREFIX +"?formId="+ formId;

        return uploaderEmail + "|" +
                contentType + "|" +
                originalFileName + "|" +
                downloadUrl;
    }
}
