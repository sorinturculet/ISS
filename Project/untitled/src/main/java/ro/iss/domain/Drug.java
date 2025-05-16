package ro.iss.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "drugs")
public class Drug extends Identifiable<Integer> {

    private String name;
    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}