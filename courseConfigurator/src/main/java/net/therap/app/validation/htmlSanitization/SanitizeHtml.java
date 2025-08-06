package net.therap.app.validation.htmlSanitization;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * @author gazizafor
 * @since 3/8/25
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@JacksonAnnotationsInside
@JsonDeserialize(using = HtmlSanitizerDeserializer.class)
public @interface SanitizeHtml {

}
