package com.toutacooking.springboot.dto;

import jakarta.validation.constraints.NotEmpty;


public class RoleDTO {

    private Long id;

    @NotEmpty
    private String libelle;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

}
