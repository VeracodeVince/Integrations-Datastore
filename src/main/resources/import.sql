insert into scm_types (name, display_name, scope) values ('github', 'GitHub', 'repo,admin:repo_hook,read:org,read:user'), ('gitlab', 'GitLab', 'api'), ('azure', 'Azure', 'vso.code_full vso.code_status vso.project_manage vso.threads_full vso.work_full'), ('bitbucket', 'BitBucket', '');
insert into scms(auth_base_url, api_base_url, repo_base_url, client_id, client_secret, type_id) values ('https://github.com', 'https://api.github.com', 'https://github.com', '', '', (select id from scm_types where name = 'github')), ('https://gitlab.com', 'https://gitlab.com/api/v4','https://gitlab.com','','',(select id from scm_types where name = 'gitlab')),('https://app.vssps.visualstudio.com','https://dev.azure.com','https://dev.azure.com','','',(select id from scm_types where name = 'azure')),('https://bitbucket.org','https://api.bitbucket.org/2.0','https://api.bitbucket.org','','',(select id from scm_types where name = 'bitbucket'));