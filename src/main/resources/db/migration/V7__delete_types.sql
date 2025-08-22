-- 1. Alterar colunas que usam ENUM para VARCHAR
ALTER TABLE equipment ALTER COLUMN classification TYPE VARCHAR(50) USING classification::text;
ALTER TABLE equipment ALTER COLUMN criticality TYPE VARCHAR(50) USING criticality::text;
ALTER TABLE equipment ALTER COLUMN equipment_status TYPE VARCHAR(50) USING equipment_status::text;

ALTER TABLE maintenance_plan ALTER COLUMN maintenance_type TYPE VARCHAR(50) USING maintenance_type::text;

ALTER TABLE work_order ALTER COLUMN activity_status TYPE VARCHAR(50) USING activity_status::text;
ALTER TABLE work_order ALTER COLUMN priority TYPE VARCHAR(50) USING priority::text;
ALTER TABLE work_order ALTER COLUMN maintenance_type TYPE VARCHAR(50) USING maintenance_type::text;

ALTER TABLE inventory ALTER COLUMN item_type TYPE VARCHAR(50) USING item_type::text;

ALTER TABLE task ALTER COLUMN activity_status TYPE VARCHAR(50) USING activity_status::text;

ALTER TABLE building ALTER COLUMN building_type TYPE VARCHAR(50) USING building_type::text;

-- 2. Apagar os tipos ENUM antigos
DROP TYPE IF EXISTS criticality;
DROP TYPE IF EXISTS equipment_status;
DROP TYPE IF EXISTS classification;
DROP TYPE IF EXISTS activity_status;
DROP TYPE IF EXISTS priority;
DROP TYPE IF EXISTS maintenance_type;
DROP TYPE IF EXISTS item_type;
DROP TYPE IF EXISTS building_type;