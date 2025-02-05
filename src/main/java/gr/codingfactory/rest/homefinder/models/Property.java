package gr.codingfactory.rest.homefinder.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "property")
public class Property implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PropertyUse propertyUse;

    @Column(nullable = false)
    private int yearOfConstruction;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private double squareMeters;

    @Column(nullable = false)
    private int floor;

    @Column(nullable = false)
    private int bathrooms;

    @Column(nullable = false)
    private int bedrooms;

    @Column(nullable = true, columnDefinition = "LONGTEXT")
    private String image;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PropertyType propertyType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EnergyClassType energyClass;

    @Column(name = "propertyCondition", nullable = false)
    @Enumerated(EnumType.STRING)
    private ConditionType condition;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "areaId")
    private Area area;

    @Column(name = "isActive", nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private Date createdAt;

    @Override
    public String toString() {
        return "Property{" + "id=" + id + ", area=" + area + ", description=" + description + ", createdAt=" + createdAt + ", yearOfConstruction=" + yearOfConstruction + ", price=" + price + ", squareMeters=" + squareMeters + ", floor=" + floor + ", bathrooms=" + bathrooms + ", bedrooms=" + bedrooms + ", propertyType=" + propertyType + ", energyClass=" + energyClass + ", condition=" + condition + ", user=" + user + ", isActive=" + isActive + ", image=" + image + '}';
    }
}
