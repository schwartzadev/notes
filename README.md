# notes
A server-side notes application using MySQL and Javalin (based on Jetty)
![Screenshot of Notes Draft](https://i.imgur.com/x3HpJAG.png)

## setup
### MySQL
You will need to install MySQL to use this project.
1. Make a Notes database: `create database notes;`
2. Add the notes table with schema:
```sql
CREATE TABLE `notes` (
  `id` int NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `body` varchar(2000) DEFAULT NULL,
  `color` varchar(6) DEFAULT NULL,
  `archived` tinyint(1) DEFAULT '0',
  `html` varchar(2000) DEFAULT NULL
)
```
3. Add the users table like so:
```sql
CREATE TABLE `users` (
  `id` int NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `isactive` tinyint(1) DEFAULT NULL
)
```
4. Add the logins table with this schema:
```sql
CREATE TABLE `logins` (
  `id` int NOT NULL,
  `user_id` int NOT NULL,
  `random` varchar(255) NOT NULL,
  `name_hash` varchar(255) NOT NULL,
  `date_created` datetime NOT NULL
)
```

### Config
You will also have to make a `config.json` file to hold your SQL information. It should look like this:
```json
{
  "SQL" : {
    "username" : "myname",
    "password" : "mypass",
    "url" : "jdbc:mysql://localhost/etcetcetc"
  }
}
```

Once you have made the database and the config file, you should be ready to go. Run the program by running `Main.main()`
