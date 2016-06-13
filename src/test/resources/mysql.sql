SET FOREIGN_KEY_CHECKS=0;

  drop table if exists users;
  create table `users`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL,
  `password` VARCHAR(500) NULL,
  `user_name` VARCHAR(255) NULL,
  `mobile` VARCHAR(255) NULL,
  `head_icon` VARCHAR(255) NULL,
  `create_date` DATETIME NULL,
  `gender` VARCHAR(255) NULL,
  `email` VARCHAR(255) NULL,
  PRIMARY KEY (`id`)
  );

  drop table if exists roles;
  create table `roles`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_name` VARCHAR(255) NULL,
  `role_name` VARCHAR(255) NULL,
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
  PRIMARY KEY (`id`)
  );

  drop table if exists buildings;
  create table `buildings`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL,
  `x_location` FLOAT NULL,
  `y_location` FLOAT NULL,
  `device_num` INT NULL,
  `alert_num` INT NULL,
  `create_date` DATETIME NULL
  `company_id` INT NULL,

  );






SET FOREIGN_KEY_CHECKS=1;