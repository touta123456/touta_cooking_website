CREATE DATABASE touta_cooking;
CREATE USER olivier WITH PASSWORD 'olivier';
GRANT ALL PRIVILEGES ON DATABASE touta_cooking TO olivier;

-- Connexion à la base concernée
\c touta_cooking

-- Accorder les droits sur le schéma public
GRANT ALL PRIVILEGES ON SCHEMA public TO olivier;
