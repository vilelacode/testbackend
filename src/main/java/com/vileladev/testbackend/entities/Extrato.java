package com.vileladev.testbackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "extratos")
@Data
public class Extrato implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @OneToOne
    @JoinColumn(name = "conta_id", unique = true )
    @JsonIgnore
    private Conta conta;

    @OneToMany(mappedBy = "extrato", cascade = CascadeType.ALL)
    private List<LogMovimentacao> logs;

    public Extrato() {
    }

    public Extrato(Conta conta, List<LogMovimentacao> logs) {
        this.conta = conta;
        this.logs = logs;
    }
}



