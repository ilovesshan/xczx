package com.xczx.base.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

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
public class RestExceptionResponse {
    private HttpStatus code = HttpStatus.INTERNAL_SERVER_ERROR;
    private String requestId;
    private String requestTime;
    private String errMessage;
}
