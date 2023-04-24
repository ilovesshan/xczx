package com.xczx.base.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/23
 * @description:
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class XczxException extends RuntimeException {
    private String errMessage;

    public XczxException(String message) {
        super(message);
        this.errMessage = message;
    }
}
