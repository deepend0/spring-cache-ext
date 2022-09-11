package deepend0.springcacheext.scooterapp;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.List;
import java.util.Objects;

@Entity
public class Polygon {
    public static class Point {
        private double x;
        private double y;

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }
    }

    public Polygon() {
    }

    public Polygon(String id) {
        this.id = id;
    }

    public Polygon(String id, List<Point> points) {
        this.id = id;
        this.points = points;
    }

    @Id
    private String id;
    @Transient
    private List<Point> points;
    @ManyToOne(cascade = CascadeType.ALL)
    private Region region;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Scooter> scooters;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public List<Scooter> getScooters() {
        return scooters;
    }

    public void setScooters(List<Scooter> scooters) {
        this.scooters = scooters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Polygon polygon = (Polygon) o;
        return id.equals(polygon.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
