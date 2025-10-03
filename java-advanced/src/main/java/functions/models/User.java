package functions.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class User {
    private int id;
    private String name;
    private String email;
    private LocalDateTime createdAt;

    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getCreatedAt() {
        return createdAt.format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public String toJson() {
        return String.format(
            "{\"id\":%d,\"name\":\"%s\",\"email\":\"%s\",\"created_at\":\"%s\"}",
            id, name, email, getCreatedAt()
        );
    }
}
