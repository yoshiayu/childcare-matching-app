INSERT INTO users (name, email, password, role) VALUES
    ('保育士A', 'hoikushiA@example.com', 'password', 'NURSE'),
    ('保育士B', 'hoikushiB@example.com', 'password', 'NURSE');

INSERT INTO nurseries (name, email, password, address, philosophy, latitude, longitude) VALUES
    ('きらきら保育園', 'kirakira@example.com', 'password', '東京都渋谷区', '子どもたちの個性を尊重します。', 35.658034, 139.701636),
    ('すくすく保育園', 'sukusuku@example.com', 'password', '神奈川県横浜市', '自然との触れ合いを大切にします。', 35.443708, 139.638026);

INSERT INTO users (name, email, password, role) VALUES
    ('管理者', 'admin@example.com', 'adminpass', 'ADMIN');

INSERT INTO areas (code, name) VALUES
    ('TOKYO', '東京都'),
    ('KANAGAWA', '神奈川県');

INSERT INTO qualifications (name) VALUES
    ('保育士'),
    ('幼稚園教諭');

-- Sample job posting
INSERT INTO job_postings (nursery_id, title, description, area, salary, work_time, status) VALUES
    ((SELECT id FROM nurseries WHERE email = 'kirakira@example.com'), '経験者歓迎！保育士募集', '子どもたちの笑顔のために一緒に働きませんか？', '東京都渋谷区', 250000, '9:00-17:00', '公開');