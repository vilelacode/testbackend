package com.vileladev.testbackend.entities.dto;

import com.vileladev.testbackend.entities.LogMovimentacao;

import java.math.BigDecimal;
import java.util.List;

public record SaldoExtratoResponse(Long conta, BigDecimal saldoTotal, List<LogMovimentacao> logs) {

}

