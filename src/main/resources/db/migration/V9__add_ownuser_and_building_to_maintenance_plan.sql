ALTER TABLE maintenance_plan
ADD COLUMN ownuser_id UUID,
ADD COLUMN building_id UUID;

ALTER TABLE maintenance_plan
ADD CONSTRAINT fk_maintenance_plan_ownuser
FOREIGN KEY (ownuser_id) REFERENCES ownuser(id);

ALTER TABLE maintenance_plan
ADD CONSTRAINT fk_maintenance_plan_building
FOREIGN KEY (building_id) REFERENCES building(id);