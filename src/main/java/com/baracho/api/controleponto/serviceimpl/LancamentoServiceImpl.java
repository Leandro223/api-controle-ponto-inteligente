package com.baracho.api.controleponto.serviceimpl;

import com.baracho.api.controleponto.entities.Lancamento;
import com.baracho.api.controleponto.repositories.LancamentoRepository;
import com.baracho.api.controleponto.service.LancamentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class LancamentoServiceImpl implements LancamentoService {
      private static final Logger log = LoggerFactory.getLogger(LancamentoServiceImpl.class);

      @Autowired
      private LancamentoRepository lancamentoRepository;

      @Override
      public Page<Lancamento> buscarPorfuncionarioId(Long funcionarioId, PageRequest pageRequest) {
            log.info("Buscando lançamentos para funcionario ID {}", funcionarioId);
            return this.lancamentoRepository.findByFuncionarioId(funcionarioId, pageRequest);
      }

      @Override
      public Optional<Lancamento> buscarPorId(Long id) {
            log.info("Buscando lancamento por Id {}", id);
            return this.lancamentoRepository.findById(id);
      }

      @Override
      public Lancamento persistir(Lancamento lancamento) {
            log.info("Persistindo o lançamento: {}", lancamento);
            return this.lancamentoRepository.save(lancamento);
      }

      @Override
      public void remover(Long id) {
            log.info("Removendo o lancamento ID: {}", id);
            this.lancamentoRepository.deleteById(id);

      }

      @Override
      public List<Lancamento> buscarTodos() {
            log.info("Buscando todos os lancamentos: ");
            return this.lancamentoRepository.findAll();
      }

      @Override
      public List<Lancamento> buscarPorData(LocalDate dataInicio, LocalDate dataFim) {
            log.info("buscaando lancamento por dataInicio: " + dataInicio + ", dataFim: " + dataFim);
            return this.lancamentoRepository.buscarPorData(dataInicio, dataFim);
      }
}
