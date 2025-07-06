package com.weburg.ghowst;

public class HttpWebServiceException extends RuntimeException {
    // Concrete for now, to differentiate later, make this class abstract and
    // create concrete subclasses. Also not checked, for now.
    // TODO this is probably best made a checked exception, many things can
    // go wrong with web services.

    private final int httpStatus;

    public HttpWebServiceException(int httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
