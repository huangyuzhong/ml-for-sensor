SET FOREIGN_KEY_CHECKS=0;

  drop table if exists users;
  create table `users`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_name` VARCHAR(255) NULL,
  `password` VARCHAR(500) NULL,
  `mobile` VARCHAR(255) NULL,
  `head_icon` VARCHAR(255) NULL,
  PRIMARY KEY (`id`)
  );

  drop table if exists roles;
  create table `roles`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_name` VARCHAR(255) NULL,
  `authority` VARCHAR(255) NULL,
  `role_auth` INT NULL,
  PRIMARY KEY (`id`)
  );

  drop table if exists role_authority;
  create table `role_authority`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL,
  `child_id` INT NULL,
  PRIMARY KEY (`id`)
  );

  drop table if exists buildings;
  create table `buildings`(

  );






SET FOREIGN_KEY_CHECKS=1;