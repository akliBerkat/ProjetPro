--CREATE DATABASE NOISEBOOK;


CREATE TABLE IF NOT EXISTS Utilisateur(
   Id_Utilisateur SERIAL PRIMARY KEY,
   username_ VARCHAR(50) NOT NULL,
   adressemail VARCHAR(50) NOT NULL,
   mdp VARCHAR(50),
   contact VARCHAR(50),
   UNIQUE(adressemail)
);

CREATE TABLE IF NOT EXISTS Artistes(
   Id_Utilisateur INT,
   groupe BOOLEAN NOT NULL,
   PRIMARY KEY(Id_Utilisateur),
   FOREIGN KEY(Id_Utilisateur) REFERENCES Utilisateur(Id_Utilisateur)
);

CREATE TABLE IF NOT EXISTS Association(
   Id_Utilisateur INT,
   cause VARCHAR(50),
   PRIMARY KEY(Id_Utilisateur),
   FOREIGN KEY(Id_Utilisateur) REFERENCES Utilisateur(Id_Utilisateur)
);

CREATE TABLE IF NOT EXISTS Playlist(
   Id_Playlist SERIAL,
   nom_playlist VARCHAR(50),
   auteur_album INT default NULL,
   PRIMARY KEY(Id_Playlist),
   FOREIGN KEY(auteur_album) REFERENCES Artistes(Id_Utilisateur)
);

CREATE TABLE IF NOT EXISTS Morceau(
   Id_Morceau SERIAL,
   titre VARCHAR(50) NOT NULL,
   PRIMARY KEY(Id_Morceau)
);

CREATE TABLE IF NOT EXISTS Publication(
   Id_Publication SERIAL,
   texte VARCHAR(50),
   date_publication timestamp,
   Id_Utilisateur INT NOT NULL,
   PRIMARY KEY(Id_Publication),
   FOREIGN KEY(Id_Utilisateur) REFERENCES Utilisateur(Id_Utilisateur)
);

CREATE TABLE IF NOT EXISTS Tag(
   Id_Tag SERIAL,
   nom_tag VARCHAR(50),
   PRIMARY KEY(Id_Tag)
);

CREATE TABLE IF NOT EXISTS Medias(
   Id_Medias SERIAL,
   lien_vers_media VARCHAR(200) NOT NULL,
   type VARCHAR(50) NOT NULL,
   PRIMARY KEY(Id_Medias)
);

CREATE TABLE IF NOT EXISTS Historique(
   Id_Historique SERIAL,
   type_activité VARCHAR(55),
   id_activite VARCHAR(50),
   date_activite timestamp,
   PRIMARY KEY(Id_Historique)
);

CREATE TABLE IF NOT EXISTS pays_(
   code INT,
   nom VARCHAR(50) NOT NULL,
   PRIMARY KEY(code),
   UNIQUE(nom)
);

CREATE TABLE IF NOT EXISTS personne(
   Id_Utilisateur INT NOT NULL,
   nom VARCHAR(50) NOT NULL,
   prenom VARCHAR(50) NOT NULL,
   PRIMARY KEY(Id_Utilisateur),
   FOREIGN KEY(Id_Utilisateur) REFERENCES Utilisateur(Id_Utilisateur)
);

CREATE TABLE IF NOT EXISTS Genre_musical(
   Id_Gm SERIAL,
   nom_genre VARCHAR(50) NOT NULL,
   Id_Tag INT,
   Id_Gm_genre_parent INT,
   PRIMARY KEY(Id_Gm),
   UNIQUE(nom_genre),
   FOREIGN KEY(Id_Tag) REFERENCES Tag(Id_Tag),
   FOREIGN KEY(Id_Gm_genre_parent) REFERENCES Genre_musical(Id_Gm)
);

CREATE TABLE IF NOT EXISTS Lieu(
   code INT,
   adresse VARCHAR(50),
   ville VARCHAR(50),
   PRIMARY KEY(code, adresse, ville),
   FOREIGN KEY(code) REFERENCES pays_(code)
);

