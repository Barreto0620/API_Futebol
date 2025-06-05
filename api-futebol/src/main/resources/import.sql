-- Times
INSERT INTO Time (nome, cidade) VALUES ('Flamengo', 'Rio de Janeiro');
INSERT INTO Time (nome, cidade) VALUES ('Palmeiras', 'São Paulo');
INSERT INTO Time (nome, cidade) VALUES ('Atlético Mineiro', 'Belo Horizonte');

-- Jogadores
INSERT INTO Jogador (nome, idade, time_id) VALUES ('Zico', 30, 1);
INSERT INTO Jogador (nome, idade, time_id) VALUES ('Endrick', 27, 1);
INSERT INTO Jogador (nome, idade, time_id) VALUES ('Pelé', 40, 2);
INSERT INTO Jogador (nome, idade, time_id) VALUES ('Ronaldo', 35, 3);

-- Partidas
INSERT INTO Partida (time_casa_id, time_fora_id, gols_casa, gols_fora, data)
VALUES (1, 2, 3, 1, '2024-03-19');

-- Destaques
INSERT INTO Destaque (partida_id, jogador_id, gols_marcados)
VALUES (1, 1, 3); -- Associa Destaque à Partida 1, Jogador 1 (Zico), com 3 gols