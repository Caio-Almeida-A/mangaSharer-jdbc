USE manga_db2;
-- ----------------------------------------------------------
-- 2. PROCEDIMENTOS (Requisito: 02 Procedimentos)

-- [Procedure 01] Atualização Cadastral de Notas (Requisito: Atualização de Dados)
-- Justificativa: Atualiza a pontuação que um leitor deu a um determinado mangá.
DELIMITER //
CREATE PROCEDURE sp_atualizar_nota_leitor(
    IN p_idLeitor INT,
    IN p_idManga INT,
    IN p_nova_pontuacao INT
)
BEGIN
    UPDATE Ler 
    SET pontuacao = p_nova_pontuacao 
    WHERE idLeitor = p_idLeitor AND idManga = p_idManga;
END //
DELIMITER ;

-- [Procedure 02] Balanceamento de Moderação (Requisito: Uso Obrigatório de CURSOR)
-- Justificativa: Percorre sequencialmente todos os mangás que ainda não possuem
-- um moderador atribuído e designa dinamicamente para o administrador que tiver
-- a menor carga de trabalho no momento. Motivo de usar Cursor: 
-- Como a carga de trabalho de cada administrador muda a cada iteração 
-- do laço, uma instrução UPDATE em lote comum seria incapaz de recalcular o
-- mínimo de forma dinâmica e individual.
DELIMITER //
CREATE PROCEDURE sp_balancear_moderacao_mangas()
BEGIN
    DECLARE v_idManga INT;
    DECLARE v_idAdmin_escolhido INT;
    DECLARE v_fim INT DEFAULT 0;

    -- Declaração do CURSOR para selecionar mangás sem moderador
    DECLARE cur_mangas CURSOR FOR 
        SELECT idManga FROM Manga WHERE idAdmin_moderador IS NULL;
    
    -- Handler para encerrar o laço do cursor
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_fim = 1;

    OPEN cur_mangas;

    read_loop: LOOP
        FETCH cur_mangas INTO v_idManga;
        IF v_fim = 1 THEN
            LEAVE read_loop;
        END IF;

        -- Localiza dinamicamente o administrador com o menor número
        -- de mangás sob moderação
        SELECT idAdmin INTO v_idAdmin_escolhido
        FROM Admin a
        LEFT JOIN Manga m ON a.idAdmin = m.idAdmin_moderador
        GROUP BY a.idAdmin
        ORDER BY COUNT(m.idManga) ASC
        LIMIT 1;

        -- Aplica a moderação calculada linha por linha
        IF v_idAdmin_escolhido IS NOT NULL THEN
            UPDATE Manga 
            SET idAdmin_moderador = v_idAdmin_escolhido 
            WHERE idManga = v_idManga;
        END IF;
    END LOOP;

    CLOSE cur_mangas;
END //
DELIMITER ;