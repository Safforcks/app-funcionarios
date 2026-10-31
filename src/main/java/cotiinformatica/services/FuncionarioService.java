package cotiinformatica.services;

import cotiinformatica.entities.Funcionario;
import cotiinformatica.repositories.FuncionarioRepository;

import java.sql.SQLException;
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

    public void consultarFuncionario() {

        System.out.println("\nCONSULTA DE FUNCIONÁRIO:\n");

        var funcionarioRepository = new FuncionarioRepository();
        var lista = funcionarioRepository.consultar();

        if (lista.isEmpty()) {
            System.out.println("NENHUM FUNCIONÁRIO ENCONTRADO.");
            return;
        }

        for (var funcionario : lista) {
            System.out.println("ID.............: " + funcionario.getId());
            System.out.println("NOME...........: " + funcionario.getNome());
            System.out.println("CPF............: " + funcionario.getCpf());
            System.out.println("MATRÍCULA......: " + funcionario.getMatricula());
            System.out.println("SALÁRIO........: " + funcionario.getSalario());
            System.out.println("CARGO..........: " + funcionario.getCargo());
            System.out.println("...");
        }
    }
}
