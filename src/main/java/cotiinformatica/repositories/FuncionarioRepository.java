package cotiinformatica.repositories;

import cotiinformatica.entities.Funcionario;
import cotiinformatica.factories.ConnectionFactory;

public class FuncionarioRepository {

    public void inserir(Funcionario funcionario) {

        var factory = new ConnectionFactory();

        try (var connection = factory.createConnection()){

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
