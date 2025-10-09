-- Adiciona a coluna is_realized
ALTER TABLE equipment_plan
ADD COLUMN IF NOT EXISTS is_realized BOOLEAN DEFAULT FALSE;

-- Adiciona a coluna building_id e a chave estrangeira
ALTER TABLE equipment_plan
ADD COLUMN IF NOT EXISTS "building_id" UUID;

ALTER TABLE equipment_plan
ADD CONSTRAINT fk_equipment_plan_building
FOREIGN KEY (building_id) REFERENCES building(id);