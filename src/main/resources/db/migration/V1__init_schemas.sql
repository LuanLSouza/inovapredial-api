CREATE TYPE "criticality" AS ENUM (
	'HIGH',
	'MEDIUM',
	'LOW'
);

CREATE TYPE "equipment_status" AS ENUM (
	'ACTIVE',
	'INACTIVE',
	'UNDER_MAINTENANCE'
);

CREATE TYPE "classification" AS ENUM (
	'COMPONENT',
	'EQUIPMENT'
);

CREATE TYPE "activity_status" AS ENUM (
	'OPEN',
	'IN_PROGRESS',
	'COMPLETED',
	'CANCELLED'
);

CREATE TYPE "priority" AS ENUM (
	'LOW',
	'MEDIUM',
	'HIGH',
	'URGENT'
);

CREATE TYPE "maintenance_type" AS ENUM (
	'CORRECTIVE',
	'PREVENTIVE',
	'PREDICTIVE'
);

CREATE TYPE "item_type" AS ENUM (
	'MATERIAL',
	'PART'
);

CREATE TYPE "building_type" AS ENUM (
	'RESIDENTIAL',
	'COMMERCIAL',
	'INDUSTRIAL',
	'MIXED',
	'OTHER'
);

CREATE TABLE "adress" (
	"id" UUID NOT NULL UNIQUE,
	"street" VARCHAR(255) NOT NULL,
	"number" INTEGER NOT NULL,
	"district" VARCHAR(255),
	"city" VARCHAR(255) NOT NULL,
	"state" VARCHAR(2),
	"zip_code" VARCHAR(10),
	PRIMARY KEY("id")
);




CREATE TABLE "calendar" (
	"id" UUID NOT NULL UNIQUE,
	"description" VARCHAR(50),
	"start_time" TIME,
	"end_time" TIME,
	PRIMARY KEY("id")
);




CREATE TABLE "equipment" (
	"id" UUID NOT NULL UNIQUE,
	"identification" VARCHAR(100) NOT NULL,
	"serial_number" VARCHAR(50),
	"classification" CLASSIFICATION NOT NULL,
	"location" VARCHAR(255),
	"criticality" CRITICALITY NOT NULL,
	"purchase_date" DATE,
	"warranty_end_date" DATE,
	"price" NUMERIC(10,2),
	"equipment_status" EQUIPMENT_STATUS NOT NULL,
	"image_url" VARCHAR(255),
	"group" VARCHAR(50),
	"model" VARCHAR(100),
	"cost_center" VARCHAR(100),
	"operation_calendar_id" UUID NOT NULL,
	"user_id" UUID,
	"building_id" UUID NOT NULL,
	PRIMARY KEY("id")
);




CREATE TABLE "maintenance_plan" (
	"id" UUID NOT NULL UNIQUE,
	"frequency_days" INTEGER NOT NULL,
	"requires_shutdown" BOOLEAN NOT NULL,
	"description" VARCHAR(255),
	"maintenance_type" MAINTENANCE_TYPE NOT NULL,
	PRIMARY KEY("id")
);




CREATE TABLE "equipment_plan" (
	"equipment_id" UUID NOT NULL,
	"plan_id" UUID NOT NULL,
	"start_date" DATE,
	"next_due_date" DATE,
	PRIMARY KEY("equipment_id", "plan_id")
);




CREATE TABLE "user" (
	"id" UUID NOT NULL UNIQUE,
	"username" VARCHAR(255) NOT NULL UNIQUE,
	"password" VARCHAR(300) NOT NULL,
	"email" VARCHAR(255) NOT NULL,
	"role" VARCHAR(50),
	PRIMARY KEY("id")
);




CREATE TABLE "work_order" (
	"id" UUID NOT NULL UNIQUE,
	"description" VARCHAR(255) NOT NULL,
	"opening_date" TIMESTAMP NOT NULL,
	"closing_date" TIMESTAMP,
	"activity_status" ACTIVITY_STATUS NOT NULL,
	"priority" PRIORITY,
	"maintenance_type" MAINTENANCE_TYPE NOT NULL,
	"total_cost" NUMERIC(10,2),
	"equipment_id" UUID NOT NULL,
	"employee_id" UUID,
	"plan_id" UUID,
	PRIMARY KEY("id")
);




CREATE TABLE "employee" (
	"id" UUID NOT NULL UNIQUE,
	"name" VARCHAR(255) NOT NULL,
	"specialty" VARCHAR(255),
	"contact" VARCHAR(255) NOT NULL,
	"operation_calendar" UUID,
	"building_id" UUID NOT NULL,
	PRIMARY KEY("id")
);




CREATE TABLE "inventory" (
	"id" UUID NOT NULL UNIQUE,
	"item_type" ITEM_TYPE,
	"name" VARCHAR(255) NOT NULL,
	"cost" NUMERIC(10,2),
	"quantity" INTEGER,
	"minimum_quantity" INTEGER,
	"employee_id" UUID,
	"building_id" UUID NOT NULL,
	PRIMARY KEY("id")
);




CREATE TABLE "task" (
	"id" UUID NOT NULL UNIQUE,
	"title" VARCHAR(255) NOT NULL,
	"description" VARCHAR(1000),
	"activity_status" ACTIVITY_STATUS,
	"estimated_time" INTEGER,
	"start_date" TIMESTAMP,
	"end_date" TIMESTAMP,
	"time_spent" INTEGER,
	"cost" NUMERIC(10,2),
	"work_order_id" UUID,
	"employee_id" UUID,
	PRIMARY KEY("id")
);




CREATE TABLE "work_order_inventory" (
	"work_order_id" UUID NOT NULL,
	"inventory_id" UUID NOT NULL,
	"quantity" INTEGER NOT NULL,
	"total_cost" DECIMAL(10,2),
	"output_date" TIMESTAMP,
	PRIMARY KEY("work_order_id", "inventory_id")
);




CREATE TABLE "building" (
	"id" UUID NOT NULL UNIQUE,
	"name" VARCHAR(255) NOT NULL,
	"building_type" BUILDING_TYPE,
	"construction_year" INTEGER,
	"description" VARCHAR(255),
	"address_id" UUID,
	PRIMARY KEY("id")
);




CREATE TABLE "user_building" (
	"user_id" UUID NOT NULL UNIQUE,
	"building_id" UUID NOT NULL UNIQUE,
	PRIMARY KEY("user_id", "building_id")
);