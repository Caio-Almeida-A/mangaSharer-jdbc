-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema manga_db2
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema manga_db2
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `manga_db2` DEFAULT CHARACTER SET utf8 ;
USE `manga_db2` ;

-- -----------------------------------------------------
-- Table `manga_db2`.`Usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `manga_db2`.`Usuario` (
  `idUsuario` INT NOT NULL AUTO_INCREMENT,
  `nomeUsuario` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`idUsuario`),
  UNIQUE INDEX `unq_email` (`email` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `manga_db2`.`Usuario_telefone`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `manga_db2`.`Usuario_telefone` (
  `telefone` VARCHAR(20) NOT NULL,
  `idUsuario` INT NOT NULL,
  INDEX `fk_Usuario_telefone_Usuario_idx` (`idUsuario` ASC) VISIBLE,
  PRIMARY KEY (`telefone`, `idUsuario`),
  CONSTRAINT `fk_Usuario_telefone_Usuario`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `manga_db2`.`Usuario` (`idUsuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `manga_db2`.`Admin`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `manga_db2`.`Admin` (
  `idAdmin` INT NOT NULL,
  `id_supervisor` INT NULL,
  PRIMARY KEY (`idAdmin`),
  INDEX `fk_Admin_Admin1_idx` (`id_supervisor` ASC) VISIBLE,
  CONSTRAINT `fk_Admin_Usuario1`
    FOREIGN KEY (`idAdmin`)
    REFERENCES `manga_db2`.`Usuario` (`idUsuario`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Admin_Admin1`
    FOREIGN KEY (`id_supervisor`)
    REFERENCES `manga_db2`.`Admin` (`idAdmin`)
    ON DELETE SET NULL
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `manga_db2`.`Padrao`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `manga_db2`.`Padrao` (
  `idPadrao` INT NOT NULL,
  PRIMARY KEY (`idPadrao`),
  CONSTRAINT `fk_Padrao_Usuario1`
    FOREIGN KEY (`idPadrao`)
    REFERENCES `manga_db2`.`Usuario` (`idUsuario`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `manga_db2`.`Leitor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `manga_db2`.`Leitor` (
  `idLeitor` INT NOT NULL,
  PRIMARY KEY (`idLeitor`),
  CONSTRAINT `fk_Leitor_Padrao1`
    FOREIGN KEY (`idLeitor`)
    REFERENCES `manga_db2`.`Padrao` (`idPadrao`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `manga_db2`.`Artista`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `manga_db2`.`Artista` (
  `idArtista` INT NOT NULL,
  PRIMARY KEY (`idArtista`),
  CONSTRAINT `fk_Artista_Padrao1`
    FOREIGN KEY (`idArtista`)
    REFERENCES `manga_db2`.`Padrao` (`idPadrao`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `manga_db2`.`Manga`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `manga_db2`.`Manga` (
  `idManga` INT NOT NULL AUTO_INCREMENT,
  `idArtista` INT NOT NULL,
  `nome` VARCHAR(150) NOT NULL,
  `idAdmin_moderador` INT NULL,
  PRIMARY KEY (`idManga`),
  INDEX `fk_Manga_Admin1_idx` (`idAdmin_moderador` ASC) VISIBLE,
  CONSTRAINT `fk_Manga_Artista1`
    FOREIGN KEY (`idArtista`)
    REFERENCES `manga_db2`.`Artista` (`idArtista`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Manga_Admin1`
    FOREIGN KEY (`idAdmin_moderador`)
    REFERENCES `manga_db2`.`Admin` (`idAdmin`)
    ON DELETE SET NULL
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `manga_db2`.`Capitulo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `manga_db2`.`Capitulo` (
  `idCapitulo` INT NOT NULL AUTO_INCREMENT,
  `idManga` INT NOT NULL,
  `nomeCapitulo` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`idCapitulo`, `idManga`),
  INDEX `fk_Capitulo_Manga1_idx` (`idManga` ASC) VISIBLE,
  CONSTRAINT `fk_Capitulo_Manga1`
    FOREIGN KEY (`idManga`)
    REFERENCES `manga_db2`.`Manga` (`idManga`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `manga_db2`.`Pagina`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `manga_db2`.`Pagina` (
  `idPagina` INT NOT NULL AUTO_INCREMENT,
  `idCapitulo` INT NOT NULL,
  `idManga` INT NOT NULL,
  `url` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`idPagina`, `idCapitulo`, `idManga`),
  INDEX `fk_Pagina_Capitulo1_idx` (`idManga` ASC, `idCapitulo` ASC) VISIBLE,
  CONSTRAINT `fk_Pagina_Capitulo1`
    FOREIGN KEY (`idManga` , `idCapitulo`)
    REFERENCES `manga_db2`.`Capitulo` (`idManga` , `idCapitulo`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `manga_db2`.`Ler`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `manga_db2`.`Ler` (
  `idLeitor` INT NOT NULL,
  `idManga` INT NOT NULL,
  `pontuacao` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`idLeitor`, `idManga`),
  INDEX `fk_Leitor_has_Manga_Manga1_idx` (`idManga` ASC) VISIBLE,
  INDEX `fk_Leitor_has_Manga_Leitor1_idx` (`idLeitor` ASC) VISIBLE,
  CONSTRAINT `fk_Leitor_has_Manga_Leitor1`
    FOREIGN KEY (`idLeitor`)
    REFERENCES `manga_db2`.`Leitor` (`idLeitor`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Leitor_has_Manga_Manga1`
    FOREIGN KEY (`idManga`)
    REFERENCES `manga_db2`.`Manga` (`idManga`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `manga_db2`.`comentar`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `manga_db2`.`comentar` (
  `idComentario` INT NOT NULL AUTO_INCREMENT,
  `idLeitor` INT NOT NULL,
  `idManga` INT NOT NULL,
  `comentario` VARCHAR(200) NOT NULL,
  `idAdmin_moderador` INT NULL,
  PRIMARY KEY (`idComentario`),
  INDEX `fk_Leitor_has_Manga_Manga2_idx` (`idManga` ASC) VISIBLE,
  INDEX `fk_Leitor_has_Manga_Leitor2_idx` (`idLeitor` ASC) VISIBLE,
  INDEX `fk_comentar_Admin1_idx` (`idAdmin_moderador` ASC) VISIBLE,
  CONSTRAINT `fk_Leitor_has_Manga_Leitor2`
    FOREIGN KEY (`idLeitor`)
    REFERENCES `manga_db2`.`Leitor` (`idLeitor`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Leitor_has_Manga_Manga2`
    FOREIGN KEY (`idManga`)
    REFERENCES `manga_db2`.`Manga` (`idManga`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_comentar_Admin1`
    FOREIGN KEY (`idAdmin_moderador`)
    REFERENCES `manga_db2`.`Admin` (`idAdmin`)
    ON DELETE NO ACTION
    ON UPDATE SET NULL)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `manga_db2`.`gerenciar`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `manga_db2`.`gerenciar` (
  `idAdmin` INT NOT NULL,
  `idPadrao` INT NOT NULL,
  `dataAcao` DATETIME NOT NULL,
  PRIMARY KEY (`idAdmin`, `idPadrao`),
  INDEX `fk_Admin_has_Padrao_Padrao1_idx` (`idPadrao` ASC) VISIBLE,
  INDEX `fk_Admin_has_Padrao_Admin1_idx` (`idAdmin` ASC) VISIBLE,
  CONSTRAINT `fk_Admin_has_Padrao_Admin1`
    FOREIGN KEY (`idAdmin`)
    REFERENCES `manga_db2`.`Admin` (`idAdmin`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Admin_has_Padrao_Padrao1`
    FOREIGN KEY (`idPadrao`)
    REFERENCES `manga_db2`.`Padrao` (`idPadrao`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE)
ENGINE = InnoDB;

-- --- REQUISITOS DE INTEGRIDADE MANUAIS (PÓS-EXPORTAÇÃO) ---

-- 1. Garante que a nota esteja entre 0 e 10 (Requisito de CHECK)
ALTER TABLE `manga_db2`.`Ler` 
ADD CONSTRAINT `chk_pontuacao_limite` CHECK (`pontuacao` >= 0 AND `pontuacao` <= 10);

-- 2. Ajuste na Moderação de Comentários (SET NULL)
-- No seu script saiu como NO ACTION, o que impediria deletar o Admin.
ALTER TABLE `manga_db2`.`comentar` 
DROP FOREIGN KEY `fk_comentar_Admin1`;

ALTER TABLE `manga_db2`.`comentar` 
ADD CONSTRAINT `fk_comentar_Admin1` 
  FOREIGN KEY (`idAdmin_moderador`) REFERENCES `manga_db2`.`Admin` (`idAdmin`) 
  ON DELETE SET NULL ON UPDATE CASCADE;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
