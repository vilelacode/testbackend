package com.vileladev.testbackend.repositories;

import com.vileladev.testbackend.entities.LogMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogMovimentacaoRepository extends JpaRepository<LogMovimentacao, Long> {


}
