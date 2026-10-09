package cotiinformatica.services;

import cotiinformatica.entities.Funcionario;

import java.util.Scanner;

public class FuncionarioService {
    /*
    Método para realizar o cadastro de um funcionário
     */
    public void cadastrarFuncionario() {
        System.out.println("\nCADASTRO DE FUNCIONÁRIO:\n");

        var funcionario = new Funcionario();
        var scanner = new Scanner(System.in);

        System.out.print("INFORME O NOME.......: ");
        funcionario.setNome(scanner.nextLine());

        System.out.print("INFORME O CPF........: ");
        funcionario.setCpf(scanner.nextLine());

        System.out.print("INFORME A MATRÍCULA..: ");
        funcionario.setMatricula(scanner.nextLine());

        System.out.print("INFORME O SALÁRIO....: ");
        funcionario.setSalario(Double.parseDouble(scanner.nextLine()));

        System.out.print("INFORME O CARGO......: ");
        funcionario.setCargo(scanner.nextLine());

        System.out.println();
    }
}
