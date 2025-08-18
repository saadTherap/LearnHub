//package net.therap.learningProcessor.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import feign.Response;
//import feign.codec.ErrorDecoder;
//import lombok.extern.slf4j.Slf4j;
//import net.therap.learningProcessor.exception.RemoteFileServiceException;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Map;
//
///**
// * @author avidewan
// * @since 8/5/25
// */
//@Slf4j
//public class FeignCustomErrorDecoder implements ErrorDecoder {
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Override
//    public Exception decode(String methodKey, Response response) {
//        try (InputStream bodyStream = response.body() != null ? response.body().asInputStream() : null) {
//
//            String defaultMessage = "Unexpected error from remote file server";
//
//            if (bodyStream != null) {
//                Map<String, Object> body = objectMapper.readValue(bodyStream, Map.class);
//
//                String message = body.getOrDefault("message", defaultMessage).toString();
//                String error = body.getOrDefault("error", "").toString();
//
//                log.error("Feign error from {}: status={}, error={}, message={}",
//                        methodKey, response.status(), error, message);
//
//                return new RemoteFileServiceException(response.status(),
//                        error,
//                        message);
//            }
//
//            return new RemoteFileServiceException(response.status(),
//                    "Unknown error",
//                    defaultMessage);
//
//        } catch (IOException e) {
//            log.error("Failed to read error response", e);
//
//            return new RemoteFileServiceException(response.status(),
//                    "Parse error",
//                    e.getMessage());
//        }
//    }
//}