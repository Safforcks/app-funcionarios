package cotiinformatica.services;

import cotiinformatica.entities.Funcionario;
import cotiinformatica.repositories.FuncionarioRepository;

import java.util.Scanner;
import java.util.UUID;

public class FuncionarioService {

    // Método para realizar o cadastro de um funcionário

    public void cadastrarFuncionario() {
        System.out.println("\nCADASTRO DE FUNCIONÁRIO:\n");

        var funcionario = new Funcionario();
        funcionario.setId(UUID.randomUUID());

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

        //Cadastrar o funcionário em um banco de dados
        var funcionarioRepository = new FuncionarioRepository();
        funcionarioRepository.inserir(funcionario);
    }
}
