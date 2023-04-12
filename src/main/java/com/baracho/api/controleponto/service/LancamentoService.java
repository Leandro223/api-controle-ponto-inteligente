package com.baracho.api.controleponto.service;

import com.baracho.api.controleponto.entities.Lancamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LancamentoService {

      /**
       * Retorna uma lista de lancamento de um determinado funcionario
       * @param funcionarioId
       * @param pageRequest
       * @return Page<Lancamento>>
       */
      Page<Lancamento> buscarPorfuncionarioId(Long funcionarioId, PageRequest pageRequest);

      /**
       * Retorna um lancamento pelo seu id na base de dados
       * @param id
       * @return Optional<Lancamento>
       */
      Optional<Lancamento> buscarPorId(Long id);


      /**
       * Persiste um lancamento na base de dados
       * @param lancamento
       * @return lancamento
       */
      Lancamento persistir(Lancamento lancamento);

      /**
       * Deleta um lancamento com base no seu id
       * @param id
       */
      void remover(Long id);

      /**
       * Busca todos os lancamentos na base de dados
       * @return
       */
      List<Lancamento> buscarTodos();

      /**
       * Busca todos os lancamentos na base de dados conforme a data passada de inicio e fim
       * @param dataInicio
       * @param dataFim
       * @return List<Lancamento>
       */
      List<Lancamento> buscarPorData(LocalDate dataInicio, LocalDate dataFim);
}
