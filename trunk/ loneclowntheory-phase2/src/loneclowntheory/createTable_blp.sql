SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `LoneClownTheory` ;
CREATE SCHEMA IF NOT EXISTS `LoneClownTheory` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
USE `LoneClownTheory` ;

-- -----------------------------------------------------
-- Table `LoneClownTheory`.`entityTable`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `LoneClownTheory`.`entityTable` ;

CREATE  TABLE IF NOT EXISTS `LoneClownTheory`.`entityTable` (
  `entityID` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `entityName` VARCHAR(10) NOT NULL ,
  `subject_or_object` TINYINT(1)  NOT NULL ,
  `sensitivity` INT UNSIGNED NOT NULL,
  `category` SET('CA', 'AZ', 'NM', 'TX', 'SFO', 'LAX', 'PHX', 'TUS', 'ABQ', 'IAH', 'DAL'),
  PRIMARY KEY (`entityID`) ,
  UNIQUE INDEX `entityName_UNIQUE` (`entityName` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `LoneClownTheory`.`acm`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `LoneClownTheory`.`acm` ;

CREATE  TABLE IF NOT EXISTS `LoneClownTheory`.`acm` (
  `subject` VARCHAR(10) NOT NULL ,
  `entity` VARCHAR(10) NOT NULL ,
  `granter` VARCHAR(10) NOT NULL ,
  `right` VARCHAR(1) NOT NULL ,
  `timestamp` TIMESTAMP NOT NULL ,
  PRIMARY KEY (`subject`, `entity`, `right`, `granter`) ,
  INDEX `entity` (`entity` ASC) ,
  INDEX `subject` (`subject` ASC) ,
  CONSTRAINT `subject`
    FOREIGN KEY (`subject` )
    REFERENCES `LoneClownTheory`.`entityTable` (`entityName` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `entity`
    FOREIGN KEY (`entity` )
    REFERENCES `LoneClownTheory`.`entityTable` (`entityName` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `LoneClownTheory`.`entityTable`
-- -----------------------------------------------------
SET AUTOCOMMIT=0;
USE `LoneClownTheory`;
INSERT INTO `LoneClownTheory`.`entityTable` (`entityID`, `entityName`, `subject_or_object`, `sensitivity`, `category`) VALUES (1, 'subject0', 1, 0,'CA,AZ,LAX,SFO,PHX,TUS');
COMMIT;

-- -----------------------------------------------------
-- Data for table `LoneClownTheory`.`acm`
-- -----------------------------------------------------
SET AUTOCOMMIT=0;
USE `LoneClownTheory`;
INSERT INTO `LoneClownTheory`.`acm` (`subject`, `entity`, `granter`, `right`, `timestamp`) VALUES ('subject0', 'subject0', 'subject0', 'o', NULL);

COMMIT;
