-- Remove a foreign key da coluna plan_id em work_order
ALTER TABLE "work_order" DROP CONSTRAINT IF EXISTS "work_order_plan_id_fkey";

-- Remove a coluna plan_id da tabela work_order
ALTER TABLE "work_order" DROP COLUMN IF EXISTS "plan_id";