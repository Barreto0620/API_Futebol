-- Inserindo times
INSERT INTO Time (id, nome, cidade) VALUES (1, 'Flamengo', 'Rio de Janeiro');
INSERT INTO Time (id, nome, cidade) VALUES (2, 'Palmeiras', 'São Paulo');
INSERT INTO Time (id, nome, cidade) VALUES (3, 'Atlético Mineiro', 'Belo Horizonte');

-- Inserindo jogadores
INSERT INTO Jogador (id, nome, idade, time_id) VALUES (1, 'Zico', 30, 1);
INSERT INTO Jogador (id, nome, idade, time_id) VALUES (2, 'Pelé', 40, 2);
INSERT INTO Jogador (id, nome, idade, time_id) VALUES (3, 'Ronaldo', 35, 3);

-- Inserindo partidas
INSERT INTO Partida (id, time_casa_id, time_fora_id, gols_casa, gols_fora, data)
VALUES (1, 1, 2, 3, 1, '2024-03-19');

INSERT INTO Partida (id, time_casa_id, time_fora_id, gols_casa, gols_fora, data)
VALUES (2, 2, 3, 2, 2, '2024-03-20');
