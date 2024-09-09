package com.vileladev.testbackend.enums;

public enum RoleEnum {
    ADMIN("ADMIN"),
    USER("USER");

    private String nome;

    RoleEnum(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

}
