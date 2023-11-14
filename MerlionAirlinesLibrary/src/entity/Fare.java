/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Su Shwe Yee
 */
@Entity
public class Fare implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fareId;
    @Column //(nullable = false)
    //@NotNull
    private String fareBasisCode;
    @Column(nullable = false, precision = 12, scale = 2)
    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal fareAmount;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private CabinClass cabinClass;

    public Fare() {
    }

    public Fare(BigDecimal fareAmount, CabinClass cabinClass) {
        this.fareAmount = fareAmount;
        this.cabinClass = cabinClass;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fareId != null ? fareId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the fareId fields are not set
        if (!(object instanceof Fare)) {
            return false;
        }
        Fare other = (Fare) object;
        if ((this.fareId == null && other.fareId != null) || (this.fareId != null && !this.fareId.equals(other.fareId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Fare[ id=" + fareId + " ]";
    }
    
    public Long getFareId() {
        return fareId;
    }

    public void setFareId(Long fareId) {
        this.fareId = fareId;
    }

    /**
     * @return the fareBasisCode
     */
    public String getFareBasisCode() {
        return fareBasisCode;
    }

    /**
     * @param fareBasisCode the fareBasisCode to set
     */
    public void setFareBasisCode(String fareBasisCode) {
        this.fareBasisCode = fareBasisCode;
    }

    /**
     * @return the fareAmount
     */
    public BigDecimal getFareAmount() {
        return fareAmount;
    }

    /**
     * @param fareAmount the fareAmount to set
     */
    public void setFareAmount(BigDecimal fareAmount) {
        this.fareAmount = fareAmount;
    }

    /**
     * @return the cabinClass
     */
    public CabinClass getCabinClass() {
        return cabinClass;
    }

    /**
     * @param cabinClass the cabinClass to set
     */
    public void setCabinClass(CabinClass cabinClass) {
        this.cabinClass = cabinClass;
    }

}
