ALTER TABLE "work_order"
ADD COLUMN IF NOT EXISTS "building_id" UUID;

ALTER TABLE "work_order"
ADD CONSTRAINT "fk_work_order_building"
FOREIGN KEY ("building_id") REFERENCES "building"("id");