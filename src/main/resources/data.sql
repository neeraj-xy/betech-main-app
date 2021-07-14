CREATE DATABASE IF NOT EXISTS medsys;
/*INSERT INTO `medsys`.`user` (`id`, `confirmation_token`, `dob`, `email`, `enabled`, `first_name`, `last_name`, `lastseen`, `password`, `authority`) 
  SELECT '99999', 'ByAdmin-Panel', '1111-11-11', 'admin@app.com', 1, 'Admin', 'User', 'Mon Jan 01 00:00:01 GMT 0001', 'P@ssw0rd', 'ROLE_ADMIN' FROM DUAL
WHERE NOT EXISTS 
  (SELECT authority FROM `medsys`.`user` WHERE authority='ROLE_ADMIN');*/
  
INSERT INTO `medsys`.`user` (`id`, `confirmation_token`, `username`, `enabled`, `first_name`, `gender`, `last_name`, `lastseen`, `password`, `authority`, `dob`, `reset_password_token`)    SELECT '99999', 'ByAdmin-Panel', 'admin@app.com', 1, 'Admin', 'male', 'Admin', 'Mon Jan 01 00:00:01 GMT 0001', '$2a$10$gVciKvpWTdJ2IOXImmOM1u8p.qNcCN9N7YztW3cLx2n8uYHzCa89W', 'ROLE_ADMIN', '0001-01-01', NULL FROM DUAL WHERE NOT EXISTS    (SELECT authority FROM `medsys`.`user` WHERE authority='ROLE_ADMIN')
