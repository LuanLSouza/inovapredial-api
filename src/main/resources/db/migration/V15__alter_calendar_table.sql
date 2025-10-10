-- Alterar tabela calendar para permitir seleção flexível de dias da semana
-- e horários compartilhados

-- Adicionar colunas para os dias da semana
ALTER TABLE calendar ADD COLUMN monday BOOLEAN DEFAULT FALSE;
ALTER TABLE calendar ADD COLUMN tuesday BOOLEAN DEFAULT FALSE;
ALTER TABLE calendar ADD COLUMN wednesday BOOLEAN DEFAULT FALSE;
ALTER TABLE calendar ADD COLUMN thursday BOOLEAN DEFAULT FALSE;
ALTER TABLE calendar ADD COLUMN friday BOOLEAN DEFAULT FALSE;
ALTER TABLE calendar ADD COLUMN saturday BOOLEAN DEFAULT FALSE;
ALTER TABLE calendar ADD COLUMN sunday BOOLEAN DEFAULT FALSE;

-- Adicionar coluna para indicar se há pausa
ALTER TABLE calendar ADD COLUMN has_break BOOLEAN DEFAULT FALSE;

-- Alterar tipo das colunas de tempo de TIMESTAMP para TIME
-- Primeiro, criar novas colunas temporárias
ALTER TABLE calendar ADD COLUMN start_time_new TIME;
ALTER TABLE calendar ADD COLUMN end_time_new TIME;

-- Copiar dados existentes (convertendo TIMESTAMP para TIME)
UPDATE calendar SET start_time_new = start_time::TIME WHERE start_time IS NOT NULL;
UPDATE calendar SET end_time_new = end_time::TIME WHERE end_time IS NOT NULL;

-- Remover colunas antigas
ALTER TABLE calendar DROP COLUMN start_time;
ALTER TABLE calendar DROP COLUMN end_time;

-- Renomear colunas novas para nomes originais
ALTER TABLE calendar RENAME COLUMN start_time_new TO start_time;
ALTER TABLE calendar RENAME COLUMN end_time_new TO end_time;

