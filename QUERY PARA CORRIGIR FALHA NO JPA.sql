/* 1) DESCROBRIR O NOME DA CONSTRAINT QUE SERÁ REMOVIDA */
SELECT constraint_name from information_schema.constraint_column_usage WHERE table_name = 'usuarios_role' AND column_name = 'role_id' AND constraint_name <> 'unique_role_user';

/* 2) EXCLUIR A CONSTRAINT */
ALTER TABLE usuarios_role DROP CONSTRAINT uk_krvk2qx218dxa3ogdyplk0wxw;

/* 3) INSERIR OS ACESSOS PADRAO PARA O CADASTRO DO NOVO USUARIO */
INSERT INTO usuarios_role (usuario_id, role_id) VALUES (45, (SELECT id FROM role WHERE nome_role = 'ROLE_USER')), 
                                                       (50, (SELECT id FROM role WHERE nome_role = 'ROLE_USER'));


