USE manga_db2;

-- ==========================================================
-- 1. LIMPEZA DE DADOS (Preserva a estrutura, limpa o conteúdo)
-- ==========================================================
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE pagina;
TRUNCATE TABLE capitulo;
TRUNCATE TABLE ler;
TRUNCATE TABLE manga;
TRUNCATE TABLE artista;
TRUNCATE TABLE leitor;
TRUNCATE TABLE admin;
TRUNCATE TABLE padrao;
TRUNCATE TABLE usuario;
SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================================
-- 2. LIMPEZA DE CONSTRAINTS (O Escudo Atualizado)
-- ==========================================================
DELIMITER //
CREATE PROCEDURE sp_limpar_requisitos_entrega()
BEGIN
    -- 1. Limpa FKs da tabela LER (Nomes Novos e Antigos)
    IF EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE CONSTRAINT_SCHEMA = DATABASE() AND CONSTRAINT_NAME = 'fk_ler_leitor') THEN
        ALTER TABLE ler DROP FOREIGN KEY fk_ler_leitor;
    END IF;
    IF EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE CONSTRAINT_SCHEMA = DATABASE() AND CONSTRAINT_NAME = 'fk_ler_manga') THEN
        ALTER TABLE ler DROP FOREIGN KEY fk_ler_manga;
    END IF;
    -- Nomes do Workbench (Caso ainda existam)
    IF EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE CONSTRAINT_SCHEMA = DATABASE() AND CONSTRAINT_NAME = 'fk_Leitor_has_Manga_Leitor1') THEN
        ALTER TABLE ler DROP FOREIGN KEY fk_Leitor_has_Manga_Leitor1;
    END IF;
    IF EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE CONSTRAINT_SCHEMA = DATABASE() AND CONSTRAINT_NAME = 'fk_Leitor_has_Manga_Manga1') THEN
        ALTER TABLE ler DROP FOREIGN KEY fk_Leitor_has_Manga_Manga1;
    END IF;

    -- 2. Limpa FK da Manga
    IF EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE CONSTRAINT_SCHEMA = DATABASE() AND CONSTRAINT_NAME = 'fk_Manga_Admin1') THEN
        ALTER TABLE manga DROP FOREIGN KEY fk_Manga_Admin1;
    END IF;

    -- 3. Limpa FK do Capitulo
    IF EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE CONSTRAINT_SCHEMA = DATABASE() AND CONSTRAINT_NAME = 'fk_Capitulo_Manga1') THEN
        ALTER TABLE capitulo DROP FOREIGN KEY fk_Capitulo_Manga1;
    END IF;

    -- 4. Limpa CHECK e UNIQUE
    IF EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE CONSTRAINT_SCHEMA = DATABASE() AND CONSTRAINT_NAME = 'chk_nota') THEN
        ALTER TABLE ler DROP CONSTRAINT chk_nota;
    END IF;
    IF EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'usuario' AND INDEX_NAME = 'unq_email') THEN
        ALTER TABLE usuario DROP INDEX unq_email;
    END IF;
END //
DELIMITER ;

CALL sp_limpar_requisitos_entrega();
DROP PROCEDURE sp_limpar_requisitos_entrega;
-- ==========================================================
-- 3. REAPLICANDO REQUISITOS (Constraints Obrigatórias)
-- ==========================================================

-- Requisito: DELETE SET NULL e UPDATE CASCADE (Tabela Manga)
ALTER TABLE manga 
ADD CONSTRAINT fk_Manga_Admin1 
FOREIGN KEY (idAdmin_moderador) REFERENCES admin(idAdmin) 
ON DELETE SET NULL ON UPDATE CASCADE;

-- Requisito: UPDATE CASCADE e DELETE CASCADE (Tabela Capitulo)
ALTER TABLE capitulo 
ADD CONSTRAINT fk_Capitulo_Manga1 
FOREIGN KEY (idManga) REFERENCES manga(idManga) 
ON UPDATE CASCADE ON DELETE CASCADE;

-- Requisito: UNIQUE (E-mail único na tabela Usuario)
ALTER TABLE usuario ADD CONSTRAINT unq_email UNIQUE (email);

-- Requisito: DEFAULT (Garante valor 0 inicial na pontuação)
ALTER TABLE ler MODIFY COLUMN pontuacao INT NOT NULL DEFAULT 0;

-- Requisito: CHECK (Pontuação entre 0 e 10)
ALTER TABLE ler ADD CONSTRAINT chk_nota CHECK (pontuacao >= 0 AND pontuacao <= 10);

-- Requisito: INTEGRIDADE DA TABELA LER (Faltou no seu script!)
ALTER TABLE ler 
ADD CONSTRAINT fk_ler_leitor FOREIGN KEY (idLeitor) REFERENCES leitor(idLeitor)
ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE ler 
ADD CONSTRAINT fk_ler_manga FOREIGN KEY (idManga) REFERENCES manga(idManga)
ON DELETE CASCADE ON UPDATE CASCADE;

