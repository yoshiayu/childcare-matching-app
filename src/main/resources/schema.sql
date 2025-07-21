DROP TABLE IF EXISTS inquiries CASCADE;
DROP TABLE IF EXISTS notices CASCADE;
DROP TABLE IF EXISTS favorites CASCADE;
DROP TABLE IF EXISTS interviews CASCADE;
DROP TABLE IF EXISTS job_postings CASCADE;
DROP TABLE IF EXISTS nurseries CASCADE;
DROP TABLE IF EXISTS push_subscriptions CASCADE;
DROP TABLE IF EXISTS user_qualifications CASCADE;
DROP TABLE IF EXISTS work_experiences CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS areas CASCADE;
DROP TABLE IF EXISTS qualifications CASCADE;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(60) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL, -- 'ROLE_NURSE' for 保育士
    profile_image VARCHAR(255),
    desired_area VARCHAR(100),
    desired_salary INTEGER,
    desired_work_time VARCHAR(50),
    reset_token VARCHAR(255), -- Add this line
    reset_token_expiry_date TIMESTAMP, -- Add this line
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE nurseries (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    philosophy TEXT,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    reset_token VARCHAR(255), -- Add this line
    reset_token_expiry_date TIMESTAMP, -- Add this line
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE job_postings (
    id SERIAL PRIMARY KEY,
    nursery_id INTEGER NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    area VARCHAR(100),
    salary INTEGER,
    work_time VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT '下書き', -- '公開', '非公開', '審査中', '却下'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (nursery_id) REFERENCES nurseries(id)
);

CREATE TABLE interviews (
    id SERIAL PRIMARY KEY,
    nursery_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    message TEXT,
    interview_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT '未承諾', -- '承諾済', '辞退済', '取り消し済'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (nursery_id) REFERENCES nurseries(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE favorites (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    job_posting_id INTEGER NOT NULL,
    UNIQUE (user_id, job_posting_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (job_posting_id) REFERENCES job_postings(id)
);

CREATE TABLE notices (
    id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    target VARCHAR(20) NOT NULL, -- '保育士', '保育園', 'ALL'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE inquiries (
    id SERIAL PRIMARY KEY,
    sender_type VARCHAR(20) NOT NULL, -- '保育士', '保育園'
    sender_id INTEGER NOT NULL,
    subject VARCHAR(255),
    message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE areas (
    code VARCHAR(10) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE qualifications (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE user_qualifications (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    qualification_id INTEGER NOT NULL,
    UNIQUE (user_id, qualification_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (qualification_id) REFERENCES qualifications(id)
);

CREATE TABLE work_experiences (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    job_title VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    description TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE push_subscriptions (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    endpoint VARCHAR(1024) NOT NULL UNIQUE,
    p256dh VARCHAR(255) NOT NULL,
    auth VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
