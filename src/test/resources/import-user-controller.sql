INSERT INTO public.teams (uuid, name, create_date) VALUES ('DB0C74736E8F448D998733FF730DAF1B', 'Bodas team Controller User', now());
INSERT INTO public.users (uuid, account_type, activate_password, active, email, enabled, mother_surname, name, password, photo, surname, user_name, create_date, team_uuid)
VALUES ('6CD8602CDE1E4D209318ABEAC0706C2D', 'DEFAULT', null, true, 'no-realcontrolleruser@fake.com', true, '', 'userMockEventControllerUser', '$2a$10$fAxW8wHuex/rYXh3tCmZdOgRtxl7sEnbO1fQ9k6ohdKm4ba7lX9Te', '', 'Surname', 'userMockEventControllerUser', now(), 'DB0C74736E8F448D998733FF730DAF1B');
