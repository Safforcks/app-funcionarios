package cotiinformatica.repositories;

import cotiinformatica.entities.Funcionario;
import cotiinformatica.factories.ConnectionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FuncionarioRepository {

    public void inserir(Funcionario funcionario) {

        var factory = new ConnectionFactory();

        try (var connection = factory.createConnection()) {

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

    public List<Funcionario> consultar() {

        var funcionarios = new ArrayList<Funcionario>();
        var factory = new ConnectionFactory();

        try (var connection = factory.createConnection()) {

            var sql = """
                    select id, nome, cpf, matricula, salario, cargo
                    from funcionarios
                    order by datahoracadastro desc, id
                    """;

            var statement = connection.prepareStatement(sql);
            var result = statement.executeQuery();

            while (result.next()) {

                var funcionario = new Funcionario();

                funcionario.setId(result.getObject("id", UUID.class));
                funcionario.setNome(result.getString("nome"));
                funcionario.setCpf(result.getString("cpf"));
                funcionario.setMatricula(result.getString("matricula"));
                funcionario.setSalario(result.getDouble("salario"));
                funcionario.setCargo(result.getString("cargo"));

                funcionarios.add(funcionario);
            }

        } catch (Exception e) {
            System.out.println("\nFALHA AO CONSULTAR FUNCIONÁRIOS:");
            System.out.println("ERRO: " + e.getMessage());
        }

        return funcionarios;
    }
}
