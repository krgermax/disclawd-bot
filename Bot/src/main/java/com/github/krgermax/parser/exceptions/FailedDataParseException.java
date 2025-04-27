package com.github.krgermax.parser.exceptions;

/**
 * Custom exception that can be thrown by the Item parser
 */
public class FailedDataParseException extends Exception{
    public FailedDataParseException(String errorMsg) {
        super(errorMsg);
    }
}
