package ro.iss.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "users", catalog = "", schema = "") 
public class User extends Identifiable<Integer> {

    @Column(name = "username")
    private String username;
    
    @Column(name = "password")
    private String password;
    
    @Column(name = "type")
    private String type;

    // Annotations removed from getters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}