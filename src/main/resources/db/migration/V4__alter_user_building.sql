
ALTER TABLE "user_building" RENAME TO "ownuser_building";

ALTER TABLE "ownuser_building" DROP CONSTRAINT IF EXISTS "ownuser_building_user_id_fkey";

ALTER TABLE "ownuser_building"
ADD CONSTRAINT "ownuser_building_ownuser_id_fkey"
FOREIGN KEY("ownuser_id") REFERENCES "ownuser"("id")
ON UPDATE CASCADE ON DELETE CASCADE;