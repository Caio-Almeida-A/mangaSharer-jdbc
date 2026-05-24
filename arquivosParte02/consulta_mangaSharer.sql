USE manga_db2;

-- 1. CONSULTAS (Requisito: 4 Consultas Específicas)

-- = Join + Group By + Having
-- Objetivo: Listar a média de pontuação de cada mangá, apenas para obras
-- com média > 7.
SELECT m.nome AS Manga, AVG(l.pontuacao) AS Media_Nota
FROM Manga m
JOIN Ler l ON m.idManga = l.idManga
GROUP BY m.nome
HAVING Media_Nota > 7;

-- = 2 Joins + Where
-- Objetivo: Listar o nome do mangá, o nome do autor (Artista) e o nome do
-- moderador responsável.
SELECT m.nome AS Manga, u_art.nomeUsuario AS Autor, u_adm.nomeUsuario AS Moderador
FROM Manga m
JOIN Artista a ON m.idArtista = a.idArtista
JOIN Usuario u_art ON a.idArtista = u_art.idUsuario
LEFT JOIN Usuario u_adm ON m.idAdmin_moderador = u_adm.idUsuario
WHERE u_adm.nomeUsuario IS NOT NULL;

-- = Anti Join (Left Join + Is Null)
-- Objetivo: Encontrar usuários que são Leitores, mas que ainda não avaliaram
-- nenhum mangá.
SELECT u.nomeUsuario AS Leitor_Sem_Avaliacao
FROM Leitor l
JOIN Usuario u ON l.idLeitor = u.idUsuario
LEFT JOIN Ler r ON l.idLeitor = r.idLeitor
WHERE r.idLeitor IS NULL;

-- = Subconsulta
-- Objetivo: Listar os nomes dos mangás que possuem uma pontuação acima da média
-- global do sistema.
SELECT nome 
FROM Manga 
WHERE idManga IN (
    SELECT idManga FROM Ler WHERE pontuacao > (SELECT AVG(pontuacao) FROM Ler)
);