INSERT INTO public.teams (uuid, name, create_date) VALUES ('1B09DD229C894535B579B0956FD95631', 'Bodas team user', now());
INSERT INTO public.users (uuid, account_type, activate_password, active, email, enabled, mother_surname, name, password, photo, surname, user_name, create_date, team_uuid) VALUES ('0E41BA836BC34655B57325FDA614B989', 'DEFAULT', null, true, 'no-real-user@fake.com', true, '', 'userMockService', '', '', 'surnameMock', 'userMockService', now(), '1B09DD229C894535B579B0956FD95631');