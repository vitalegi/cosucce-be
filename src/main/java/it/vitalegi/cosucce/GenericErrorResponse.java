package it.vitalegi.cosucce;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GenericErrorResponse {
    Instant timestamp;
    String error;
    String message;
}
