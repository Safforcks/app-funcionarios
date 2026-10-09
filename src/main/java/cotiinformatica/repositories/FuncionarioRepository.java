package cotiinformatica.repositories;

import cotiinformatica.entities.Funcionario;

import java.sql.DriverManager;

public class FuncionarioRepository {

    private String host = "jdbc:postgresql://localhost:5434/bd-funcionarios";
    private String user = "coti";
    private String pass = "coti";

   /*
   Método para receber um funcionário e salvá-lo
   na tabela do banco de dados (INSERT)
    */

    public void inserir(Funcionario funcionario) {
        try {
            //Abrindo conexão com o banco de dados
            var connection = DriverManager.getConnection(host, user, pass);

            //Escrevendo o comando SQL que iremos executar no banco de dados
            var statement = connection.prepareStatement("insert into funcionarios(id, nome, cpf, matricula, salario, cargo) values (?,?,?,?,?,?)");

            //Preenchendo os campos da tabela
            statement.setObject(1, funcionario.getId());
            statement.setString(2, funcionario.getNome());
            statement.setString(3, funcionario.getCpf());
            statement.setString(4, funcionario.getMatricula());
            statement.setDouble(5, funcionario.getSalario());
            statement.setString(6, funcionario.getCargo());

            //Executando e fechando a conexão
            statement.execute();
            connection.close();

            System.out.println("\nFUNCIONÁRIO CADASTRADO COM SUCESSO!");

        } catch (Exception e) {
            System.out.println("\nFALHA AO GRAVAR NO BANCO DE DADOS:");
            System.out.println("ERRO: " + e.getMessage());
        }
    }
}