-- ==========================================================
-- 4. POVOAMENTO (REQUISITO: 30 TUPLAS - SEM IDS MANUAIS)
-- ==========================================================

-- 4.1 Inserindo 30 Usuários (O banco gera os IDs de 1 a 30 automaticamente)
INSERT INTO usuario (nomeUsuario, email) VALUES 
('Oda','oda@e.com'), ('Kishimoto','k@e.com'), ('Kubo','kb@e.com'), ('Toyotaro','t@e.com'), ('Horikoshi','h@e.com'),
('Murata','m@e.com'), ('Inoue','i@e.com'), ('Miura','miu@e.com'), ('Togashi','tog@e.com'), ('Urasawa','u@e.com'),
('Admin1','adm1@e.com'), ('Admin2','adm2@e.com'), ('User1','u1@e.com'), ('User2','u2@e.com'), ('User3','u3@e.com'),
('User4','u4@e.com'), ('User5','u5@e.com'), ('User6','u6@e.com'), ('User7','u7@e.com'), ('User8','u8@e.com'),
('User9','u9@e.com'), ('User10','u10@e.com'), ('User11','u11@e.com'), ('User12','u12@e.com'), ('User13','u13@e.com'),
('User14','u14@e.com'), ('User15','u15@e.com'), ('User16','u16@e.com'), ('User17','u17@e.com'), ('User18','u18@e.com');

-- 4.2 Vinculando Perfis (Usando SELECT para evitar IDs "mágicos")
-- Vincula todos como Padrão
INSERT INTO padrao (idPadrao) SELECT idUsuario FROM usuario;

-- Artistas (Os 10 primeiros usuários)
INSERT INTO artista (idArtista) SELECT idUsuario FROM usuario WHERE idUsuario <= 10;

-- Admins (Os usuários 11 e 12)
INSERT INTO admin (idAdmin) SELECT idUsuario FROM usuario WHERE idUsuario BETWEEN 11 AND 12;

-- Leitores (Do 13 ao 30)
INSERT INTO leitor (idLeitor) SELECT idUsuario FROM usuario WHERE idUsuario >= 13;

-- 4.3 Inserindo 30 Mangás (O banco gera os IDs de 1 a 30)
-- Referenciamos os Artistas de 1 a 10 de forma cíclica ou direta
INSERT INTO manga (nome, idArtista) VALUES 
('One Piece', 1), ('Naruto', 2), ('Bleach', 3), ('Dragon Ball Super', 4), ('My Hero Academia', 5),
('One Punch Man', 6), ('Vagabond', 7), ('Berserk', 8), ('Hunter x Hunter', 9), ('Monster', 10),
('Blue Lock', 1), ('Jujutsu Kaisen', 2), ('Black Clover', 3), ('Boruto', 4), ('Kaiju No. 8', 5),
('Eyeshield 21', 6), ('Slam Dunk', 7), ('Gigantomakhia', 8), ('YuYu Hakusho', 9), ('20th Century Boys', 10),
('Kingdom', 1), ('Chainsaw Man', 2), ('Burn the Witch', 3), ('Sand Land', 4), ('Oumagadoki Zoo', 5),
('Mob Psycho 100', 6), ('Real', 7), ('Duranki', 8), ('Level E', 9), ('Pluto', 10);

-- 4.4 Inserindo 30 Capítulos (idCapitulo é AUTO_INCREMENT)
-- Usamos um SELECT para inserir 1 capítulo para cada mangá existente
INSERT INTO capitulo (idManga, nomeCapitulo) 
SELECT idManga, CONCAT('Capítulo 1 de ', nome) FROM manga;

-- 4.5 Inserindo 30 Páginas (idPagina é AUTO_INCREMENT)
-- Usamos o ID do capítulo gerado acima
INSERT INTO pagina (idCapitulo, idManga, url) 
SELECT idCapitulo, idManga, CONCAT('https://storage.com/manga', idManga, '/p1.jpg') FROM capitulo;

-- 4.6 Inserindo 30 Notas (Aqui idLeitor e idManga são FKs, não são AI, então os valores são necessários)
INSERT INTO ler (idLeitor, idManga, pontuacao) VALUES 
(13,1,10),(14,2,9),(15,3,8),(16,4,7),(17,5,10),(18,6,9),(19,7,10),(20,8,10),(21,9,9),(22,10,10),
(23,11,8),(24,12,9),(25,13,7),(26,14,6),(27,15,8),(28,16,9),(29,17,10),(30,18,10),(13,19,9),(14,20,10),
(15,21,8),(16,22,9),(17,23,7),(18,24,6),(19,25,8),(20,26,9),(21,27,10),(22,28,10),(23,29,9),(24,30,10);
-- ==========================================================
-- 5. VERIFICAÇÃO FINAL
-- ==========================================================
SELECT m.nome AS Obra, u_art.nomeUsuario AS Autor, l.pontuacao AS Nota
FROM manga m
JOIN artista a ON m.idArtista = a.idArtista
JOIN usuario u_art ON a.idArtista = u_art.idUsuario
JOIN ler l ON m.idManga = l.idManga
LIMIT 10;