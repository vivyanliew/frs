/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 *
 * @author liewvivyan
 */
@Entity
public class SeatInventory implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatInventoryId;
    @OneToOne(optional = false)
    @JoinColumn(nullable = false) 
    private FlightSchedule flightSchedule;
    private List<CabinClass> allCabinClasses;
    private List<HashSet<String>> availableSeats;

    public Long getSeatInventoryId() {
        return seatInventoryId;
    }

    public void setSeatInventoryId(Long seatInventoryId) {
        this.seatInventoryId = seatInventoryId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (seatInventoryId != null ? seatInventoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SeatInventory)) {
            return false;
        }
        SeatInventory other = (SeatInventory) object;
        if ((this.seatInventoryId == null && other.seatInventoryId != null) || (this.seatInventoryId != null && !this.seatInventoryId.equals(other.seatInventoryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.SeatInventory[ id=" + seatInventoryId + " ]";
    }
    
}
