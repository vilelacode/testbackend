
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    role VARCHAR(50),
    numero_conta BIGINT,
    CONSTRAINT fk_usuario_conta FOREIGN KEY (numero_conta) REFERENCES contas(numeroConta) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS contas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numeroConta BIGINT NOT NULL UNIQUE,
    nomeTitular VARCHAR(255) NOT NULL,
    saldo DECIMAL(19, 2) NOT NULL
);


CREATE TABLE IF NOT EXISTS extratos (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  conta_id BIGINT UNIQUE,
  CONSTRAINT fk_extrato_conta FOREIGN KEY (conta_id) REFERENCES contas(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS  logs_movimentacao (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   dataHora TIMESTAMP NOT NULL,
   solicitante VARCHAR(255),
   numeroContaDestinatario BIGINT,
   valor DECIMAL(19, 2),
   descricao VARCHAR(255),
   extrato_id BIGINT,
   CONSTRAINT fk_log_extrato FOREIGN KEY (extrato_id) REFERENCES extratos(id) ON DELETE CASCADE
);


