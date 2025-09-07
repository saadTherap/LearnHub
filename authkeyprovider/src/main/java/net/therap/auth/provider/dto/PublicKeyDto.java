package net.therap.auth.provider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author apurboturjo
 * @since 9/7/25
 */
@Data
@AllArgsConstructor
public class PublicKeyDto {

    private String kid;
    private String publicKey;
}