package com.baracho.api.controleponto.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "funcionario")
public class Funcionario {

    private Long id;
    private String nome;
    private String email;
    private String senha;
    private String cpf;
    private BigDecimal valorHora;
    private Float qtdHorasTrabalhoDia;
    private Float qtdHorasAlmoco;

    private Date dataCriacao;
    private Date dataAtualizacao;
    private Empresa empresa;


}
