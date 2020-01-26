CREATE TABLE `projects` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `browsers` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT NOT NULL,
  `client` VARCHAR(10) NOT NULL,
  `width` INT NOT NULL,
  `height` INT NOT NULL,
  `additional_config` TEXT NULL,
  PRIMARY KEY (`id`)
);
