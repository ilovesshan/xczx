package com.xczx.base.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/23
 * @description:
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class XczxException extends RuntimeException {
    private String errMessage;
}
