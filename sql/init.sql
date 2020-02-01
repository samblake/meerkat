CREATE TABLE `projects` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT NOT NULL,
  `base` VARCHAR(255) NOT NULL,
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

CREATE TABLE `cases` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT NOT NULL,
  `project` INT NOT NULL,
  `path` TEXT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_cases_project_id` FOREIGN KEY (`project`) REFERENCES projects(id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE `scenarios` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT NOT NULL,
  `project` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_scenarios_project_id` FOREIGN KEY (`project`) REFERENCES projects(id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE scenario_cases (
  `id` INT NOT NULL AUTO_INCREMENT,
  `scenario` INT NOT NULL,
  `case` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_scenario_cases_scenario_id` FOREIGN KEY (`scenario`) REFERENCES scenarios(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_scenario_cases_case_id` FOREIGN KEY (`case`) REFERENCES cases(id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE runs (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE results (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT NOT NULL,
  `run` INT NOT NULL,
  `case` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_results_run_id` FOREIGN KEY (`run`) REFERENCES runs(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_results_case_id` FOREIGN KEY (`case`) REFERENCES cases(id) ON DELETE RESTRICT ON UPDATE RESTRICT
);