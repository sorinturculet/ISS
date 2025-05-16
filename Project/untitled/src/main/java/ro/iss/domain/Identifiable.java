package ro.iss.domain;
import jakarta.persistence.*;

import java.io.Serializable;

@MappedSuperclass
public class Identifiable<ID extends Serializable> implements Serializable {
    private ID id;

    @Id
    @GeneratedValue(generator = "increment")
    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }
}