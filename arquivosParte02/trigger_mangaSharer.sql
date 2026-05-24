USE manga_db2;
-- ----------------------------------------------------------
-- 3. TRIGGERS (Requisito: 02 Triggers com Justificativa)


-- [Trigger 01] Auditoria de Ações Administrativas (Requisito: Atualizar Tabela de Logs)
-- Justificativa: Monitora alterações na tabela Manga. Sempre que um moderador
-- assume a responsabilidade por uma obra, uma entrada de auditoria histórica
-- é disparada e persistida na tabela LogGerenciamento.
DELIMITER //
CREATE TRIGGER tg_log_moderacao_manga
AFTER UPDATE ON Manga
FOR EACH ROW
BEGIN
    -- Verifica se um administrador foi associado ao mangá nesta alteração
    IF OLD.idAdmin_moderador IS NULL AND NEW.idAdmin_moderador IS NOT NULL THEN
        INSERT INTO LogGerenciamento (idAdmin, idPadrao, dataAcao)
        VALUES (NEW.idAdmin_moderador, NEW.idManga, NOW());
    END IF;
END //
DELIMITER ;

-- [Trigger 02] Validação de Domínio de Notas
-- Justificativa: Impede a inserção de pontuações inválidas (fora do
-- intervalo de 0 a 10) 
-- por meio de restrição ativa em nível de banco de dados, protegendo a
-- integridade estatística do sistema.
DELIMITER //
CREATE TRIGGER tg_validar_pontuacao_limites
BEFORE INSERT ON Ler
FOR EACH ROW
BEGIN
    IF NEW.pontuacao < 0 OR NEW.pontuacao > 10 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Erro de Consistência: A pontuação avaliada deve obrigatoriamente estar entre 0 e 10.';
    END IF;
END //
DELIMITER ;