CREATE TABLE IF NOT EXISTS Concert(
   Id_Concert SERIAL,
   nom_concert VARCHAR(100) UNIQUE,
   salle_concert VARCHAR(100),
   date_event timestamp,
   nb_place INT,
   prix_place NUMERIC NOT NULL,
   besoin_volentaire INT NOT NULL,
   en_exterieur BOOLEAN NOT NULL,
   lineUp VARCHAR(50),
   pour_enfants BOOLEAN NOT NULL,
   cause_de_soutien VARCHAR(200),
   descriptif VARCHAR(50),
   Id_Utilisateur INT NOT NULL,
   code INT NOT NULL,
   adresse VARCHAR(50) NOT NULL,
   ville VARCHAR(50) NOT NULL,
   PRIMARY KEY(Id_Concert),
   FOREIGN KEY(Id_Utilisateur) REFERENCES Utilisateur(Id_Utilisateur),
   FOREIGN KEY(code, adresse, ville) REFERENCES Lieu(code, adresse, ville)
);

CREATE TABLE IF NOT EXISTS archive_concert(
   Id_Concert INT,
   Id_archive_concert SERIAL,
   details VARCHAR(50),
   nb_participants INT,
   PRIMARY KEY(Id_Concert, Id_archive_concert),
   FOREIGN KEY(Id_Concert) REFERENCES Concert(Id_Concert)
);


CREATE TABLE IF NOT EXISTS Avis(
   Id_Avis SERIAL,
   date_publication timestamp,
   commentaire VARCHAR(100),
   note INT CHECK( note>0 and note<6),
   Id_Utilisateur INT NOT NULL,
   Id_Publication INT DEFAULT NULL,
   Id_Concert INT DEFAULT NULL,
   PRIMARY KEY(Id_Avis),
   FOREIGN KEY(Id_Utilisateur) REFERENCES Utilisateur(Id_Utilisateur) ON DELETE CASCADE,
   FOREIGN KEY(Id_Publication) REFERENCES Publication(Id_Publication) ON DELETE CASCADE,
   FOREIGN KEY(Id_Concert) REFERENCES Concert(Id_Concert) ON DELETE CASCADE,
   CHECK ((Id_Publication IS NULL AND  Id_Concert IS NOT NULL) OR  (Id_Publication IS NOT NULL AND  Id_Concert IS NULL) )

);


