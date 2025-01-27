CREATE TABLE `area` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `firstName` varchar(255) NOT NULL,
  `lastName` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phoneNumber` varchar(255) NOT NULL,
  `userName` varchar(255) NOT NULL,
  `userType` enum('ADMIN','PROPERTY_OWNER') NOT NULL,
  `isActive` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_user_email` (`email`),
  UNIQUE KEY `UK_user_phoneNumber` (`phoneNumber`),
  UNIQUE KEY `UK_user_userName` (`userName`)
);

CREATE TABLE `property` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `areaId` bigint DEFAULT NULL,
  `userId` bigint DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `bathrooms` int NOT NULL,
  `bedrooms` int NOT NULL,
  `floor` int NOT NULL,
  `price` double NOT NULL,
  `squareMeters` double NOT NULL,
  `yearOfConstruction` int NOT NULL,
  `energyClass` enum('Aplus','A','Bplus','B','C','D','E','Z','H') NOT NULL,
  `propertyCondition` enum('NEWLY_BUILT','UNDER_CONSTRUCTION','RENOVATED','NEEDS_RENOVATION') NOT NULL,
  `propertyType` enum('APARTMENT','MAISONETTE','DETACHED_HOUSE','BUILDING','PENTHOUSE','FULL_FLOOR_APARTMENT') NOT NULL,
  `image` longtext,
  `propertyUse` enum('SALE','RENT') NOT NULL,
  `createdat` datetime(6) NOT NULL,
  `isActive` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `property_areaId` (`areaId`),
  KEY `property_userId` (`userId`),
  CONSTRAINT `FK_property_userId` FOREIGN KEY (`userId`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_property_areaId` FOREIGN KEY (`areaId`) REFERENCES `area` (`id`)
);
