package com.checkmarx.integrations.datastore.controllers.exceptions;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class GeneralExceptionDO {

    private String message;
    private LocalDateTime localDateTime;
}