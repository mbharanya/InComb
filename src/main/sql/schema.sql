-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema incomb
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `incomb` ;

-- -----------------------------------------------------
-- Schema incomb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `incomb` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `incomb` ;

-- -----------------------------------------------------
-- Table `incomb`.`user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `incomb`.`user` ;

CREATE TABLE IF NOT EXISTS `incomb`.`user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(255) NOT NULL,
  `username` VARCHAR(255) NOT NULL,
  `display_name` VARCHAR(255) NOT NULL,
  `password_hash` VARCHAR(255) NOT NULL,
  `salt` VARCHAR(255) NOT NULL,
  `register_date` DATETIME NOT NULL,
  `deleted` BIT(1) NOT NULL DEFAULT 0,
  `private_profile` BIT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `incomb`.`role`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `incomb`.`role` ;

CREATE TABLE IF NOT EXISTS `incomb`.`role` (
  `id` INT NOT NULL,
  `name_key` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `incomb`.`permission`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `incomb`.`permission` ;

CREATE TABLE IF NOT EXISTS `incomb`.`permission` (
  `id` INT NOT NULL,
  `name_key` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `incomb`.`user_nm_role`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `incomb`.`user_nm_role` ;

CREATE TABLE IF NOT EXISTS `incomb`.`user_nm_role` (
  `user_id` BIGINT NOT NULL,
  `role_id` INT NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`),
  INDEX `fk_User_has_Role_Role1_idx` (`role_id` ASC),
  INDEX `fk_User_has_Role_User_idx` (`user_id` ASC),
  CONSTRAINT `fk_User_has_Role_User`
    FOREIGN KEY (`user_id`)
    REFERENCES `incomb`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_User_has_Role_Role1`
    FOREIGN KEY (`role_id`)
    REFERENCES `incomb`.`role` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `incomb`.`role_nm_permission`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `incomb`.`role_nm_permission` ;

CREATE TABLE IF NOT EXISTS `incomb`.`role_nm_permission` (
  `role_id` INT NOT NULL,
  `permission_id` INT NOT NULL,
  PRIMARY KEY (`role_id`, `permission_id`),
  INDEX `fk_Role_has_Permission_Permission1_idx` (`permission_id` ASC),
  INDEX `fk_Role_has_Permission_Role1_idx` (`role_id` ASC),
  CONSTRAINT `fk_Role_has_Permission_Role1`
    FOREIGN KEY (`role_id`)
    REFERENCES `incomb`.`role` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Role_has_Permission_Permission1`
    FOREIGN KEY (`permission_id`)
    REFERENCES `incomb`.`permission` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `incomb`.`tag_preference`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `incomb`.`tag_preference` ;

CREATE TABLE IF NOT EXISTS `incomb`.`tag_preference` (
  `user_id` BIGINT NOT NULL,
  `tag` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`user_id`, `tag`),
  CONSTRAINT `fk_TagPreference_User1`
    FOREIGN KEY (`user_id`)
    REFERENCES `incomb`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `incomb`.`provider`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `incomb`.`provider` ;

CREATE TABLE IF NOT EXISTS `incomb`.`provider` (
  `id` INT NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `image_path` VARCHAR(255) NOT NULL,
  `website` VARCHAR(255) NOT NULL,
  `parser_class` VARCHAR(255) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `incomb`.`module`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `incomb`.`module` ;

CREATE TABLE IF NOT EXISTS `incomb`.`module` (
  `id` INT NOT NULL,
  `name_key` VARCHAR(255) NOT NULL,
  `image_path` VARCHAR(45) NOT NULL,
  `template_path` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `incomb`.`category`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `incomb`.`category` ;

CREATE TABLE IF NOT EXISTS `incomb`.`category` (
  `id` INT NOT NULL,
  `name_key` VARCHAR(255) NOT NULL,
  `image_path` VARCHAR(45) NOT NULL,
  `module_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_category_module1_idx` (`module_id` ASC),
  CONSTRAINT `fk_category_module1`
    FOREIGN KEY (`module_id`)
    REFERENCES `incomb`.`module` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `incomb`.`content_source`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `incomb`.`content_source` ;

CREATE TABLE IF NOT EXISTS `incomb`.`content_source` (
  `id` INT NOT NULL,
  `provider_id` INT NOT NULL,
  `category_id` INT NOT NULL,
  `interval` INT NOT NULL,
  `url` VARCHAR(255) NOT NULL,
  `locale` VARCHAR(5) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_ContentSource_Provider1_idx` (`provider_id` ASC),
  INDEX `fk_ContentSource_Category1_idx` (`category_id` ASC),
  CONSTRAINT `fk_ContentSource_Provider1`
    FOREIGN KEY (`provider_id`)
    REFERENCES `incomb`.`provider` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ContentSource_Category1`
    FOREIGN KEY (`category_id`)
    REFERENCES `incomb`.`category` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `incomb`.`fetch_history`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `incomb`.`fetch_history` ;

CREATE TABLE IF NOT EXISTS `incomb`.`fetch_history` (
  `content_source_id` INT NOT NULL,
  `fetch_time` DATETIME NOT NULL,
  `result` BIT(1) NOT NULL,
  PRIMARY KEY (`content_source_id`, `fetch_time`),
  INDEX `fk_FetchHistory_ContentSource1_idx` (`content_source_id` ASC),
  CONSTRAINT `fk_FetchHistory_ContentSource1`
    FOREIGN KEY (`content_source_id`)
    REFERENCES `incomb`.`content_source` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `incomb`.`rss_feed_content_source`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `incomb`.`rss_feed_content_source` ;

CREATE TABLE IF NOT EXISTS `incomb`.`rss_feed_content_source` (
  `content_source_id` INT NOT NULL,
  PRIMARY KEY (`content_source_id`),
  INDEX `fk_RSSFeedContentSource_ContentSource1_idx` (`content_source_id` ASC),
  CONSTRAINT `fk_RSSFeedContentSource_ContentSource1`
    FOREIGN KEY (`content_source_id`)
    REFERENCES `incomb`.`content_source` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `incomb`.`category_preference`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `incomb`.`category_preference` ;

CREATE TABLE IF NOT EXISTS `incomb`.`category_preference` (
  `user_id` BIGINT NOT NULL,
  `category_id` INT NOT NULL,
  `factor` FLOAT NOT NULL,
  PRIMARY KEY (`user_id`, `category_id`),
  INDEX `fk_CategoryPreference_Category1_idx` (`category_id` ASC),
  CONSTRAINT `fk_CategoryPreference_User1`
    FOREIGN KEY (`user_id`)
    REFERENCES `incomb`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_CategoryPreference_Category1`
    FOREIGN KEY (`category_id`)
    REFERENCES `incomb`.`category` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `incomb`.`provider_exclusion`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `incomb`.`provider_exclusion` ;

CREATE TABLE IF NOT EXISTS `incomb`.`provider_exclusion` (
  `user_id` BIGINT NOT NULL,
  `provider_id` INT NOT NULL,
  `category_id` INT NOT NULL,
  PRIMARY KEY (`user_id`, `provider_id`, `category_id`),
  INDEX `fk_ProviderExclusion_Provider1_idx` (`provider_id` ASC),
  INDEX `fk_ProviderExclusion_Category1_idx` (`category_id` ASC),
  CONSTRAINT `fk_ProviderExclusion_User1`
    FOREIGN KEY (`user_id`)
    REFERENCES `incomb`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ProviderExclusion_Provider1`
    FOREIGN KEY (`provider_id`)
    REFERENCES `incomb`.`provider` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ProviderExclusion_Category1`
    FOREIGN KEY (`category_id`)
    REFERENCES `incomb`.`category` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `incomb`.`content`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `incomb`.`content` ;

CREATE TABLE IF NOT EXISTS `incomb`.`content` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `provider_id` INT NOT NULL,
  `category_id` INT NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `locale` VARCHAR(5) NOT NULL,
  `text` MEDIUMTEXT NOT NULL,
  `publish_date` DATETIME NOT NULL,
  `indexed` BIT(1) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Content_Provider1_idx` (`provider_id` ASC),
  INDEX `fk_Content_Category1_idx` (`category_id` ASC),
  CONSTRAINT `fk_Content_Provider1`
    FOREIGN KEY (`provider_id`)
    REFERENCES `incomb`.`provider` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Content_Category1`
    FOREIGN KEY (`category_id`)
    REFERENCES `incomb`.`category` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `incomb`.`content_vote`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `incomb`.`content_vote` ;

CREATE TABLE IF NOT EXISTS `incomb`.`content_vote` (
  `user_id` BIGINT NOT NULL,
  `content_id` BIGINT NOT NULL,
  `vote_date` DATETIME NOT NULL,
  `up` BIT(1) NOT NULL,
  PRIMARY KEY (`user_id`, `content_id`),
  INDEX `fk_ContentVote_Content1_idx` (`content_id` ASC),
  CONSTRAINT `fk_ContentVote_User1`
    FOREIGN KEY (`user_id`)
    REFERENCES `incomb`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ContentVote_Content1`
    FOREIGN KEY (`content_id`)
    REFERENCES `incomb`.`content` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `incomb`.`content_comment`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `incomb`.`content_comment` ;

CREATE TABLE IF NOT EXISTS `incomb`.`content_comment` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `content_id` BIGINT NOT NULL,
  `comment_date` DATETIME NOT NULL,
  `comment` MEDIUMTEXT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_ContentComment_Content1_idx` (`content_id` ASC),
  CONSTRAINT `fk_ContentComment_User1`
    FOREIGN KEY (`user_id`)
    REFERENCES `incomb`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ContentComment_Content1`
    FOREIGN KEY (`content_id`)
    REFERENCES `incomb`.`content` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `incomb`.`fly_with`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `incomb`.`fly_with` ;

CREATE TABLE IF NOT EXISTS `incomb`.`fly_with` (
  `user_id` BIGINT NOT NULL,
  `fly_with_id` BIGINT NOT NULL,
  `fly_with_start_date` DATETIME NOT NULL,
  PRIMARY KEY (`user_id`, `fly_with_id`),
  INDEX `fk_User_has_User_User2_idx` (`fly_with_id` ASC),
  INDEX `fk_User_has_User_User1_idx` (`user_id` ASC),
  CONSTRAINT `fk_User_has_User_User1`
    FOREIGN KEY (`user_id`)
    REFERENCES `incomb`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_User_has_User_User2`
    FOREIGN KEY (`fly_with_id`)
    REFERENCES `incomb`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `incomb`.`comb_item`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `incomb`.`comb_item` ;

CREATE TABLE IF NOT EXISTS `incomb`.`comb_item` (
  `user_id` BIGINT NOT NULL,
  `content_id` BIGINT NOT NULL,
  `add_date` DATETIME NOT NULL,
  `read_date` DATETIME NULL,
  PRIMARY KEY (`user_id`, `content_id`),
  INDEX `fk_User_has_Content_Content1_idx` (`content_id` ASC),
  INDEX `fk_User_has_Content_User1_idx` (`user_id` ASC),
  CONSTRAINT `fk_User_has_Content_User1`
    FOREIGN KEY (`user_id`)
    REFERENCES `incomb`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_User_has_Content_Content1`
    FOREIGN KEY (`content_id`)
    REFERENCES `incomb`.`content` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `incomb`.`news`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `incomb`.`news` ;

CREATE TABLE IF NOT EXISTS `incomb`.`news` (
  `content_id` BIGINT NOT NULL,
  `link` MEDIUMTEXT NOT NULL,
  `image_url` MEDIUMTEXT NULL,
  `image_width` INT NULL,
  `image_height` INT NULL,
  `news_group_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`content_id`),
  CONSTRAINT `fk_News_Content1`
    FOREIGN KEY (`content_id`)
    REFERENCES `incomb`.`content` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `incomb`.`user_locale`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `incomb`.`user_locale` ;

CREATE TABLE IF NOT EXISTS `incomb`.`user_locale` (
  `user_id` BIGINT NOT NULL,
  `locale` VARCHAR(5) NOT NULL,
  PRIMARY KEY (`user_id`, `locale`),
  CONSTRAINT `fk_user_locale_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `incomb`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
               
               
-- Indices
alter table incomb.content add index (title);
alter table incomb.content add index (publish_date);
alter table incomb.news add index (link(500));
