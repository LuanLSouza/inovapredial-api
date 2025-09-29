package com.inovapredial.dto.requests;

import jakarta.validation.constraints.NotBlank;

public record AddressRequestDTO(
        @NotBlank(message = "Campo obrigatório") String street,
        @NotBlank(message = "Campo obrigatório") Integer number,
        String  district,
        String city,
        String state,
        String zipCode
) {
}
