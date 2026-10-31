# app-funcionarios — Cadastro e consulta de funcionários em Java

Aplicação de console para cadastrar e consultar funcionários, com persistência em PostgreSQL por meio de JDBC. O projeto trabalha conceitos de orientação a objetos, herança, encapsulamento, Lombok, organização em camadas e execução de SQL com parâmetros.

O usuário informa nome, CPF, matrícula, salário e cargo no terminal. A aplicação gera um identificador UUID e grava o registro na tabela `funcionarios`.

**Estado atual:** cadastro e consulta estão implementados no menu. A opção `1` grava um funcionário e a opção `2` lista os registros, ordenados por data de cadastro decrescente e, em caso de empate, por ID. Atualização e exclusão ainda não estão disponíveis.

## Sumário

- [Funcionalidades e fluxo de uso](#funcionalidades-e-fluxo-de-uso)
- [Tecnologias](#tecnologias)
- [Estrutura e responsabilidades](#estrutura-e-responsabilidades)
- [Orientação a objetos](#orientação-a-objetos)
- [Regras e comportamento atual](#regras-e-comportamento-atual)
- [Banco de dados](#banco-de-dados)
- [Como executar](#como-executar)
- [Exemplo de cadastro e conferência](#exemplo-de-cadastro-e-conferência)
- [Ambiente Docker](#ambiente-docker)
- [Problemas comuns](#problemas-comuns)
- [Limitações e possibilidades de evolução](#limitações-e-possibilidades-de-evolução)
- [Imagem do projeto](#imagem-do-projeto)
- [Fontes e referências](#fontes-e-referências)

## Funcionalidades e fluxo de uso

| Opção do menu | Função apresentada | Situação atual |
| --- | --- | --- |
| `1` | Cadastrar um funcionário | Solicita os cinco dados do funcionário, gera o UUID e tenta inserir o registro no banco. |
| `2` | Consultar todos os funcionários | Basta escolher a opção; não é necessário informar outros dados. Lista ID, nome, CPF, matrícula, salário e cargo. |

A aplicação executa uma única escolha e encerra. Para cadastrar outro funcionário ou consultar os dados após um cadastro, é necessário iniciar o programa novamente.

O cadastro segue esta sequência:

1. `Main` lê a opção do menu.
2. `FuncionarioService` instancia um `Funcionario` e gera seu ID com `UUID.randomUUID()`.
3. O serviço solicita nome, CPF, matrícula, salário e cargo.
4. `FuncionarioRepository` abre uma conexão com o PostgreSQL e executa um `INSERT`.
5. Em caso de sucesso, o repositório imprime `FUNCIONÁRIO CADASTRADO COM SUCESSO!`. Em caso de falha no acesso ou na gravação, imprime a mensagem de erro.

A consulta segue esta sequência:

1. `Main` direciona a opção `2` para `FuncionarioService.consultarFuncionario()`.
2. O serviço chama `FuncionarioRepository.consultar()`.
3. O repositório abre uma conexão por `ConnectionFactory`, executa o `SELECT` e transforma cada linha em um objeto `Funcionario`.
4. O serviço exibe os dados de cada funcionário. Se a lista retornada estiver vazia, imprime `NENHUM FUNCIONÁRIO ENCONTRADO.`.

## Tecnologias

As versões abaixo são as declaradas nos arquivos do repositório.

| Tecnologia | Versão/configuração | Finalidade |
| --- | --- | --- |
| Java | Source e target `21` | Linguagem e execução da aplicação. |
| Maven | `cotiinformatica:app-funcionarios:1.0-SNAPSHOT` | Compilação e resolução de dependências. |
| JDBC | API `java.sql` | Acesso ao banco e execução de comandos SQL. |
| Lombok | `org.projectlombok:lombok:1.18.48`, escopo `provided` | Geração de métodos nas entidades com `@Data`. |
| PostgreSQL JDBC Driver | `org.postgresql:postgresql:42.7.13` | Driver para comunicação entre Java e PostgreSQL. |
| PostgreSQL | Imagem `postgres:16` | Armazenamento dos funcionários. |
| pgAdmin | Imagem `dpage/pgadmin4:latest` | Administração do PostgreSQL pelo navegador. |
| Docker Compose | Projeto `app-funcionarios` | Inicialização dos serviços e configuração dos volumes. |

O ponto de entrada é `cotiinformatica.Main`. A interface de cadastro é o terminal; o pgAdmin administra o banco de dados. A aplicação não inclui interface web própria, API HTTP ou ORM.

## Estrutura e responsabilidades

| Arquivo | Responsabilidade |
| --- | --- |
| [pom.xml](pom.xml) | Define o projeto Maven, o nível Java, o driver JDBC e o Lombok. |
| [docker-compose.yml](docker-compose.yml) | Configura PostgreSQL, pgAdmin, portas e volumes. |
| [Main.java](src/main/java/cotiinformatica/Main.java) | Apresenta o menu e direciona as opções para cadastro ou consulta. |
| [entities/Pessoa.java](src/main/java/cotiinformatica/entities/Pessoa.java) | Define os dados comuns de uma pessoa: ID, nome e CPF. |
| [entities/Funcionario.java](src/main/java/cotiinformatica/entities/Funcionario.java) | Herda de `Pessoa` e acrescenta matrícula, salário e cargo. |
| [services/FuncionarioService.java](src/main/java/cotiinformatica/services/FuncionarioService.java) | Gera o UUID, lê os dados do cadastro e apresenta os resultados da consulta. |
| [factories/ConnectionFactory.java](src/main/java/cotiinformatica/factories/ConnectionFactory.java) | Centraliza as credenciais JDBC e abre conexões pelo método `createConnection()`. |
| [repositories/FuncionarioRepository.java](src/main/java/cotiinformatica/repositories/FuncionarioRepository.java) | Executa `INSERT` e `SELECT`, monta a lista de funcionários e apresenta erros de acesso ao banco. |
| [scripts/schema.sql](src/main/java/cotiinformatica/scripts/schema.sql) | Define a tabela `funcionarios`. |

### Organização em camadas

- **Entidades:** representam os dados do domínio.
- **Serviço:** coordena cadastro e consulta, lê os dados do console e apresenta a lista de funcionários.
- **Repositório:** realiza a inserção e a consulta no banco por JDBC.
- **Fábrica de conexões:** reúne a configuração JDBC e a abertura das conexões.
- **Classe principal:** inicia o programa e encaminha a opção escolhida.

Essa divisão facilita localizar cada responsabilidade. A separação ainda é parcial: o serviço faz interação com o usuário e o repositório imprime mensagens no terminal. As credenciais e a abertura da conexão estão centralizadas em `ConnectionFactory`, utilizada pelo repositório nas duas operações.

### Como a gravação funciona

O método `FuncionarioRepository.inserir(Funcionario)` prepara o comando:

```sql
INSERT INTO funcionarios (id, nome, cpf, matricula, salario, cargo)
VALUES (?, ?, ?, ?, ?, ?);
```

Os valores são associados aos parâmetros por `setObject`, `setString` e `setDouble`. Dessa forma, os dados informados não são concatenados ao texto do SQL.

O repositório obtém a conexão por `ConnectionFactory.createConnection()`, que utiliza `DriverManager.getConnection(...)`. Tanto a inserção quanto a consulta envolvem a conexão em `try-with-resources`, garantindo seu fechamento ao sair do bloco, inclusive em caso de exceção. Na inserção ainda existe uma chamada explícita a `connection.close()`, redundante com esse gerenciamento.

O método de inserção retorna `void` e captura as próprias exceções. O serviço não recebe um objeto de resultado nem um indicador de sucesso.

### Como a consulta funciona

O método `FuncionarioRepository.consultar()` executa:

```sql
SELECT id, nome, cpf, matricula, salario, cargo
FROM funcionarios
ORDER BY datahoracadastro DESC, id;
```

O `ResultSet` é percorrido para montar uma `List<Funcionario>`. O UUID é lido com `getObject("id", UUID.class)`; nome, CPF, matrícula e cargo são lidos como texto, e salário com `getDouble`.

A ordenação prioriza a data de cadastro decrescente e utiliza o ID como segundo critério. A coluna `datahoracadastro` participa da ordenação, mas não é selecionada nem exibida no terminal.

Se ocorrer uma exceção, o repositório imprime `FALHA AO CONSULTAR FUNCIONÁRIOS:` e retorna a lista construída até aquele momento. Isso significa que uma lista vazia também pode ser consequência de falha de consulta; nesse caso, o serviço exibe a mensagem de ausência de funcionários depois da mensagem de erro. Uma falha durante a leitura pode retornar uma lista parcial.

## Orientação a objetos

### Herança entre Pessoa e Funcionario

A declaração `public class Funcionario extends Pessoa` permite que um funcionário utilize os atributos e métodos definidos em `Pessoa`, além dos seus próprios dados.

| Classe | Dados definidos | Tipos Java |
| --- | --- | --- |
| `Pessoa` | `id`, `nome`, `cpf` | `UUID`, `String`, `String` |
| `Funcionario` | `matricula`, `salario`, `cargo` | `String`, `double`, `String` |

Por isso, o serviço pode chamar `funcionario.setNome(...)` e `funcionario.setCpf(...)`, mesmo que esses métodos estejam em `Pessoa`.

### Encapsulamento

Os atributos são privados e acessados por getters e setters gerados pelo Lombok. As duas classes utilizam `@Data`, que também gera métodos como `toString`, `equals` e `hashCode`.

Os métodos de acesso de `Pessoa` ficam disponíveis em `Funcionario` por herança. A geração desses métodos não acrescenta validação de CPF, texto vazio ou salário.

A herança existe no modelo Java. No banco, todos os campos são armazenados em uma única tabela `funcionarios`; o esquema não define uma tabela `pessoas` nem um relacionamento entre duas tabelas.

## Regras e comportamento atual

As regras abaixo descrevem o código e as restrições SQL existentes. Melhorias propostas estão identificadas ao final do README.

| Assunto | Comportamento implementado |
| --- | --- |
| ID | Gerado pela aplicação com `UUID.randomUUID()` antes da leitura dos dados. O banco exige chave primária, mas não possui geração automática de UUID no esquema. |
| Nome | Armazenado em `VARCHAR(150) NOT NULL`. Não há validação Java para nome em branco ou comprimento máximo. |
| CPF | Armazenado como texto, até 14 caracteres. Não há validação de dígitos verificadores nem aplicação automática de máscara. |
| Matrícula | Texto de até 20 caracteres. Não há geração automática nem verificação de duplicidade. |
| Salário | Convertido com `Double.parseDouble` e enviado por `setDouble`. Digite valores usuais com ponto decimal, como `3500.00`, sem símbolo de moeda. |
| Cargo | Texto de até 50 caracteres, sem lista pré-definida de cargos. |
| Campos obrigatórios | Nome, CPF, matrícula, salário e cargo têm `NOT NULL` no banco. Essa restrição não impede strings vazias. |
| Duplicidade | CPF e matrícula não têm restrição `UNIQUE`; o esquema permite repetir esses valores. |
| Valores negativos | Não há validação Java nem restrição `CHECK` para impedir salário negativo ou zero. |
| Data e hora de cadastro | Geradas pelo PostgreSQL com `DEFAULT CURRENT_TIMESTAMP`. Não há campo correspondente nas entidades atuais. |
| Erros de persistência | A inserção captura falhas e imprime `FALHA AO GRAVAR NO BANCO DE DADOS:` e `ERRO:`. A consulta imprime `FALHA AO CONSULTAR FUNCIONÁRIOS:` e `ERRO:`. |
| Consulta | Retorna todos os funcionários, sem filtros ou paginação, ordenados por `datahoracadastro DESC, id`. |
| Lista vazia | O serviço imprime `NENHUM FUNCIONÁRIO ENCONTRADO.`. Verifique se houve mensagem de erro antes dela, pois o repositório também retorna uma lista vazia quando falha antes de carregar registros. |
| Erros de entrada | Opção não numérica e salário em formato inválido não têm tratamento específico no menu ou no serviço; podem encerrar a execução com exceção. |
| Repetição | Não existe laço de repetição nem pergunta para realizar outra operação. |

As colunas com limite de tamanho são restringidas pelo banco, sem uma mensagem de validação própria da aplicação. As opções numéricas diferentes de `1` e `2` seguem o caso padrão do menu e exibem `OPÇÃO INVÁLIDA!`.

## Banco de dados

### Conexões e credenciais locais

| Acesso | Configuração |
| --- | --- |
| Nome do banco | `bd-funcionarios` |
| Usuário PostgreSQL | `coti` |
| Senha PostgreSQL | Valor de `POSTGRES_PASSWORD` no Compose, correspondente à configuração de `ConnectionFactory.java`. |
| Host/porta para Java ou ferramentas na máquina local | `localhost:5434` |
| URL JDBC | `jdbc:postgresql://localhost:5434/bd-funcionarios` |
| Host/porta para o pgAdmin dentro do Compose | `postgres:5432` |
| Endereço web do pgAdmin | [http://localhost:5051](http://localhost:5051) |
| E-mail de acesso ao pgAdmin | `coti@email.com` |
| Senha de acesso ao pgAdmin | Valor de `PGADMIN_DEFAULT_PASSWORD` no Compose. |

O login do pgAdmin é diferente do login do PostgreSQL. Primeiro, entre na interface web com o e-mail e a senha do pgAdmin; depois, registre o servidor usando o usuário e a senha do banco.

Os valores de acesso são definidos no Compose e, no caso da aplicação, diretamente em `ConnectionFactory.java`. Consulte esses arquivos no ambiente local para preencher as senhas. São configurações de desenvolvimento e devem ser adaptadas para outros ambientes.

### Estrutura da tabela funcionarios

| Coluna | Tipo PostgreSQL | Restrições/padrão | Origem no Java |
| --- | --- | --- | --- |
| `id` | `UUID` | Chave primária | `Pessoa.id` — `UUID` |
| `nome` | `VARCHAR(150)` | `NOT NULL` | `Pessoa.nome` — `String` |
| `cpf` | `VARCHAR(14)` | `NOT NULL` | `Pessoa.cpf` — `String` |
| `matricula` | `VARCHAR(20)` | `NOT NULL` | `Funcionario.matricula` — `String` |
| `salario` | `NUMERIC(10,2)` | `NOT NULL` | `Funcionario.salario` — `double` |
| `cargo` | `VARCHAR(50)` | `NOT NULL` | `Funcionario.cargo` — `String` |
| `datahoracadastro` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP`; sem `NOT NULL` explícito | Preenchida pelo banco; ausente das entidades. |

O tipo `NUMERIC(10,2)` comporta dez dígitos no total, sendo dois decimais. A coluna utiliza precisão decimal, enquanto o Java utiliza `double`, de representação binária; uma evolução possível é usar `BigDecimal` e `setBigDecimal` para tratar valores monetários de ponta a ponta.

O SQL do projeto, com terminador para execução interativa, é:

```sql
CREATE TABLE funcionarios (
    id               UUID PRIMARY KEY,
    nome             VARCHAR(150) NOT NULL,
    cpf              VARCHAR(14) NOT NULL,
    matricula        VARCHAR(20) NOT NULL,
    salario          NUMERIC(10,2) NOT NULL,
    cargo            VARCHAR(50) NOT NULL,
    datahoracadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

O esquema tem uma única tabela, sem chaves estrangeiras. O arquivo `schema.sql` não usa `IF NOT EXISTS`; tentar criar novamente uma tabela existente gera erro.

**A criação da tabela é manual.** O Compose inicializa o banco configurado em `POSTGRES_DB` em um volume novo, mas não monta nem executa `src/main/java/cotiinformatica/scripts/schema.sql`. O código Java também não executa esse script.

## Como executar

### 1. Preparar os requisitos

- JDK 21, conforme a configuração do `pom.xml`.
- Maven instalado; não há Maven Wrapper no repositório.
- Docker Engine com Docker Compose ou Docker Desktop com Compose.
- Git para clonar o projeto.
- Portas locais `5434` e `5051` disponíveis.

Confira as ferramentas:

```bash
java -version
mvn -version
docker version
docker compose version
```

Verifique se o Maven também está usando o JDK 21. A primeira execução precisa baixar o driver JDBC, o Lombok e as imagens dos serviços.

### 2. Clonar o repositório

```bash
git clone https://github.com/Safforcks/app-funcionarios.git
cd app-funcionarios
```

Execute os próximos comandos na raiz do projeto.

### 3. Iniciar o PostgreSQL e o pgAdmin

```bash
docker compose up -d
docker compose ps
docker compose logs postgres
```

Aguarde o PostgreSQL concluir a inicialização. Para verificar se está aceitando conexões:

```bash
docker compose exec postgres pg_isready -U coti -d bd-funcionarios
```

Esse comando verifica a disponibilidade do servidor; não confirma que a tabela foi criada. O Compose atual não define health checks. O `depends_on` do pgAdmin estabelece a ordem de inicialização, mas não aguarda a prontidão do banco.

### 4. Conectar o pgAdmin ao banco

1. Abra [http://localhost:5051](http://localhost:5051).
2. Entre com `coti@email.com` e a senha definida em `PGADMIN_DEFAULT_PASSWORD` no Compose.
3. Em **Register > Server**, informe um nome para identificar a conexão, por exemplo `PostgreSQL app-funcionarios`.
4. Na aba **Connection**, use:

| Campo | Valor |
| --- | --- |
| Host name/address | `postgres` |
| Port | `5432` |
| Maintenance database | `bd-funcionarios` |
| Username | `coti` |
| Password | Valor de `POSTGRES_PASSWORD` no Compose. |

O pgAdmin está em um container e acessa o serviço pelo nome `postgres`. Para uma ferramenta instalada diretamente na máquina, use `localhost` e porta `5434`.

### 5. Criar a tabela

No pgAdmin, selecione o banco `bd-funcionarios`, abra o **Query Tool** e execute o SQL de criação apresentado acima. Também é possível abrir o arquivo [schema.sql](src/main/java/cotiinformatica/scripts/schema.sql) no Query Tool e executar seu conteúdo.

Como alternativa, abra o terminal interativo do PostgreSQL:

```bash
docker compose exec postgres psql -U coti -d bd-funcionarios
```

Cole o comando completo `CREATE TABLE` desta documentação, incluindo o ponto e vírgula final. Em seguida, confira a estrutura e saia:

```text
\dt
\d funcionarios
\q
```

Os comandos iniciados por barra invertida são próprios do terminal `psql`, não do Query Tool.

### 6. Executar a aplicação Java

**Pela IDE:** abra o projeto como Maven, configure o JDK 21, aguarde o carregamento das dependências e confira o suporte ao processamento de anotações do Lombok. Execute a classe `cotiinformatica.Main`.

**Pelo terminal com Maven:**

```bash
mvn clean compile
mvn org.codehaus.mojo:exec-maven-plugin:3.5.0:java -Dexec.mainClass=cotiinformatica.Main
```

O segundo comando invoca explicitamente o Exec Maven Plugin e inclui as dependências na execução, sem exigir alteração no `pom.xml`. Use um terminal interativo para escolher a operação e responder aos campos do cadastro.

O Compose inicia somente a infraestrutura de banco e administração. A aplicação Java é iniciada separadamente na máquina local.

## Exemplo de cadastro e conferência

O menu apresenta:

```text
MENU DE OPÇÕES:

    (1) CADASTRAR UM FUNCIONÁRIO
    (2) CONSULTAR TODOS OS FUNCIONÁRIOS

INFORME A OPÇÃO DESEJADA:
```

Escolha `1` e informe, por exemplo:

| Campo | Valor de teste |
| --- | --- |
| Nome | `Funcionário Exemplo` |
| CPF | `000.000.000-00` |
| Matrícula | `TESTE-001` |
| Salário | `3500.00` |
| Cargo | `Desenvolvedor` |

Os dados são fictícios. O CPF de exemplo não é válido e serve para demonstrar que a versão atual não valida o documento.

Com banco acessível e tabela criada, a mensagem esperada é:

```text
FUNCIONÁRIO CADASTRADO COM SUCESSO!
```

Inicie o programa novamente e escolha `2`. O terminal deve listar ID, nome, CPF, matrícula, salário e cargo. O salário utiliza a representação padrão de `double`, sem formatação monetária brasileira.

Se a lista retornada estiver vazia, o serviço apresenta:

```text
NENHUM FUNCIONÁRIO ENCONTRADO.
```

Para uma conferência adicional, inclusive da data de cadastro, execute no Query Tool ou no `psql`:

```sql
SELECT id, nome, cpf, matricula, salario, cargo, datahoracadastro
FROM funcionarios
ORDER BY datahoracadastro DESC, id;
```

A consulta SQL acima é uma instrução manual de conferência e inclui `datahoracadastro`. O `SELECT` executado pelo Java utiliza a mesma ordenação, mas retorna apenas os seis campos exibidos no terminal. Cada novo cadastro gera outro UUID. Repetir CPF ou matrícula não é impedido pelo esquema atual.

## Ambiente Docker

| Recurso | Configuração |
| --- | --- |
| Serviço `postgres` | Imagem `postgres:16`, porta publicada `5434:5432`. |
| Serviço `pgadmin` | Imagem `dpage/pgadmin4:latest`, porta publicada `5051:80`. |
| Volume `postgres_data` | Dados do PostgreSQL em `/var/lib/postgresql/data`. |
| Volume `pgadmin_data` | Configurações e dados do pgAdmin em `/var/lib/pgadmin`. |
| Rede | Rede padrão criada pelo Compose, compartilhada pelos serviços. |
| Dependência | O pgAdmin declara `depends_on: postgres`. |

Os nomes dos volumes acima são as chaves do arquivo; os recursos criados pelo Compose podem receber o prefixo do projeto. O arquivo não configura nomes fixos de containers, rede personalizada, health checks ou política de reinício.

A tag `latest` do pgAdmin não fixa uma versão específica. A imagem utilizada pode mudar em novos downloads dessa tag.

Comandos úteis:

```bash
# Consultar o estado dos serviços
docker compose ps

# Acompanhar logs do banco
docker compose logs -f postgres

# Encerrar o ambiente e remover containers, preservando volumes
docker compose down

# Iniciar novamente
docker compose up -d
```

**Atenção:** `docker compose down -v` também remove os volumes e apaga os dados persistidos, incluindo os funcionários e as configurações do pgAdmin. Use apenas quando quiser descartar o ambiente local.

Alterações em `POSTGRES_DB`, `POSTGRES_USER` e `POSTGRES_PASSWORD` não recriam automaticamente um banco já inicializado em volume existente.

## Problemas comuns

| Problema | Verificação sugerida |
| --- | --- |
| Conexão recusada em `localhost:5434` | Confira os containers e logs, aguarde o banco iniciar e execute `pg_isready`. |
| pgAdmin não se conecta ao servidor | Na conexão cadastrada dentro do pgAdmin, utilize `postgres:5432`; `localhost` apontaria para o próprio container do pgAdmin. |
| Login do pgAdmin falha | Use o e-mail configurado para o pgAdmin e a senha de `PGADMIN_DEFAULT_PASSWORD`; esse login é diferente do acesso ao PostgreSQL. |
| Erro de autenticação PostgreSQL | Confira `POSTGRES_USER`, `POSTGRES_PASSWORD`, a configuração de `ConnectionFactory.java` e se o volume foi inicializado anteriormente com outras credenciais. |
| Banco `bd-funcionarios` inexistente | Verifique a instância acessada e a inicialização do volume; as variáveis do Compose não alteram automaticamente um banco existente. |
| `relation "funcionarios" does not exist` | Execute `schema.sql` no banco `bd-funcionarios` e confira a tabela. |
| Tabela já existe ao criar | Confira a estrutura; o script não é idempotente. Não é necessário recriá-la a cada execução da aplicação. |
| `NumberFormatException` no salário | Informe um número como `3500.00`, sem vírgula, separador de milhar ou `R$`. |
| `NumberFormatException` no menu | Digite `1` para cadastrar ou `2` para consultar; a entrada deve ser numérica. |
| A consulta informa que não há funcionários | Confira se a tabela está vazia e se houve `FALHA AO CONSULTAR FUNCIONÁRIOS:` antes da mensagem. Uma falha também pode produzir lista vazia. |
| A consulta retorna apenas parte dos registros | Verifique a mensagem de erro: uma falha durante a leitura retorna a lista construída até aquele momento. |
| Getters e setters não encontrados | Verifique a resolução do Lombok e o processamento de anotações na IDE/compilação. |
| Texto maior que o tamanho da coluna | Respeite os limites do esquema; o Java ainda não valida os comprimentos. |
| `numeric field overflow` | Confira se o salário cabe na precisão `NUMERIC(10,2)`. |
| Porta ocupada | Libere `5434` ou `5051`. Ao alterar a porta publicada do PostgreSQL, ajuste a URL JDBC em `ConnectionFactory.java`. |
| `invalid target release: 21` | Configure o JDK 21 na IDE e no Maven. |
| Maven não resolve o driver | Verifique acesso ao repositório de dependências e disponibilidade da versão `42.7.13` declarada no `pom.xml`. |
| `No suitable driver` | Execute com o driver PostgreSQL no classpath, pela IDE com Maven ou pelo comando indicado. |

## Limitações e possibilidades de evolução

As funcionalidades abaixo ainda não estão implementadas e podem orientar a continuidade do projeto:

- Completar o CRUD com atualização e exclusão e adicionar busca por ID ou filtros.
- Adicionar um laço de navegação para realizar várias operações na mesma execução.
- Validar campos em branco, limites de tamanho, CPF e entrada numérica.
- Definir regras de negócio para salário e unicidade de CPF e matrícula, refletindo-as também no banco.
- Usar `BigDecimal` para o salário e representar a data de cadastro no modelo Java.
- Separar a interação com o console das regras de negócio e das mensagens de persistência.
- Externalizar as credenciais e demais configurações atualmente centralizadas em `ConnectionFactory`.
- Estender o uso explícito de `try-with-resources` aos statements e resultados e remover o fechamento redundante da conexão na inserção.
- Diferenciar falha de consulta de ausência de registros e evitar tratar listas parciais como consultas concluídas.
- Retornar resultados ou propagar falhas ao serviço, em vez de concentrar mensagens no repositório.
- Automatizar a criação e evolução do esquema e configurar health checks no Compose.
- Fixar uma versão do pgAdmin e configurar os plugins Maven para facilitar a reprodução do ambiente.
- Adicionar testes automatizados e integração contínua.

O repositório ainda não contém testes automatizados. O exemplo de cadastro, a opção de consulta do menu e a conferência SQL desta documentação fornecem um roteiro de verificação manual.

## Imagem do projeto

Imagem preservada da documentação original:

<img width="592" height="320" alt="Captura de tela do projeto app-funcionarios" src="https://github.com/user-attachments/assets/c9db7847-f17a-46de-8c34-b9d954b6bfc8" />

## Fontes e referências

As funcionalidades, restrições e configurações descritas têm como fonte os arquivos do próprio repositório:

- [Main.java — menu e opções implementadas](src/main/java/cotiinformatica/Main.java)
- [Pessoa.java e seus atributos](src/main/java/cotiinformatica/entities/Pessoa.java)
- [Funcionario.java — herança e dados do funcionário](src/main/java/cotiinformatica/entities/Funcionario.java)
- [FuncionarioService.java — cadastro e apresentação da consulta](src/main/java/cotiinformatica/services/FuncionarioService.java)
- [ConnectionFactory.java — configuração e abertura das conexões JDBC](src/main/java/cotiinformatica/factories/ConnectionFactory.java)
- [FuncionarioRepository.java — JDBC, SQL e tratamento de falhas](src/main/java/cotiinformatica/repositories/FuncionarioRepository.java)
- [schema.sql — tipos e restrições da tabela](src/main/java/cotiinformatica/scripts/schema.sql)
- [pom.xml — Java e dependências](pom.xml)
- [docker-compose.yml — serviços e infraestrutura](docker-compose.yml)

Documentação oficial para aprofundamento:

- [Java 21 — API JDBC](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/package-summary.html)
- [Java 21 — UUID](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/UUID.html)
- [Lombok — @Data](https://projectlombok.org/features/Data)
- [Apache Maven — Introdução](https://maven.apache.org/guides/getting-started/)
- [MojoHaus — Exec Maven Plugin](https://www.mojohaus.org/exec-maven-plugin/)
- [PostgreSQL JDBC Driver](https://jdbc.postgresql.org/documentation/)
- [PostgreSQL 16 — CREATE TABLE](https://www.postgresql.org/docs/16/sql-createtable.html)
- [PostgreSQL 16 — Tipos numéricos](https://www.postgresql.org/docs/16/datatype-numeric.html)
- [Docker — Referência do Compose](https://docs.docker.com/reference/compose-file/)
- [Docker — Imagem oficial PostgreSQL](https://hub.docker.com/_/postgres)
- [pgAdmin — Implantação em container](https://www.pgadmin.org/docs/pgadmin4/latest/container_deployment.html)
