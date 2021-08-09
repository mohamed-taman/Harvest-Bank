package org.siriusxi.blueharvest.bank.cs.infra.filter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

/**
 * Record <code>HttpErrorInfo</code> which encapsulate all HTTP errors sent to client.
 *
 * @implNote Since it is a record and not normal POJO, so it needs some customizations
 * to be serialized to JSON and this is done with method
 * <code>GlobalConfiguration.jacksonCustomizer()</code>.
 * @author Mohamed Taman
 * @version 0.5
 * @see java.lang.Record
 * @since Harvest beta v0.1
 */
public record HttpErrorInfo(
@JsonProperty("status") HttpStatus httpStatus,
@JsonProperty("message") String message,
@JsonProperty("timestamp")
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
@JsonSerialize(using = ZonedDateTimeSerializer.class)
        ZonedDateTime timestamp){

    /**
     * Instantiates a new Http error info.
     *
     * @param httpStatus the http status code and type.
     * @param message the error message.
     */
    public HttpErrorInfo(HttpStatus httpStatus,String message){
               this(httpStatus,message,ZonedDateTime.now());
    }
}
