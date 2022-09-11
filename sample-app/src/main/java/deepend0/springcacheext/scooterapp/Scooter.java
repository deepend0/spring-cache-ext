package deepend0.springcacheext.scooterapp;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Entity
public class Scooter {
    public enum Status {
        BROKEN,
        BATTERY_EMPTY,
        STEADY
    }
    @Id
    private String id;
    private String model;
    private Status status;
    @ManyToOne(cascade = CascadeType.ALL)
    private Polygon polygon;

    public Scooter() {
    }

    public Scooter(String id) {
        this.id = id;
    }

    public Scooter(String id, String model, Status status) {
        this.id = id;
        this.model = model;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scooter scooter = (Scooter) o;
        return id.equals(scooter.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
