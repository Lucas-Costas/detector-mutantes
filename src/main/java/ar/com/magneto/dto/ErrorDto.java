package ar.com.magneto.dto;

import lombok.Data;

@Data
public class ErrorDto {

    private String message;
    private String cause;

    public ErrorDto(String error, String causa) {
        this.message = error;
        this.cause = causa;
    }

    public ErrorDto(Exception ex) {
        this.message = ex.getMessage();
        this.cause = ex.getCause().getMessage();
    }
}
