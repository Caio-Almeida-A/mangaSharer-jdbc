USE manga_db2;
-- ETAPA 05 - FUNÇÕES, PROCEDIMENTOS E TRIGGERS
-- ----------------------------------------------------------
-- 1. FUNÇÕES (Requisito: 02 Funções com Justificativa)

-- [Função 01] Classificação Dinâmica do Mangá (Com Estrutura Condicional)
-- Justificativa: Calcula a média de notas de um mangá e retorna um rótulo 
-- de qualidade. Essencial para que a interface exiba selos (ex: "Excelente")  
-- sem processar texto no Java.
DELIMITER //
CREATE FUNCTION fn_classificar_manga(p_idManga INT)
RETURNS VARCHAR(20)
DETERMINISTIC
BEGIN
    DECLARE v_media DECIMAL(4,2);
    DECLARE v_classificacao VARCHAR(20);

    -- Calcula a média aritmética da tabela Ler
    SELECT AVG(pontuacao) INTO v_media FROM Ler WHERE idManga = p_idManga;

    -- Estrutura Condicional para definição do rótulo
    IF v_media IS NULL THEN
        SET v_classificacao = 'Sem Avaliações';
    ELSEIF v_media >= 8.0 THEN
        SET v_classificacao = 'Excelente';
    ELSEIF v_media >= 5.0 THEN
        SET v_classificacao = 'Bom';
    ELSE
        SET v_classificacao = 'Regular';
    END IF;

    RETURN v_classificacao;
END //
DELIMITER ;

-- [Função 02] Contador de Engajamento de Leitores
-- Justificativa: Retorna a quantidade total de comentários de
-- um leitor específico. Útil para criar sistemas de medalhas ou níveis
-- de usuários ativos diretamente na aplicação.
DELIMITER //
CREATE FUNCTION fn_total_comentarios_leitor(p_idLeitor INT)
RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE v_total INT;
    SELECT COUNT(*) INTO v_total FROM comentar WHERE idLeitor = p_idLeitor;
    RETURN v_total;
END //
DELIMITER ;