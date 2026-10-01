package se.lexicon.ecommercejpaworkshop.dto;

public record CustomerResponse(
        Long id,
        String fullName,
        String email,
        AddressResponse addressResponse
) {}
