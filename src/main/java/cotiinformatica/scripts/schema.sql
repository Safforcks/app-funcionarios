create table funcionarios (
    id                  uuid                primary key,
    nome                varchar(150)        not null,
    cpf                 varchar(14)         not null,
    matricula           varchar(20)         not null,
    salario             numeric(10,2)       not null,
    cargo               varchar(50)         not null,
    datahoracadastro    timestamp           default current_timestamp
)