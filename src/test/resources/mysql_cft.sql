drop table if exists users;
create table users(
`id` INT NOT null AUTO_INCREMENT,
`account` VARCHAR(255) NULL,
`name` VARCHAR(255) NULL,
`mobile` VARCHAR(255) null,
`telephone` VARCHAR(255) null,
`verify` VARCHAR(500) null,
`password` VARCHAR(500) null,
`create_date` datetime null,
`role` VARCHAR(500) NULL,
PRIMARY KEY (`id`)
);

drop table if exists authority;
create table authority(
`id` INT NOT NULL AUTO_INCREMENT,
`authority_name` int not null,
PRIMARY KEY(`id`)
);

drop table if exists user_authority;
create table user_authority(
`id` int not null AUTO_INCREMENT,
`user_id` int not null,
`authority_id` int not null,
PRIMARY KEY (`id`)
);

drop table if exists customer;
create table customer(
`id` int not null auto_increment,
`user_id` int not null,
`nick_name` VARCHAR(255) null,
`head_url` VARCHAR(500) null,
`shop_id` int null,
`open_id` VARCHAR(255) null,
PRIMARY KEY (`id`)
);

drop table if exists admin;
create table admin(
`id` int not null auto_increment,
`nick_name` VARCHAR(255) null,
`head_url` VARCHAR(500) null,
PRIMARY KEY (`id`)
);

drop table if exists shopper;
create table shopper(
`id` int not null auto_increment,
`nick_name` VARCHAR(255) null,
`head_url` VARCHAR(500) null,
`open_id` VARCHAR(255) null,
PRIMARY KEY (`id`)
);

drop table if exists shop;
create table shop(
`id` int not null auto_increment,
`name` VARCHAR(255) null,
`shopper_id` int null,
`logo` VARCHAR(255) null,
PRIMARY KEY (`id`)
);

drop table if exists service;
create table service(
`id` int not null auto_increment,
`name` VARCHAR(255) null,
`suggest_price` decimal null,
`description` VARCHAR(1000) null,
`create_date` datetime null,
PRIMARY KEY (`id`)
);

drop table if exists shop_service;
create table shop_service(
`id` int not null auto_increment,
`shop_id` int not null,
`service_id` int not null,
`original_price` decimal null,
`create_date` datetime null,
PRIMARY KEY (`id`)
);



drop table if exists
