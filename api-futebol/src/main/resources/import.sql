-- Times
-- Usando INSERT INTO ... VALUES para compatibilidade geral (H2, Postgres, etc.)
INSERT INTO Time (id, nome, cidade) VALUES (1, 'Flamengo', 'Rio de Janeiro');
INSERT INTO Time (id, nome, cidade) VALUES (2, 'Palmeiras', 'São Paulo');
INSERT INTO Time (id, nome, cidade) VALUES (3, 'Atlético Mineiro', 'Belo Horizonte');
-- Removida linha ALTER SEQUENCE time_seq

-- Jogadores
INSERT INTO Jogador (id, nome, idade, time_id) VALUES (1, 'Zico', 30, 1);
INSERT INTO Jogador (id, nome, idade, time_id) VALUES (2, 'Endrick', 27, 1);
INSERT INTO Jogador (id, nome, idade, time_id) VALUES (3, 'Pelé', 40, 2);
INSERT INTO Jogador (id, nome, idade, time_id) VALUES (4, 'Ronaldo', 35, 3);
-- Removida linha ALTER SEQUENCE jogador_seq

-- Partidas
-- Nomes das colunas gols_casa e gols_fora devem corresponder ao @Column na entidade Partida
INSERT INTO Partida (id, time_casa_id, time_fora_id, gols_casa, gols_fora, data)
VALUES (1, 1, 2, 3, 1, '2024-03-19');
-- Removida linha ALTER SEQUENCE partida_seq

-- Destaques
-- Nome da coluna gols_marcados deve corresponder ao @Column na entidade Destaque
-- Esta inserção deve funcionar agora que a Partida id=1 pode ser inserida
INSERT INTO Destaque (id, partida_id, jogador_id, gols_marcados)
VALUES (1, 1, 1, 3); -- Associa Destaque 1 à Partida 1, Jogador 1 (Zico), com 3 gols
-- Removida linha ALTER SEQUENCE destaque_seq