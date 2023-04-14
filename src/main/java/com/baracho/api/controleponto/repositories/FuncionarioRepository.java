package com.baracho.api.controleponto.repositories;

import com.baracho.api.controleponto.entities.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface FuncionarioRepository  extends JpaRepository<Funcionario, Long> {

    Funcionario findByCpf(String cpf);

    Funcionario findByEmail(String email);

    Funcionario findByCpfOrEmail(String cpf, String email);

    @Query("SELECT f FROM Funcionario f WHERE f.nome = :nome")
    Optional<Funcionario> buscarPorNome(String nome);

    //Boolean existsByUsername(String nome);
}
