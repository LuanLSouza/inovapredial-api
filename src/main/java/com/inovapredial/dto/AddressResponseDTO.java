package com.inovapredial.dto;

import java.util.UUID;

public record AddressResponseDTO(

        UUID id,
        String street,
        Integer number,
        String  district,
        String city,
        String state,
        String zipCode
) {
}
