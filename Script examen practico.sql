CREATE SCHEMA IF NOT EXISTS sap;
SET search_path TO sap, public;


-- Usuarios
CREATE TABLE IF NOT EXISTS sap.users (
id BIGSERIAL PRIMARY KEY,
username VARCHAR(150) NOT NULL UNIQUE,
password VARCHAR(120) NOT NULL,
role VARCHAR(50) NOT NULL DEFAULT 'ROLE_USER',
enabled BOOLEAN NOT NULL DEFAULT TRUE,
created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);


-- Cuentas
CREATE TABLE IF NOT EXISTS sap.accounts (
id BIGSERIAL PRIMARY KEY,
user_id BIGINT NOT NULL REFERENCES sap.users(id) ON DELETE CASCADE,
name VARCHAR(120) NOT NULL,
currency CHAR(3) NOT NULL,
balance NUMERIC(15,2) NOT NULL DEFAULT 0.00,
created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_accounts_user ON sap.accounts(user_id);


-- Conceptos de gasto
CREATE TABLE IF NOT EXISTS sap.expense_concepts (
id BIGSERIAL PRIMARY KEY,
user_id BIGINT NOT NULL REFERENCES sap.users(id) ON DELETE CASCADE,
name VARCHAR(120) NOT NULL,
created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_concepts_user ON sap.expense_concepts(user_id);
-- (Opcional) Un concepto único por usuario:
-- ALTER TABLE sap.expense_concepts ADD CONSTRAINT uq_concept_per_user UNIQUE (user_id, name);


-- Transacciones
-- type: DEPOSIT, WITHDRAWAL, TRANSFER, EXPENSE
CREATE TABLE IF NOT EXISTS sap.transactions (
id BIGSERIAL PRIMARY KEY,
user_id BIGINT NOT NULL REFERENCES sap.users(id) ON DELETE CASCADE,
source_account_id BIGINT REFERENCES sap.accounts(id) ON DELETE SET NULL,
dest_account_id BIGINT REFERENCES sap.accounts(id) ON DELETE SET NULL,
concept_id BIGINT REFERENCES sap.expense_concepts(id) ON DELETE SET NULL,
type VARCHAR(20) NOT NULL CHECK (type IN ('DEPOSIT','WITHDRAWAL','TRANSFER','EXPENSE')),
amount NUMERIC(15,2) NOT NULL,
currency CHAR(3) NOT NULL,
occurred_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
description VARCHAR(300)
);
CREATE INDEX IF NOT EXISTS idx_tx_user ON sap.transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_tx_type ON sap.transactions(type);
CREATE INDEX IF NOT EXISTS idx_tx_src ON sap.transactions(source_account_id);
CREATE INDEX IF NOT EXISTS idx_tx_dst ON sap.transactions(dest_account_id);
CREATE INDEX IF NOT EXISTS idx_tx_concept ON sap.transactions(concept_id);

-- Usuario demo (hash BCrypt de "123456")
INSERT INTO sap.users(username, password, role, enabled)
VALUES ('admin@example.com', '$2a$10$S1hW6Vh5WPRcJ3zPlfYvOe9x0o3rC9E8v3DqN9nQh8oJ7dXeB9N2e', 'ROLE_USER', TRUE)
ON CONFLICT (username) DO NOTHING;


-- Cuentas demo
WITH u AS (SELECT id FROM sap.users WHERE username='admin@example.com')
INSERT INTO sap.accounts(user_id, name, currency, balance)
SELECT u.id, v.name, 'MXN', 0.00 FROM u CROSS JOIN (VALUES ('Cuenta Nómina'),('Ahorros')) AS v(name)
ON CONFLICT DO NOTHING;


-- Conceptos demo
WITH u AS (SELECT id FROM sap.users WHERE username='admin@example.com')
INSERT INTO sap.expense_concepts(user_id, name)
SELECT u.id, v.name FROM u CROSS JOIN (VALUES ('Renta'),('Comida'),('Transporte'),('Servicios')) AS v(name)
ON CONFLICT DO NOTHING;


-- Transacciones: transferencias (pie por cuenta destino)
WITH u AS (SELECT id AS uid FROM sap.users WHERE username='admin@example.com'),
nom AS (SELECT id AS id_nom FROM sap.accounts a, u WHERE a.user_id=u.uid AND a.name='Cuenta Nómina'),
aho AS (SELECT id AS id_aho FROM sap.accounts a, u WHERE a.user_id=u.uid AND a.name='Ahorros')
INSERT INTO sap.transactions (user_id, source_account_id, dest_account_id, type, amount, currency, occurred_at, description)
SELECT u.uid, nom.id_nom, aho.id_aho, 'TRANSFER', 1500.00, 'MXN', NOW() - INTERVAL '7 days', 'Transferencia a ahorros' FROM u, nom, aho
UNION ALL
SELECT u.uid, nom.id_nom, aho.id_aho, 'TRANSFER', 800.00, 'MXN', NOW() - INTERVAL '3 days', 'Transferencia a ahorros' FROM u, nom, aho
UNION ALL
SELECT u.uid, aho.id_aho, nom.id_nom, 'TRANSFER', 600.00, 'MXN', NOW() - INTERVAL '1 day', 'Reversa a nómina' FROM u, nom, aho;


-- Transacciones: gastos (para top 3 por concepto)
WITH u AS (SELECT id AS uid FROM sap.users WHERE username='admin@example.com'),
nom AS (SELECT id AS id_nom FROM sap.accounts a, u WHERE a.user_id=u.uid AND a.name='Cuenta Nómina'),
c_renta AS (SELECT id AS cid FROM sap.expense_concepts c, u WHERE c.user_id=u.uid AND c.name='Renta'),
c_comida AS (SELECT id AS cid FROM sap.expense_concepts c, u WHERE c.user_id=u.uid AND c.name='Comida'),
c_serv AS (SELECT id AS cid FROM sap.expense_concepts c, u WHERE c.user_id=u.uid AND c.name='Servicios'),
c_trans AS (SELECT id AS cid FROM sap.expense_concepts c, u WHERE c.user_id=u.uid AND c.name='Transporte')
INSERT INTO sap.transactions (user_id, source_account_id, concept_id, type, amount, currency, occurred_at, description)
SELECT u.uid, nom.id_nom, c_renta.cid, 'EXPENSE', 5000.00, 'MXN', NOW() - INTERVAL '10 days', 'Pago de renta' FROM u, nom, c_renta
UNION ALL
SELECT u.uid, nom.id_nom, c_comida.cid, 'EXPENSE', 1200.00, 'MXN', NOW() - INTERVAL '2 days', 'Super' FROM u, nom, c_comida
UNION ALL
SELECT u.uid, nom.id_nom, c_comida.cid, 'EXPENSE', 800.00, 'MXN', NOW() - INTERVAL '1 day', 'Cena' FROM u, nom, c_comida
UNION ALL
SELECT u.uid, nom.id_nom, c_serv.cid, 'EXPENSE', 700.00, 'MXN', NOW() - INTERVAL '4 days', 'Luz/Agua' FROM u, nom, c_serv
UNION ALL
SELECT u.uid, nom.id_nom, c_trans.cid, 'EXPENSE', 300.00, 'MXN', NOW() - INTERVAL '5 days', 'Metro/Uber' FROM u, nom, c_trans;