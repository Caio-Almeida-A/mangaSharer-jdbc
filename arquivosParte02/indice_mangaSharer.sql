USE manga_db2;

-- 3. ÍNDICES (Requisito: 2 Índices Úteis com Justificativa)

-- = Busca por Título
-- Justificativa: Como o sistema terá muitos mangás, o índice no campo 'nome' 
-- acelera drasticamente as buscas textuais e a ordenação alfabética na interface.
CREATE INDEX idx_manga_nome ON Manga(nome);

-- = Filtro de Notas
-- Justificativa: A tabela 'Ler' terá milhares de registros. Um índice na 
-- 'pontuacao' otimiza as consultas de média (AVG) e filtros de "Top Rated"
-- usados no Dashboard.
CREATE INDEX idx_ler_pontuacao ON Ler(pontuacao);