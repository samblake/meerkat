
INSERT INTO projects (id, name, description, base) VALUES (1, 'Google', 'A basic example.', 'https://www.google.com');

INSERT INTO browsers (id, name, description, client, width, height)
    VALUES (1, 'Desktop', 'Chrome desktop (21.5" monitor)', 'Chrome', 1920, 1080);

INSERT INTO browsers (id, name, description, client, width, height)
    VALUES (2, 'Mobile', 'Chrome mobile (iPhone 7)', 'Chrome', 750, 1334);

INSERT INTO cases (id, name, description, project, path)
    VALUES (1, 'Home', 'The home page', 1, '/');

INSERT INTO scenarios (id, name, description, project)
    VALUES (1, 'Example', 'A simple example scenario', 1);
