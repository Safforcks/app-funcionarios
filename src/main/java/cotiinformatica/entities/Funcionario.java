package cotiinformatica.entities;

import lombok.Data;

@Data
public class Funcionario extends Pessoa {

    private String matricula;
    private double salario;
    private String cargo;
}
