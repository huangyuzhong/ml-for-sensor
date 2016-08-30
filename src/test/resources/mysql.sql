SET FOREIGN_KEY_CHECKS=0;

  drop table if exists users;
  create table `users`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL,
  `password` VARCHAR(500) NULL,
  `user_name` VARCHAR(255) NULL,
  `mobile` VARCHAR(255) NULL,
  `telephone` VARCHAR(255) NULL,
  `head_icon` VARCHAR(255) NULL,
  `create_date` DATETIME NULL,
  `gender` VARCHAR(255) NULL,
  `email` VARCHAR(255) NULL,
  `company_id` INT NULL,
  `department` VARCHAR(255) NULL,
  `job` VARCHAR(255) NULL,
  PRIMARY KEY (`id`)
  );

  drop table if exists roles;
  create table `roles`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(255) NULL,
  `authority` VARCHAR(255) NULL,
  `role_auth_id` INT NULL,
  PRIMARY KEY (`id`)
  );

  drop table if exists role_authority;
  create table `role_authority`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL,
  `child_id` INT NULL,
  PRIMARY KEY (`id`)
  );

--  ALTER TABLE `roles` ADD CONSTRAINT `roles_users_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
  ALTER TABLE `roles` ADD CONSTRAINT `roles_2` FOREIGN KEY (`role_auth_id`) REFERENCES `role_authority` (`id`);

  drop table if exists company;
  create table `company`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL,
  `address` VARCHAR(255) NULL,
  `manager_user_id` INT NOT NULL,
  `business_user_id` INT NULL,
  `email` VARCHAR(255) NULL,
  `telephone` VARCHAR(255) NULL,
  `contract_no` VARCHAR(255) NULL,
  `sign_date` DATETIME NULL,
  `contract_end_date` DATETIME NULL,
  `background_url` VARCHAR(255) NULL,
  PRIMARY KEY (`id`)
  );

  ALTER TABLE `company` ADD CONSTRAINT `company_1` FOREIGN KEY (`manager_user_id`) REFERENCES `users` (`id`);
  ALTER TABLE `company` ADD CONSTRAINT `company_2` FOREIGN KEY (`business_user_id`) REFERENCES `users` (`id`);

  drop table if exists buildings;
  create table `buildings`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL,
  `x_location` FLOAT NULL,
  `y_location` FLOAT NULL,
  `device_num` INT NULL DEFAULT 0,
  `create_date` DATETIME NULL,
  `company_id` INT NULL,
  PRIMARY KEY (`id`)
  );

  ALTER TABLE `buildings` ADD CONSTRAINT `buildings_1` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`);

  drop table if exists buildings_daily;
  create table `buildings_daily`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `build_id` INT NULL,
  `start_time` DATETIME NULL,
  `end_time` DATETIME NULL,
  `alert_num` INT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
  );

  ALTER TABLE `buildings_daily` ADD CONSTRAINT `buildings_daily_1` FOREIGN KEY (`build_id`) REFERENCES `buildings` (`id`);

  drop table if exists floors;
  create table `floors` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `floor_num` INT NULL,
  `name` VARCHAR(255) NULL,
  `build_id` INT NOT NULL,
  `device_num` INT NULL DEFAULT 0,
  `create_date` DATETIME NULL,
  PRIMARY KEY (`id`)
  );

  ALTER TABLE `floors` ADD CONSTRAINT `floors_1` FOREIGN KEY (`build_id`) REFERENCES `buildings` (`id`);

  drop table if exists floors_daily;
  create table `floors_daily`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `floor_id` INT NULL,
  `start_time` DATETIME NULL,
  `end_time` DATETIME NULL,
  `alert_num` INT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
  );

  ALTER TABLE `floors_daily` ADD CONSTRAINT `floors_daily_1` FOREIGN KEY (`floor_id`) REFERENCES `floors` (`id`);

  drop table if exists room;
  create table `room`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL,
  `floor_id` INT NULL,
  `x_location` FLOAT NULL,
  `y_location` FLOAT NULL,
  `device_num` INT NULL DEFAULT 0,
  `create_date` DATETIME NULL,
  PRIMARY KEY (`id`)
  );

  ALTER TABLE `room` ADD CONSTRAINT `room_1` FOREIGN KEY (`floor_id`) REFERENCES `floors` (`id`);

  drop table if exists room_daily;
  create table `room_daily`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `room_id` INT NOT NULL,
  `start_time` DATETIME NULL,
  `end_time` DATETIME NULL,
  `alert_num` INT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
  );

  ALTER TABLE `room_daily` ADD CONSTRAINT `room_daily_1` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`);

  drop table if exists inspect_type;
  create table `inspect_type` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL,
  `code` VARCHAR(255) NULL,
  PRIMARY KEY (`id`)
  );

  drop table if exists device_type;
  create table `device_type`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL,
  `logo_url` VARCHAR(255) NULL,
  PRIMARY KEY (`id`)
  );

  drop table if exists device_type_inspect;
  create table `device_type_inspect`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `device_type_id` INT NULL,
  `inspect_type_id` INT NULL,
  PRIMARY KEY (`id`)
  );

  ALTER TABLE `device_type_inspect` ADD CONSTRAINT `device_type_inspect_1` FOREIGN KEY (`device_type_id`) REFERENCES `device_type` (`id`);
  ALTER TABLE `device_type_inspect` ADD CONSTRAINT `device_type_inspect_2` FOREIGN KEY (`inspect_type_id`) REFERENCES `inspect_type` (`id`);


  drop table if exists monitor_device;
  create table `monitor_device`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `number` VARCHAR(255) NULL,
  `battery_status`  VARCHAR(255) NULL,
  `online_status` VARCHAR(255) NULL,
  PRIMARY KEY (`id`)
  );

  drop table if exists device;
  create table `device`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(255) NULL,
  `name` VARCHAR(255) NULL,
  `type_id` INT NULL,
  `create_date` DATETIME NULL,
  `creator` VARCHAR(255) NULL,
  `purchase_date` DATETIME NULL,
  `photo` VARCHAR(255) NULL,
  `manager_user_id` INT NULL,
  `alert_num` INT NULL DEFAULT 0,
  `maintain_rule` VARCHAR(1000) NULL,
  `maintain_date` DATETIME NULL,
  `maintain_alert_days` INT NULL,
  PRIMARY KEY (`id`)
  );

  ALTER TABLE `device` ADD CONSTRAINT `device_1` FOREIGN KEY (`type_id`) REFERENCES `device_type` (`id`);
  ALTER TABLE `device` ADD CONSTRAINT `device_2` FOREIGN KEY (`manager_user_id`) REFERENCES `users` (`id`);

  drop table if exists file;
  create table `file`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `url` VARCHAR(255) NULL,
  `type` VARCHAR(255) NULL,
  `description` VARCHAR(500) NULL,
  `enable` INT NULL DEFAULT 0,
  `create_date` DATETIME NULL,
  PRIMARY KEY (`id`)
  );

  drop table if exists device_file;
  create table `device_file` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `device_id` INT NULL,
  `file_id` INT NULL,
  PRIMARY KEY (`id`)
  );

  ALTER TABLE `device_file` ADD CONSTRAINT `device_file_1` FOREIGN KEY (`device_id`) REFERENCES `device` (`id`);
  ALTER TABLE `device_file` ADD CONSTRAINT `device_file_2` FOREIGN KEY (`file_id`) REFERENCES `file` (`id`);

  drop table if exists device_inspect;
  create table `device_inspect` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `device_id` INT NULL,
  `inspect_type_id` INT NULL,
  `standard` FLOAT NULL,
  `low_up_alert` FLOAT NULL,
  `low_down_alert` FLOAT NULL,
  `high_up_alert` FLOAT NULL,
  `high_down_alert` FLOAT NULL,
  `low_alert_minutes` INT NULL,
  PRIMARY KEY (`id`)
  );

  ALTER TABLE `device_inspect` ADD CONSTRAINT `device_inspect_1` FOREIGN KEY (`device_id`) REFERENCES `device` (`id`);
  ALTER TABLE `device_inspect` ADD CONSTRAINT `device_inspect_2` FOREIGN KEY (`inspect_type_id`) REFERENCES `inspect_type` (`id`);

  drop table if exists inspect_data;
  create table `inspect_data`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `device_id` INT NULL,
  `device_inspect_id` INT NULL,
  `result` INT NULL,
  PRIMARY KEY (`id`)
  );

  ALTER TABLE `inspect_data` ADD CONSTRAINT `inspect_data_1` FOREIGN KEY (`device_id`) REFERENCES `device` (`id`);
  ALTER TABLE `inspect_data` ADD CONSTRAINT `inspect_data_2` FOREIGN KEY (`device_inspect_id`) REFERENCES `device_inspect` (`id`);

  drop table if exists device_floor;
  create table `device_floor`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `device_id` INT NULL,
  `floor_num` INT NULL,
  `user_id` INT NULL,
  `subject_name` VARCHAR(255) NULL,
  `subject_num` INT NULL,
  PRIMARY KEY (`id`)
  );

  ALTER TABLE `device_floor` ADD CONSTRAINT `device_floor_1` FOREIGN KEY (`device_id`) REFERENCES `device` (`id`);
  ALTER TABLE `device_floor` ADD CONSTRAINT `device_floor_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

  drop table if exists monitor_version;
  create table `monitor_version`(
  `id` INT NOT NULL,
  `version` VARCHAR(255) NULL,
  `url` VARCHAR(255) NULL,
  `name` VARCHAR(255) NULL,
  `description` VARCHAR(255) NULL,
  `create_date` DATETIME NULL,
  PRIMARY KEY (`id`)
  );


  ALTER TABLE `device` ADD `room_id` INT NULL;
  ALTER TABLE `monitor_device` ADD `device_id` INT NULL;

  ALTER TABLE `device_floor` DROP FOREIGN kEY `device_floor_2`;
  ALTER TABLE `device_floor` DROP `user_id`;
  ALTER TABLE `device_floor` ADD `scientist` VARCHAR(255);
  ALTER TABLE `device_floor` ADD `scientist_mobile` VARCHAR(255);
  ALTER TABLE `device_floor` ADD `scientist_email` VARCHAR(255);

  ALTER TABLE `inspect_data` ADD `create_date` DATETIME NULL;

  ALTER TABLE `device_inspect` ADD `name` VARCHAR(255) NULL;

  ALTER TABLE `buildings` ADD `background_url` VARCHAR(255) NULL;
  ALTER TABLE `floors` ADD `background_url` VARCHAR(255) NULL;
  ALTER TABLE `room` ADD `background_url` VARCHAR(255) NULL;

  ALTER TABLE `floors` ADD `x_location` FLOAT NULL;
  ALTER TABLE `floors` ADD `y_location` FLOAT NULL;

  ALTER TABLE `users` ADD `job_number` VARCHAR(255) NULL;
  ALTER TABLE `device` ADD `model` VARCHAR(255) NULL;

  ALTER TABLE `device_floor` ADD `product_num` INT NULL;

  ALTER TABLE `device` ADD `x_location` FLOAT NULL;
  ALTER TABLE `device` ADD `y_location` FLOAT NULL;

  ALTER TABLE `company` ADD `create_date` DATETIME NULL;
  ALTER TABLE `room` ADD `device_id` INT NULL;

  ALTER TABLE `role_authority` ADD `role_name` VARCHAR(255) NULL;

  ALTER TABLE `device_type_inspect` ADD `standard` FLOAT NULL;
  ALTER TABLE `device_type_inspect` ADD `low_up_alert` FLOAT NULL;
  ALTER TABLE `device_type_inspect` ADD `low_down_alert` FLOAT NULL;
  ALTER TABLE `device_type_inspect` ADD  `high_up_alert` FLOAT NULL;
  ALTER TABLE `device_type_inspect` ADD `high_down_alert` FLOAT NULL;
  ALTER TABLE `device_type_inspect` ADD `low_alert_minutes` INT NULL;




SET FOREIGN_KEY_CHECKS=1;