CREATE OR REPLACE PROCEDURE update_budget_type() AS $$
BEGIN
UPDATE budget SET type = 'Расход' WHERE type = 'Комиссия';
END;
$$ LANGUAGE plpgsql;