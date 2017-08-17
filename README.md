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

### Java Config
You will also have to make a `DefaultConfig.java` file to hold your MySQL credentials. It should look like this:
```java
public class DefaultConfig implements Config {
    public String getSqlUsername() {
        return "myusername";
    }
    public String getSqlPassword() {
        return "mycasesensitivepass";
    }

    public String getDbUrl() {
        return "jdbc:mysql://localhost/NOTES?useSSL=true"; // or whatever you named your my SQL database
    }
}
```

Once you have made the database and the `Config.java` file, you should be ready to go. Run the program by running `Main.main()`
