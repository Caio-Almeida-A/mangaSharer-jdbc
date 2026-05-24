USE manga_db2;

-- 2. VISÕES (Requisito: 2 Visões Elaboradas com Justificativa)

-- = Resumo da Obra (3 Joins + Where)
-- Justificativa: Centraliza informações críticas para a interface do 
-- administrador, unindo autor, obra e moderador em um único objeto de visualização.
CREATE OR REPLACE VIEW vw_resumo_obras_moderadas AS
SELECT m.idManga, m.nome AS Obra, u_art.nomeUsuario AS Autor, u_adm.nomeUsuario AS Moderador
FROM Manga m
JOIN Artista a ON m.idArtista = a.idArtista
JOIN Usuario u_art ON a.idArtista = u_art.idUsuario
JOIN Admin adm ON m.idAdmin_moderador = adm.idAdmin
JOIN Usuario u_adm ON adm.idAdmin = u_adm.idUsuario
WHERE m.idAdmin_moderador IS NOT NULL;

-- = Ranking de Engajamento (1 Join + Subconsulta)
-- Justificativa: Permite que a interface mostre quais obras estão gerando mais 
-- discussões (comentários), facilitando a criação de uma seção de "Destaques".
CREATE OR REPLACE VIEW vw_ranking_comentarios AS
SELECT m.nome, 
       (SELECT COUNT(*) FROM comentar c WHERE c.idManga = m.idManga) AS Total_Comentarios
FROM Manga m
ORDER BY Total_Comentarios DESC;