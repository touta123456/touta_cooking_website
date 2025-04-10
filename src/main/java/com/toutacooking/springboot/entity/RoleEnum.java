package com.toutacooking.springboot.entity;

import java.util.Arrays;

public enum RoleEnum {
    ADMIN(1L, "ADMIN"),
    CHEF(2L, "CHEF"),
    USER(3L, "USER");

    private Long id;
    private String libelle;

    RoleEnum(Long id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

    public Long getId() {
        return id;
    }

    public String getLibelle() {
        return libelle;
    }

    public static RoleEnum fromId(Long id) {
        return Arrays.stream(values())
            .filter(role -> role.id.equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("ID de rôle invalide: " + id));
    }
}
