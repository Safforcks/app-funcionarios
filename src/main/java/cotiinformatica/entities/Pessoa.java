package cotiinformatica.entities;

import lombok.Data;

import java.util.UUID;

@Data
public class Pessoa {

    private UUID id;
    private String nome;
    private String cpf;
}
