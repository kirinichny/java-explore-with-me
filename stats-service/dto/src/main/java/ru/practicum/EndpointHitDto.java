package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class EndpointHitDto {
    @NotBlank(message = "Идентификатор сервиса не должен быть пустым или содержать только пробельные символы")
    private String app;

    @NotBlank(message = "URI не должен быть пустым или содержать только пробельные символы")
    private String uri;

    @NotBlank(message = "IP-адрес не должен быть пустым или содержать только пробельные символы")
    @Size(message = "Не верная длина IP-адреса", max = 15)
    private String ip;

    @NotNull(message = "Время, когда был совершен запрос не должно быть null")
    private String timestamp;
}