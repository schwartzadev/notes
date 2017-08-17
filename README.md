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
  `id` int(11) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `body` varchar(2000) DEFAULT NULL,
  `color` varchar(6) DEFAULT NULL,
  `archived` tinyint(1) DEFAULT '0',
  `html` varchar(2000) DEFAULT NULL
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
