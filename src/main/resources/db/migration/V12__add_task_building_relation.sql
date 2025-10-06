ALTER TABLE "task"
ADD COLUMN IF NOT EXISTS "building_id" UUID;

ALTER TABLE "task"
ADD CONSTRAINT "fk_task_building"
FOREIGN KEY ("building_id") REFERENCES "building"("id");