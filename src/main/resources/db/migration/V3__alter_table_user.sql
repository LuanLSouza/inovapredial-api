ALTER TABLE "user" RENAME TO "ownuser";

ALTER TABLE "user_building" DROP CONSTRAINT IF EXISTS "user_building_user_id_fkey";

ALTER TABLE "user_building" RENAME COLUMN "user_id" TO "ownuser_id";

ALTER TABLE "user_building"
ADD CONSTRAINT "user_building_ownuser_id_fkey"
FOREIGN KEY("ownuser_id") REFERENCES "ownuser"("id")
ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "equipment" DROP CONSTRAINT IF EXISTS "equipment_user_id_fkey";

ALTER TABLE "equipment" RENAME COLUMN "user_id" TO "ownuser_id";

ALTER TABLE "equipment"
ADD CONSTRAINT "equipment_ownuser_id_fkey"
FOREIGN KEY("ownuser_id") REFERENCES "ownuser"("id")
ON UPDATE CASCADE ON DELETE CASCADE;