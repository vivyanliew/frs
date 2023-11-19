/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.validation.ValidationException;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Su Shwe Yee
 */
@Entity
public class CabinClass implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cabinClassId;
   @Column(nullable = false)
    private String cabinClassName;
    @Column(nullable = false)
    @NotNull
    @Min(0)
    @Max(2)
    private int numAisles;
    @Column(nullable = false)
    @NotNull
    @Min(1)
    private int numRows;
    @Column(nullable = false)
    @NotNull
    @Min(1)
    private int numSeatsAbreast;
    @Column(nullable = false)
    @NotNull
    private String actualSeatConfigPerCol;
    @Column(nullable = false)
    //@NotNull
    //@Min(1)
    //@Max(1000)
    private int maxSeatCapacity;
    
    @ManyToMany(mappedBy = "cabinClasses")
    private List<AircraftConfig> aircraftConfigs;
    @OneToMany(mappedBy = "cabinClass", fetch = FetchType.EAGER)
    private List<Fare> fares;
    

    public CabinClass() {
        this.aircraftConfigs = new ArrayList<>();
        this.fares = new ArrayList<Fare>();
    }

    public CabinClass(String cabinClassName, int numAisles, int numRows, int numSeatsAbreast, String actualSeatConfigPerCol) {
        this();
        this.cabinClassName = cabinClassName;
        this.numAisles = numAisles;
        this.numRows = numRows;
        this.numSeatsAbreast = numSeatsAbreast;
        this.actualSeatConfigPerCol = actualSeatConfigPerCol;
        this.maxSeatCapacity = this.numRows * this.numSeatsAbreast;
    }

    public String getCabinClassName() {
        return cabinClassName;
    }

    public void setCabinClassName(String cabinClassName) {
        this.cabinClassName = cabinClassName;
    }

    public int getMaxSeatCapacity() {
        return maxSeatCapacity;
    }
    public void updateMaxSeatCapacity() {
        this.maxSeatCapacity = this.numRows * this.numSeatsAbreast;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cabinClassId != null ? cabinClassId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the cabinClassId fields are not set
        if (!(object instanceof CabinClass)) {
            return false;
        }
        CabinClass other = (CabinClass) object;
        if ((this.cabinClassId == null && other.cabinClassId != null) || (this.cabinClassId != null && !this.cabinClassId.equals(other.cabinClassId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.CabinClass[ id=" + cabinClassId + " ]";
    }
    
    public Long getCabinClassId() {
        return cabinClassId;
    }

    public void setCabinClassId(Long cabinClassId) {
        this.cabinClassId = cabinClassId;
    }

    /**
     * @return the numAisles
     */
    public int getNumAisles() {
        return numAisles;
    }

    /**
     * @param numAisles the numAisles to set
     */
    public void setNumAisles(int numAisles) {
        this.numAisles = numAisles;
    }

    /**
     * @return the numRows
     */
    public int getNumRows() {
        return numRows;
    }

    /**
     * @param numRows the numRows to set
     */
    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    /**
     * @return the numSeatsAbreast
     */
    public int getNumSeatsAbreast() {
        return numSeatsAbreast;
    }

    /**
     * @param numSeatsAbreast the numSeatsAbreast to set
     */
    public void setNumSeatsAbreast(int numSeatsAbreast) {
        this.numSeatsAbreast = numSeatsAbreast;
    }

    public String getActualSeatConfigPerCol() {
        return actualSeatConfigPerCol;
    }

    public void setActualSeatConfigPerCol(String actualSeatConfigPerCol) {
        this.actualSeatConfigPerCol = actualSeatConfigPerCol;
    }

    
    public List<Fare> getFares() {
        return fares;
    }

    public void setFares(List<Fare> fares) {
        this.fares = fares;
    }

    /**
     * @return the aircraftConfigs
     */
    public List<AircraftConfig> getAircraftConfigs() {
        return aircraftConfigs;
    }

    /**
     * @param aircraftConfigs the aircraftConfigs to set
     */
    public void setAircraftConfigs(List<AircraftConfig> aircraftConfigs) {
        this.aircraftConfigs = aircraftConfigs;
    }
    

    
}
