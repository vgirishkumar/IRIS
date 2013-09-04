package com.temenos.interaction.core.command;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

/**
 * Define the HTTP status types not defined by jax-rs.
 * @author aphethean
 */
public enum HttpStatusTypes implements StatusType {

    /**
     * 205 Reset content
     */
	RESET_CONTENT(205, "Reset Content"),

    /**
     * 405 Method not allowed
     */
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),

    /**
     * 504 Gateway timeout
     */
    GATEWAY_TIMEOUT(504, "Gateway Timeout");
    
    private final int code;
    private final String reason;
    private Status.Family family;

    HttpStatusTypes(final int statusCode, final String reasonPhrase) {
        this.code = statusCode;
        this.reason = reasonPhrase;
        switch(code/100) {
            case 1: this.family = Status.Family.INFORMATIONAL; break;
            case 2: this.family = Status.Family.SUCCESSFUL; break;
            case 3: this.family = Status.Family.REDIRECTION; break;
            case 4: this.family = Status.Family.CLIENT_ERROR; break;
            case 5: this.family = Status.Family.SERVER_ERROR; break;
            default: this.family = Status.Family.OTHER; break;
        }
    }
    
    /**
     * Get the class of status code
     * @return the class of status code
     */
    public Family getFamily() {
        return family;
    }
    
    /**
     * Get the associated status code
     * @return the status code
     */
    public int getStatusCode() {
        return code;
    }
    
    /**
     * Get the reason phrase
     * @return the reason phrase
     */
    public String getReasonPhrase() {
        return toString();
    }
    
    /**
     * Get the reason phrase
     * @return the reason phrase
     */
    @Override
    public String toString() {
        return reason;
    }
    
    /**
     * Convert a numerical status code into the corresponding Status
     * @param statusCode the numerical status code
     * @return the matching Status or null is no matching Status is defined
     */
    public static StatusType fromStatusCode(final int statusCode) {
        for (HttpStatusTypes s : HttpStatusTypes.values()) {
            if (s.code == statusCode) {
                return s;
            }
        }
        return Status.fromStatusCode(statusCode);
    }
}
