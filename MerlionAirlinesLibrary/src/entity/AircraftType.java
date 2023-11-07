/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class AircraftType implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aircraftTypeId;
    @Column(length = 100, nullable = false, unique = true)
    @NotNull
    @Size(min = 1, max = 100)
    private String aircraftTypeName;
    @Column(nullable = false)
    @NotNull
    @Min(1)
    @Max(1000)
    private int maxSeatCapacity;
    
    @OneToMany(mappedBy = "aircraftType")
    private List<AircraftConfig> aircraftConfigs;

    public AircraftType() {
    }

    public AircraftType(String aircraftTypeName, int maxSeatCapacity) {
        this.aircraftTypeName = aircraftTypeName;
        this.maxSeatCapacity = maxSeatCapacity;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (aircraftTypeId != null ? aircraftTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the aircraftTypeId fields are not set
        if (!(object instanceof AircraftType)) {
            return false;
        }
        AircraftType other = (AircraftType) object;
        if ((this.aircraftTypeId == null && other.aircraftTypeId != null) || (this.aircraftTypeId != null && !this.aircraftTypeId.equals(other.aircraftTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.AircraftType[ id=" + aircraftTypeId + " ]";
    }
    
    public Long getAircraftTypeId() {
        return aircraftTypeId;
    }

    public void setAircraftTypeId(Long aircraftTypeId) {
        this.aircraftTypeId = aircraftTypeId;
    }

    /**
     * @return the aircraftTypeName
     */
    public String getAircraftTypeName() {
        return aircraftTypeName;
    }

    /**
     * @param aircraftTypeName the aircraftTypeName to set
     */
    public void setAircraftTypeName(String aircraftTypeName) {
        this.aircraftTypeName = aircraftTypeName;
    }

    /**
     * @return the maxSeatCapacity
     */
    public int getMaxSeatCapacity() {
        return maxSeatCapacity;
    }

    /**
     * @param maxSeatCapacity the maxSeatCapacity to set
     */
    public void setMaxSeatCapacity(int maxSeatCapacity) {
        this.maxSeatCapacity = maxSeatCapacity;
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
