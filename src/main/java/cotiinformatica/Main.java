package cotiinformatica;

import cotiinformatica.services.FuncionarioService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var funcionarioService = new FuncionarioService();
        var scanner = new Scanner(System.in);

        System.out.println("\nMENU DE OPÇÕES:\n");

        System.out.println("\t(1) CADASTRAR UM FUNCIONÁRIO");
        System.out.println("\t(2) CONSULTAR TODOS OS FUNCIONÁRIOS");

        System.out.print("\nINFORME A OPÇÃO DESEJADA: ");

        var opcao = Integer.parseInt(scanner.nextLine());

        switch (opcao) {
            case 1:
                funcionarioService.cadastrarFuncionario();
                break;
            default:
                System.out.println("\nOPÇÃO INVÁLIDA!");
                break;
        }
    }
}