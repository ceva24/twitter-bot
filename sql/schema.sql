-- MySql Dialect
CREATE TABLE status (id INT NOT NULL AUTO_INCREMENT, text VARCHAR(140) NOT NULL, sequence_no INT NOT NULL UNIQUE, tweeted_on TIMESTAMP NULL, PRIMARY KEY (id));