-- MyWorld (MySQL) seed data
-- Compatible with JPA entities/tables:
-- users, books, chapters, chapter_reads, comments, articles
--
-- Notes:
-- - passwords below are BCrypt hashes for the plaintext "Password123!"
-- - roles are stored as strings: ADMIN / USER
-- - naming assumes Spring/Hibernate snake_case columns (created_at, is_active, url_image, like_count)

-- Users
INSERT IGNORE INTO users (id, email, username, password, role, created_at, is_active) VALUES
  (1, 'admin@myworld.com', 'admin', '$2a$10$u1Vg7uR9yYw3qk2wmtu5leWQk3c9y2f5TQdS6Xf8j2gY6m7Qx4qK2', 'ADMIN', NOW() - INTERVAL 60 DAY, 1),
  (2, 'alice@myworld.com', 'alice', '$2a$10$u1Vg7uR9yYw3qk2wmtu5leWQk3c9y2f5TQdS6Xf8j2gY6m7Qx4qK2', 'USER',  NOW() - INTERVAL 30 DAY, 1),
  (3, 'bob@myworld.com',   'bob',   '$2a$10$u1Vg7uR9yYw3qk2wmtu5leWQk3c9y2f5TQdS6Xf8j2gY6m7Qx4qK2', 'USER',  NOW() - INTERVAL 20 DAY, 1),
  (4, 'eva@myworld.com',   'eva',   '$2a$10$u1Vg7uR9yYw3qk2wmtu5leWQk3c9y2f5TQdS6Xf8j2gY6m7Qx4qK2', 'USER',  NOW() - INTERVAL 10 DAY, 0);

-- Books
INSERT IGNORE INTO books (id, title, `number`, created_at) VALUES
  (1, 'MyWorld — Tome 1', 1, NOW() - INTERVAL 40 DAY),
  (2, 'MyWorld — Tome 2', 2, NOW() - INTERVAL 15 DAY);

-- Chapters (unique per (book_id, number))
INSERT IGNORE INTO chapters (id, title, `number`, content, book_id, created_at, like_count) VALUES
  (1, 'Prologue', 1, 'Bienvenue dans MyWorld. Ceci est le prologue.', 1, NOW() - INTERVAL 39 DAY, 5),
  (2, 'Chapitre 1 — Le départ', 2, 'Le héros quitte son village et découvre un nouveau monde.', 1, NOW() - INTERVAL 38 DAY, 12),
  (3, 'Chapitre 2 — La rencontre', 3, 'Une rencontre inattendue change le cours des événements.', 1, NOW() - INTERVAL 37 DAY, 3),
  (4, 'Tome 2 — Chapitre 1', 1, 'Nouveau tome, nouveaux enjeux.', 2, NOW() - INTERVAL 14 DAY, 7);

-- Chapter reads (1 row per (user_id, chapter_id))
INSERT IGNORE INTO chapter_reads (id, user_id, chapter_id, read_at) VALUES
  (1, 2, 1, NOW() - INTERVAL 7 DAY),
  (2, 2, 2, NOW() - INTERVAL 6 DAY),
  (3, 3, 1, NOW() - INTERVAL 5 DAY),
  (4, 3, 4, NOW() - INTERVAL 1 DAY);

-- Comments (unique per (user_id, chapter_id))
INSERT IGNORE INTO comments (id, content, rating, user_id, chapter_id, created_at) VALUES
  (1, 'Très bon début, hâte de lire la suite.', 9, 2, 1, NOW() - INTERVAL 6 DAY),
  (2, 'Le rythme est bon, mais j\'attends plus d\'action.', 7, 3, 1, NOW() - INTERVAL 4 DAY),
  (3, 'Super chapitre, j ai adoré la rencontre.', 10, 2, 2, NOW() - INTERVAL 5 DAY),
  (4, 'Tome 2 prometteur !', 8, 3, 4, NOW() - INTERVAL 12 HOUR);

-- Articles (written by a user)
INSERT IGNORE INTO articles (id, title, content, user_id, url_image, created_at) VALUES
  (1, 'Annonce — Ouverture de MyWorld', 'Le projet MyWorld est officiellement lancé. Bienvenue !', 1, 'https://picsum.photos/seed/myworld1/1200/630', NOW() - INTERVAL 25 DAY),
  (2, 'Devlog — Tome 1', 'Quelques notes de développement sur le tome 1 et ses chapitres.', 1, 'https://picsum.photos/seed/myworld2/1200/630', NOW() - INTERVAL 18 DAY),
  (3, 'Communauté — Vos retours', 'Merci pour vos retours et vos commentaires, continuez !', 1, 'https://picsum.photos/seed/myworld3/1200/630', NOW() - INTERVAL 8 DAY);
