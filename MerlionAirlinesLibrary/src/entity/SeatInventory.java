/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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
    @OneToMany(fetch = FetchType.EAGER)
    private List<CabinClass> allCabinClasses;
    private List<List<String>> availableSeats;
    private List<List<String>> reservedSeats;
    private List<List<String>> balanceSeats;

    public SeatInventory() {
        this.allCabinClasses = new ArrayList<CabinClass>();
        this.availableSeats = new ArrayList<List<String>>();
        this.reservedSeats = new ArrayList<List<String>>();
        this.balanceSeats = new ArrayList<List<String>>();
    }
    
    public SeatInventory(FlightSchedule fs) {
        this();
        this.flightSchedule = fs;
    }
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

    /**
     * @return the flightSchedule
     */
    public FlightSchedule getFlightSchedule() {
        return flightSchedule;
    }

    /**
     * @param flightSchedule the flightSchedule to set
     */
    public void setFlightSchedule(FlightSchedule flightSchedule) {
        this.flightSchedule = flightSchedule;
    }

    /**
     * @return the allCabinClasses
     */
    public List<CabinClass> getAllCabinClasses() {
        return allCabinClasses;
    }

    /**
     * @param allCabinClasses the allCabinClasses to set
     */
    public void setAllCabinClasses(List<CabinClass> allCabinClasses) {
        this.allCabinClasses = allCabinClasses;
    }

    /**
     * @return the availableSeats
     */
    public List<List<String>> getAvailableSeats() {
        return availableSeats;
    }

    /**
     * @param availableSeats the availableSeats to set
     */
    public void setAvailableSeats(List<List<String>> availableSeats) {
        this.availableSeats = availableSeats;
    }

    /**
     * @return the reservedSeats
     */
    public List<List<String>> getReservedSeats() {
        return reservedSeats;
    }

    /**
     * @param reservedSeats the reservedSeats to set
     */
    public void setReservedSeats(List<List<String>> reservedSeats) {
        this.reservedSeats = reservedSeats;
    }

    /**
     * @return the balanceSeats
     */
    public List<List<String>> getBalanceSeats() {
        return balanceSeats;
    }

    /**
     * @param balanceSeats the balanceSeats to set
     */
    public void setBalanceSeats(List<List<String>> balanceSeats) {
        this.balanceSeats = balanceSeats;
    }

    
    
    
}
