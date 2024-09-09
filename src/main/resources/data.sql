INSERT INTO `contas` (`id`,`numero_conta`, `nome_titular`, `saldo`) VALUES (1,123456, 'vilela', 1000.00);

INSERT INTO `extratos` (ID, CONTA_ID) VALUES (1,1);

INSERT INTO `usuarios` (`ID`,`numero_conta`, `login`, `senha`, `role`) VALUES ( 1,123456, 'admin', '$2a$12$4qY7B/FHCIsrkFrhCwWV0uvwesOrw5Y97l8JX/Glbg8zffaKKIGPy', 'ADMIN');


INSERT INTO `contas` (`id`, `numero_conta`, `nome_titular`, `saldo`) VALUES (2, 111111, 'usuario um', 1500.00);

INSERT INTO `extratos` (ID, CONTA_ID) VALUES (2, 2);

INSERT INTO `usuarios` (`ID`, `numero_conta`, `login`, `senha`, `role`) VALUES (2, 111111, 'usuario1', '$2a$12$4qY7B/FHCIsrkFrhCwWV0uvwesOrw5Y97l8JX/Glbg8zffaKKIGPz', 'USER');


INSERT INTO `contas` (`id`, `numero_conta`, `nome_titular`, `saldo`) VALUES (3, 222222, 'usuario dois', 2000.00);

INSERT INTO `extratos` (ID, CONTA_ID) VALUES (3, 3);

INSERT INTO `usuarios` (`ID`, `numero_conta`, `login`, `senha`, `role`) VALUES (3, 222222, 'usuario2', '$2a$12$4qY7B/FHCIsrkFrhCwWV0uvwesOrw5Y97l8JX/Glbg8zffaKKIGQz', 'USER');


INSERT INTO `contas` (`id`, `numero_conta`, `nome_titular`, `saldo`) VALUES (4, 333333, 'usuario três', 2500.00);

INSERT INTO `extratos` (ID, CONTA_ID) VALUES (4, 4);

INSERT INTO `usuarios` (`ID`, `numero_conta`, `login`, `senha`, `role`) VALUES (4, 333333, 'usuario3', '$2a$12$4qY7B/FHCIsrkFrhCwWV0uvwesOrw5Y97l8JX/Glbg8zffaKKIGRx', 'USER');
