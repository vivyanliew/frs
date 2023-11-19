/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Su Shwe Yee
 */
@Entity
public class AircraftConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aircraftConfigId;
    @Column(length = 100, nullable = false)
    @NotNull
    @Size(max = 100)
    private String aircraftConfigName;
    @Column(nullable = false)
    @Min(1)
    @Max(4)
    private int numCabinClasses;
//    @Column(nullable = false)
//    @NotNull
//    @Min(1)
//    @Max(1000)
private int maxSeatCapacity;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private AircraftType aircraftType;
    @ManyToMany(fetch=FetchType.EAGER)
    private List<CabinClass> cabinClasses;
    @OneToMany(mappedBy = "aircraftConfig")
    private List<Flight> flights;

    public AircraftConfig() {
        this.cabinClasses = new ArrayList<>();
        this.flights = new ArrayList<>();
    }

    public AircraftConfig(AircraftType aircraftType, String aircraftConfigName, int numCabinClasses) {

        this();
        this.aircraftType = aircraftType;
        this.aircraftConfigName = aircraftConfigName;
        this.numCabinClasses = numCabinClasses;
        //this.maxSeatCapacity = calculateMaxSeatCapacity(this.cabinClasses);
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (aircraftConfigId != null ? aircraftConfigId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the aircraftConfigId fields are not set
        if (!(object instanceof AircraftConfig)) {
            return false;
        }
        AircraftConfig other = (AircraftConfig) object;
        if ((this.aircraftConfigId == null && other.aircraftConfigId != null) || (this.aircraftConfigId != null && !this.aircraftConfigId.equals(other.aircraftConfigId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.AircraftConfig[ id=" + aircraftConfigId + " ]";
    }

    public Long getAircraftConfigId() {
        return aircraftConfigId;
    }

    public void setAircraftConfigId(Long aircraftConfigId) {
        this.aircraftConfigId = aircraftConfigId;
    }
    private void setMaxSeatCapacity(List<CabinClass> cabinClasses) {
        int max = 0;
        for (int i = 0; i < cabinClasses.size(); i++) {
            max += cabinClasses.get(i).getMaxSeatCapacity();
        }
        this.maxSeatCapacity = max;
    }


     public int getMaxSeatCapacity() {
        return maxSeatCapacity;
    }


    /**
     * @return the aircraftConfigName
     */
    public String getAircraftConfigName() {
        return aircraftConfigName;
    }

    /**
     * @param aircraftConfigName the aircraftConfigName to set
     */
    public void setAircraftConfigName(String aircraftConfigName) {    
        this.aircraftConfigName = aircraftConfigName;
    }

    /**
     * @return the numCabinClasses
     */
    public int getNumCabinClasses() {
        return numCabinClasses;
    }

    /**
     * @param numCabinClasses the numCabinClasses to set
     */
    public void setNumCabinClasses(int numCabinClasses) {
        this.numCabinClasses = numCabinClasses;
    }

    /**
     * @return the maxSeatCapacity
     */
    /**
     * @return the cabinClasses
     */
    public List<CabinClass> getCabinClasses() {
        return cabinClasses;
    }

    /**
     * @param cabinClasses the cabinClasses to set
     */
    public void setCabinClasses(List<CabinClass> cabinClasses) {
        this.cabinClasses = cabinClasses;
    }

    /**
     * @return the aircraftType
     */
    public AircraftType getAircraftType() {
        return aircraftType;
    }

    /**
     * @param aircraftType the aircraftType to set
     */
    public void setAircraftType(AircraftType aircraftType) {
        this.aircraftType = aircraftType;
    }

    /**
     * @param maxSeatCapacity the maxSeatCapacity to set
     */
    public void setMaxSeatCapacity(int maxSeatCapacity) {
        this.maxSeatCapacity = maxSeatCapacity;
    }
    
    public void updateMaxSeatCapacity() {
        int max = 0;
        for (int i = 0; i < this.cabinClasses.size(); i++) {
            max += this.cabinClasses.get(i).getMaxSeatCapacity();
        }
        this.maxSeatCapacity = max;
    }
}
