package org.example.finostra.Exceptions;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ApiError {
    private Integer statusCode;
    private String message;
    private Date timestamp;
}
