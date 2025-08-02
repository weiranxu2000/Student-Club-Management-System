BEGIN;

INSERT INTO app.user (e_mail, student_id, admin_id, password, name) VALUES
    ('weiranx1@student.unimelb.edu.au', 1454343, null, 'user', 'Weiran Xu'),
    ('yixiangx2@student.unimelb.edu.au', 1493483, null, 'user', 'Yixiang Xia'),
    ('donhuo@student.unimelb.edu.au', 1441867, null, 'user', 'Donglin Huo'),
    ('xuerx1@student.unimelb.edu.au', 1451279, null, 'user', 'Rui Xue'),
    ('user1@student.unimelb.edu.au', 1111111, null, 'user', 'Dummy User 1'),
    ('user2@student.unimelb.edu.au', 2222222, null, 'user', 'Dummy User 2'),
    ('user3@student.unimelb.edu.au', 3333333, null, 'user', 'Dummy User 3'),
    ('user4@student.unimelb.edu.au', 4444444, null, 'user', 'Dummy User 4'),
    ('user5@student.unimelb.edu.au', 5555555, null, 'user', 'Dummy User 5'),
    ('user6@student.unimelb.edu.au', 6666666, null, 'user', 'Dummy User 6'),
    ('user7@student.unimelb.edu.au', 7777777, null, 'user', 'Dummy User 7'),
    ('user8@student.unimelb.edu.au', 8888888, null, 'user', 'Dummy User 8'),
    ('user9@student.unimelb.edu.au', 9999999, null, 'user', 'Dummy User 9'),
    ('admin1@unimelb.edu.au', null, 1, 'admin', 'Dummy Admin 1'),
    ('admin2@unimelb.edu.au', null, 2, 'admin', 'Dummy Admin 1');

INSERT INTO app.club (id, name, description) VALUES
    (1, 'Esports Club', 'Your home of Esports and Gaming at Unimelb! If you do not support T1, you a dub.'),
    (2, 'Kpop Club', 'If you like dancing to the latest K-pop hits, belting out songs by your fave K-pop artists, binging K-dramas, or just enjoying the pop culture sensation that is K-pop, you''ll find your home at UKC.'),
    (3, 'Engineering Music Society', 'A non-auditioned music club where musicians of any skill level or study background (NOT just engineering!) can play their instrument in a relaxed environment outside of work/study.'),
    (4, 'HackMelbourne', 'Dedicated to hackathons, software development education and innovation, our vision is to run large intercollegiate hackathons for all students and provide tech-focused educational opportunities.'),
    (5, 'Genshin Impact Club', 'Every OP join the club please.');

INSERT INTO app.student_club (student_id, club_id, is_admin) VALUES
    (1454343, 1, true),
    (1454343, 2, true),
    (1454343, 3, true),
    (1454343, 4, true),
    (1111111, 1, true),
    (1111111, 2, true),
    (1111111, 3, true),
    (2222222, 2, true),
    (3333333, 3, true);

INSERT INTO app.venue (id, name, capacity) VALUES
    (0, 'Online Zoom Space', 9999),
    (1, 'PAR-192-L2-L108-Laby Theatre', 100),
    (2, 'PAR-379-B1-B115-Digital Learning Space', 3),
    (3, 'PAR-104-L1-101-Collaborative Learning Space', 10),
    (4, 'PAR-149-L1-122-Public Lecture Theatre & Event Venue', 100),
    (5, 'PAR-122-L1-124-Turner Theatre', 50),
    (6, 'PAR-193-L1-122-FEIT Computer Lab', 30);

INSERT INTO app.event (id, club_id, title, description, venue_id, date, time, cost, status) VALUES
    (gen_random_uuid(), 1, 'Watch LCK final game.', 'Support T1!', 0, '2024-09-08', '16:00', 0, 'created'),
    (gen_random_uuid(), 2, 'Aespa Concert', 'Watch Aespa''s Melbourne Concert!', 2, '2024-09-02', '19:00', 100, 'created'),
    (gen_random_uuid(), 3, 'Kanye Haikou LP', 'Kanye West is the best artist in the world.', 0, '2024-09-15', '20:00', 1280, 'created'),
    (gen_random_uuid(), 4, 'Play Genshin', 'Use HUAWEI phones to play.', 1, '2024-09-21', '12:00', 648, 'cancelled');

INSERT INTO app.fund (id, description, amount, time, club_id, status)
VALUES
    ('f3a7e1d3-26e0-4fbd-bdc6-7dca6a9e5f8e', 'Annual sports equipment fund', 5000, '2024-10-10 10:00:00', 1, 'submitted'),
    ('84eac62d-3b5b-4292-b09a-9a7aaf74114d', 'Library renovation fund', 12000, '2024-09-15 14:30:00', 2, 'submitted'),
    ('1945cb9d-57f1-4c13-92c5-91c56c58a2f3', 'Student event sponsorship', 3000, '2024-08-20 09:45:00', 3, 'submitted'),
    ('0b18c6f2-6dcb-4e4d-a678-909ba3d6b534', 'New football field project', 25000, '2024-07-12 11:15:00', 4, 'submitted'),
    ('e1b0c5b7-3b99-4cc8-9e71-2f85f6c44d8c', 'Cultural festival fund', 1500, '2024-06-22 16:20:00', 5, 'submitted');

COMMIT;