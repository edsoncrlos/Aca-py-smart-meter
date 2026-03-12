INSERT INTO TB_ROLE (name, description, active)
VALUES
    ('ROLE_USER', 'Default user of system', true),
    ('ROLE_EMPLOYEE', 'Employee of company', true);

INSERT INTO TB_USER (username, password)
VALUES
    ('alice', '$2a$12$bYXG1awWTQMf1t1KnC5oBOx6Gq4c7VE8CRbJLoJ91WxSdpzGxCRn.'),
    ('bob',   '$2a$12$bYXG1awWTQMf1t1KnC5oBOx6Gq4c7VE8CRbJLoJ91WxSdpzGxCRn.');

-- alice -> ROLE_USER
INSERT INTO TB_USER_ROLE (user_id, role_id)
VALUES (
   (SELECT id FROM TB_USER WHERE username = 'alice'),
   (SELECT id FROM TB_ROLE WHERE name = 'ROLE_USER')
);

-- bob -> ROLE_USER e ROLE_EMPLOYEE
INSERT INTO TB_USER_ROLE (user_id, role_id)
VALUES (
    (SELECT id FROM TB_USER WHERE username = 'bob'),
    (SELECT id FROM TB_ROLE WHERE name = 'ROLE_USER')
),
(
    (SELECT id FROM TB_USER WHERE username = 'bob'),
    (SELECT id FROM TB_ROLE WHERE name = 'ROLE_EMPLOYEE')
);
