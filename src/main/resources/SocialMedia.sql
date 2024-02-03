DROP TABLE if EXISTS message;
DROP TABLE if EXISTS account;
CREATE TABLE account (
    account_id INT PRIMARY KEY auto_increment,
    username VARCHAR(255) UNIQUE,
    password VARCHAR(255)
);
CREATE TABLE message (
    message_id INT PRIMARY KEY auto_increment,
    posted_by INT,
    message_text VARCHAR(255),
    time_posted_epoch bigint,
    FOREIGN KEY (posted_by) REFERENCES account(account_id)
);

INSERT INTO account (username, password) VALUES ('testuser1', 'password');
INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (1,'test message 1',1669947792);