CREATE TABLE IF NOT EXISTS follow(
   Id_Utilisateur_followed INT,
   Id_Utilisateur_follows INT,
   PRIMARY KEY(Id_Utilisateur_followed, Id_Utilisateur_follows),
   FOREIGN KEY(Id_Utilisateur_followed) REFERENCES Utilisateur(Id_Utilisateur) ON DELETE CASCADE,
   FOREIGN KEY(Id_Utilisateur_follows) REFERENCES Utilisateur(Id_Utilisateur)ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS afficher(
   Id_Utilisateur INT,
   Id_Playlist INT,
   PRIMARY KEY(Id_Utilisateur, Id_Playlist),
   FOREIGN KEY(Id_Utilisateur) REFERENCES Utilisateur(Id_Utilisateur) ON DELETE CASCADE, 
   FOREIGN KEY(Id_Playlist) REFERENCES Playlist(Id_Playlist) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS contenir(
   Id_Playlist INT NOT NULL,
   Id_Morceau INT NOT NULL,
   PRIMARY KEY(Id_Playlist, Id_Morceau),
   FOREIGN KEY(Id_Playlist) REFERENCES Playlist(Id_Playlist) ON DELETE CASCADE,
   FOREIGN KEY(Id_Morceau) REFERENCES Morceau(Id_Morceau)ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS appartient_genre(
   Id_Morceau INT,
   Id_Gm INT,
   PRIMARY KEY(Id_Morceau, Id_Gm) ,
   FOREIGN KEY(Id_Morceau) REFERENCES Morceau(Id_Morceau) ON DELETE CASCADE,
   FOREIGN KEY(Id_Gm) REFERENCES Genre_musical(Id_Gm)ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS auteur(
   Id_Utilisateur INT,
   Id_Morceau INT,
   PRIMARY KEY(Id_Utilisateur, Id_Morceau),
   FOREIGN KEY(Id_Utilisateur) REFERENCES Artistes(Id_Utilisateur)ON DELETE CASCADE,
   FOREIGN KEY(Id_Morceau) REFERENCES Morceau(Id_Morceau)ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS organise(
   Id_Utilisateur INT,
   Id_Concert INT,
   PRIMARY KEY(Id_Utilisateur, Id_Concert),
   FOREIGN KEY(Id_Utilisateur) REFERENCES Association(Id_Utilisateur) ON DELETE CASCADE,
   FOREIGN KEY(Id_Concert) REFERENCES Concert(Id_Concert)ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS participer(
   Id_Utilisateur INT,
   Id_Concert INT,
   PRIMARY KEY(Id_Utilisateur, Id_Concert),
   FOREIGN KEY(Id_Utilisateur) REFERENCES Utilisateur(Id_Utilisateur),
   FOREIGN KEY(Id_Concert) REFERENCES Concert(Id_Concert)
);

CREATE TABLE IF NOT EXISTS interessé_par(
   Id_Utilisateur INT,
   Id_Concert INT,
   PRIMARY KEY(Id_Utilisateur, Id_Concert),
   FOREIGN KEY(Id_Utilisateur) REFERENCES Utilisateur(Id_Utilisateur) ON DELETE CASCADE,
   FOREIGN KEY(Id_Concert) REFERENCES Concert(Id_Concert)ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS performer(
   Id_Utilisateur INT,
   Id_Concert INT,
   PRIMARY KEY(Id_Utilisateur, Id_Concert),
   FOREIGN KEY(Id_Utilisateur) REFERENCES Artistes(Id_Utilisateur) ON DELETE CASCADE,
   FOREIGN KEY(Id_Concert) REFERENCES Concert(Id_Concert)ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS contenir_media(
   Id_Concert INT,
   Id_archive_concert INT,
   Id_Medias INT,
   PRIMARY KEY(Id_Concert, Id_archive_concert, Id_Medias),
   FOREIGN KEY(Id_Concert, Id_archive_concert) REFERENCES archive_concert(Id_Concert, Id_archive_concert) ON DELETE CASCADE,
   FOREIGN KEY(Id_Medias) REFERENCES Medias(Id_Medias)ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS enregister_activite(
   Id_Utilisateur INT,
   Id_Historique INT,
   PRIMARY KEY(Id_Utilisateur, Id_Historique),
   FOREIGN KEY(Id_Utilisateur) REFERENCES Utilisateur(Id_Utilisateur) ON DELETE CASCADE,
   FOREIGN KEY(Id_Historique) REFERENCES Historique(Id_Historique) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tag_publication(
   Id_Publication INT,
   Id_Tag INT,
   PRIMARY KEY(Id_Publication, Id_Tag) ,
   FOREIGN KEY(Id_Publication) REFERENCES Publication(Id_Publication) ON DELETE CASCADE,
   FOREIGN KEY(Id_Tag) REFERENCES Tag(Id_Tag) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tag_avis(
   Id_Avis INT,
   Id_Tag INT,
   PRIMARY KEY(Id_Avis, Id_Tag),
   FOREIGN KEY(Id_Avis) REFERENCES Avis(Id_Avis) ON DELETE CASCADE,
   FOREIGN KEY(Id_Tag) REFERENCES Tag(Id_Tag) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tag_groupe(
   Id_Utilisateur INT,
   Id_Tag INT,
   PRIMARY KEY(Id_Utilisateur, Id_Tag),
   FOREIGN KEY(Id_Utilisateur) REFERENCES Artistes(Id_Utilisateur),
   FOREIGN KEY(Id_Tag) REFERENCES Tag(Id_Tag)
);

CREATE TABLE IF NOT EXISTS tag_concert(
   Id_Concert INT,
   Id_Tag INT,
   PRIMARY KEY(Id_Concert, Id_Tag),
   FOREIGN KEY(Id_Concert) REFERENCES Concert(Id_Concert),
   FOREIGN KEY(Id_Tag) REFERENCES Tag(Id_Tag)
);

CREATE TABLE IF NOT EXISTS tag_lieu(
   code INT,
   adresse VARCHAR(50),
   ville VARCHAR(50),
   Id_Tag INT,
   PRIMARY KEY(code, adresse, ville, Id_Tag),
   FOREIGN KEY(code, adresse, ville) REFERENCES Lieu(code, adresse, ville),
   FOREIGN KEY(Id_Tag) REFERENCES Tag(Id_Tag)
);

CREATE TABLE IF NOT EXISTS liker(
   Id_Utilisateur INT,
   Id_Publication INT,
   PRIMARY KEY(Id_Utilisateur, Id_Publication),
   FOREIGN KEY(Id_Utilisateur) REFERENCES Utilisateur(Id_Utilisateur),
   FOREIGN KEY(Id_Publication) REFERENCES Publication(Id_Publication)
);

CREATE TABLE IF NOT EXISTS amis(
   Id_Utilisateur INT,
   Id_Utilisateur_1 INT,
   PRIMARY KEY(Id_Utilisateur, Id_Utilisateur_1),
   FOREIGN KEY(Id_Utilisateur) REFERENCES Utilisateur(Id_Utilisateur),
   FOREIGN KEY(Id_Utilisateur_1) REFERENCES Utilisateur(Id_Utilisateur),
   CHECK (Id_Utilisateur <> Id_Utilisateur_1)
);

CREATE TABLE IF NOT EXISTS membre_groupe(
   Id_Utilisateur INT,
   Id_Utilisateur_1 INT,
   PRIMARY KEY(Id_Utilisateur, Id_Utilisateur_1),
   FOREIGN KEY(Id_Utilisateur) REFERENCES Artistes(Id_Utilisateur),
   FOREIGN KEY(Id_Utilisateur_1) REFERENCES Artistes(Id_Utilisateur)
);

CREATE TABLE IF NOT EXISTS participe(
   Id_Concert INT,
   Id_Utilisateur INT,
   PRIMARY KEY(Id_Concert, Id_Utilisateur),
   FOREIGN KEY(Id_Concert) REFERENCES Concert(Id_Concert),
   FOREIGN KEY(Id_Utilisateur) REFERENCES personne(Id_Utilisateur)
);

CREATE TABLE IF NOT EXISTS interesse(
   Id_Concert INT,
   Id_Utilisateur INT,
   PRIMARY KEY(Id_Concert, Id_Utilisateur),
   FOREIGN KEY(Id_Concert) REFERENCES Concert(Id_Concert),
   FOREIGN KEY(Id_Utilisateur) REFERENCES personne(Id_Utilisateur)
);

CREATE TABLE IF NOT EXISTS tag_musicienn(
   Id_Avis INT,
   Id_Utilisateur INT,
   PRIMARY KEY(Id_Avis, Id_Utilisateur),
   FOREIGN KEY(Id_Avis) REFERENCES Avis(Id_Avis),
   FOREIGN KEY(Id_Utilisateur) REFERENCES personne(Id_Utilisateur)
);

-- TRIGGERS 

CREATE OR REPLACE FUNCTION check_mutual_friendship()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM amis
        WHERE (Id_Utilisateur = NEW.Id_Utilisateur_1 AND Id_Utilisateur_1 = NEW.Id_Utilisateur)
        OR (Id_Utilisateur = NEW.Id_Utilisateur AND Id_Utilisateur_1 = NEW.Id_Utilisateur_1)
    ) THEN
        RAISE EXCEPTION 'L amitié doit être mutuelle.';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_mutual_friendship
BEFORE UPDATE ON amis
FOR EACH ROW
EXECUTE FUNCTION check_mutual_friendship();


CREATE OR REPLACE FUNCTION check_interest()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM participer
        WHERE Id_Utilisateur = NEW.Id_Utilisateur
          AND Id_Concert = NEW.Id_Concert
    ) THEN
        RAISE EXCEPTION 'Un utilisateur ne peut être à la fois intéressé et participer à un concert.';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_interest
BEFORE INSERT OR UPDATE ON interessé_par
FOR EACH ROW
EXECUTE FUNCTION check_interest();



CREATE OR REPLACE FUNCTION check_participation()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM participer
        WHERE Id_Utilisateur = NEW.Id_Utilisateur
          AND Id_Concert = NEW.Id_Concert
    ) THEN
        RAISE EXCEPTION 'Un utilisateur ne peut être à la fois intéressé et participer à un concert.';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_participation
BEFORE INSERT OR UPDATE ON participer
FOR EACH ROW
EXECUTE FUNCTION check_participation();


-- Créez une fonction de déclencheur pour limiter le nombre de playlists affichées par utilisateur
CREATE OR REPLACE FUNCTION limit_playlist_display()
RETURNS TRIGGER AS $$
DECLARE
    playlist_count INT;
BEGIN
    -- Compter le nombre de playlists déjà affichées pour l'utilisateur
    SELECT COUNT(*)
    INTO playlist_count
    FROM afficher
    WHERE Id_Utilisateur = NEW.Id_Utilisateur;

    IF playlist_count >= 10 THEN
        -- Si l'utilisateur a déjà 10 playlists affichées, annuler l'insertion ou la mise à jour
        RAISE EXCEPTION 'Limite de 10 playlists atteinte pour cet utilisateur.';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Ajoutez le déclencheur à la table afficher
CREATE TRIGGER trg_limit_playlist_display
BEFORE INSERT OR UPDATE ON afficher
FOR EACH ROW
EXECUTE FUNCTION limit_playlist_display();

-- Créez la fonction de déclenchement
CREATE OR REPLACE FUNCTION limit_songs_per_playlist()
RETURNS TRIGGER AS $$
BEGIN
    IF (
        SELECT COUNT(*)
        FROM contenir
        WHERE Id_Playlist = NEW.Id_Playlist
    ) >= 20 THEN
        RAISE EXCEPTION 'Une playlist ne peut contenir plus de 20 morceaux.';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Ajoutez le déclencheur à la table contenir
CREATE TRIGGER trg_limit_songs_per_playlist
BEFORE INSERT OR UPDATE ON contenir
FOR EACH ROW
EXECUTE FUNCTION limit_songs_per_playlist();

-- remplissage

\copy utilisateur(username_,adressemail,mdp,contact) FROM './mycsv/Utilisateurs.csv' WITH (FORMAT csv);
\copy personne(nom,Id_Utilisateur, prenom) FROM './mycsv/persones.csv' WITH (FORMAT csv);
\copy artistes(groupe,Id_Utilisateur) FROM './mycsv/artistes.csv' WITH (FORMAT csv);
\copy pays_(code,nom) FROM './mycsv/pays.csv' WITH (FORMAT csv);
\copy association(Id_Utilisateur, cause) FROM './mycsv/associations.csv' WITH (FORMAT csv);
\copy Lieu(code, adresse, ville) FROM './mycsv/Lieu.csv' WITH (FORMAT csv);
\copy Concert(nom_concert,salle_concert,date_event,nb_place,prix_place,besoin_volentaire,en_exterieur,lineUp,pour_enfants,cause_de_soutien,descriptif,Id_Utilisateur,code,adresse,ville) FROM './mycsv/concerts.csv' WITH (FORMAT csv);
\copy Publication(texte, date_publication, Id_Utilisateur) FROM './mycsv/publications.csv' WITH (FORMAT csv);
\copy Playlist(nom_playlist, auteur_album) FROM './mycsv/Playlists.csv' WITH (FORMAT csv);
\copy amis(Id_Utilisateur,Id_Utilisateur_1) FROM './mycsv/amis.csv' WITH (FORMAT csv);
\copy follow(Id_Utilisateur_followed,Id_Utilisateur_follows ) FROM './mycsv/follows.csv' WITH (FORMAT csv);
\copy tag(nom_tag) FROM './mycsv/tags.csv' WITH (FORMAT csv);
\copy tag_publication(Id_Publication,Id_Tag) FROM './mycsv/tag_pub.csv' WITH (FORMAT csv);
\copy tag_concert(Id_Concert,Id_Tag) FROM './mycsv/tag_concert.csv' WITH (FORMAT csv);
\copy Avis(date_publication,commentaire,note,Id_Utilisateur,Id_Publication,Id_Concert) FROM './mycsv/avis.csv' WITH (FORMAT csv);
\copy tag_avis(Id_Avis,Id_Tag) FROM './mycsv/tag_avis.csv' WITH (FORMAT csv);
\copy MOrceau(titre) FROM './mycsv/morceau.csv' WITH (FORMAT csv);
\copy participer(Id_Utilisateur,Id_Concert) FROM './mycsv/participer.csv' WITH (FORMAT csv);
