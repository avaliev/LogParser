create database ApacheInspector;
use ApacheInspector;

CREATE TABLE `apacheinspector`.`settings` (
  `key` VARCHAR(20) NOT NULL,
  `value` VARCHAR(45) NULL,
  PRIMARY KEY (`key`));

CREATE TABLE `ApacheInspector`.`logs` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `ip` VARCHAR(45) NULL,
  `user_name` VARCHAR(45) NULL,
  `date_time` DATETIME NULL,
  `resource_url` VARCHAR(100) NULL COMMENT 'requested resource url',
  `status_code` INT NULL COMMENT 'HTTP status code',
  `response_size` INT NULL COMMENT 'response size in bytes',
  `user_agent` VARCHAR(200) NULL,
  PRIMARY KEY (`id`))
COMMENT = 'apache httpd server logs';

CREATE TABLE `ApacheInspector`.`event_types` (
  `id` INT NOT NULL,
  `event_name` VARCHAR(45) NULL,
  PRIMARY KEY (`id`));
  
  CREATE TABLE `apacheinspector`.`settings` (
  `key_col` VARCHAR(20) NOT NULL,
  `value_col` VARCHAR(45) NULL,
  PRIMARY KEY (`key_col`));


insert into event_types values(1,'suspicious_address');
insert into event_types values(2,'overload');

SET SQL_SAFE_UPDATES = 0;

CREATE TABLE `ApacheInspector`.`events` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `event_type` INT NULL,
  `date_time` DATETIME NOT NULL,
  `ip_address` VARCHAR(45) NULL,
  `description` VARCHAR(100) NULL,
  PRIMARY KEY (`id`),
  INDEX `event_type_idx` (`event_type` ASC),
  CONSTRAINT `fk_events_event_type`
    FOREIGN KEY (`event_type`)
    REFERENCES `event_types` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT);
	
INSERT INTO `apacheinspector`.`settings` (`key_col`, `value_col`) VALUES ('program_path', '/opt/ApacheInspector');
INSERT INTO `apacheinspector`.`settings` (`key_col`, `value_col`) VALUES ('max_req_per_second', '10');
INSERT INTO `apacheinspector`.`settings` (`key_col`, `value_col`) VALUES ('max_req_for_ip', '1');
INSERT INTO `apacheinspector`.`settings` (`key_col`, `value_col`) VALUES ('check_period', '60');
INSERT INTO `apacheinspector`.`settings` (`key_col`, `value_col`) VALUES ('apache_logfile_path', 'access.log');
