/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author liewvivyan
 */
@Entity
public class FlightReservation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightReservationId;
    
    @ManyToOne (optional = false)
    @JoinColumn(nullable = false)
    private Customer customer;
    @ManyToMany
    private List<FlightSchedule> flightSchedules;
    
    @OneToMany
    private List<Passenger> passengers;
    @ManyToMany
    private List<CabinClass> cabinClasses;
    @Column(nullable = false)
    private String ccNum;
    @Column(nullable = false)
    private String ccExpiryDate;
    @Column(nullable = false)
    private int cvv;
    @Column
    private List<BigDecimal> fareAmount;
    @Column
    private List<String> fareBasisCode;
    
    public FlightReservation() {
        this.fareAmount = new ArrayList<>();
        this.fareBasisCode = new ArrayList<>();
        this.passengers = new ArrayList<>();
        this.flightSchedules = new ArrayList<>();
        this.cabinClasses = new ArrayList<>();
    }

    public List<FlightSchedule> getFlightSchedules() {
        return flightSchedules;
    }

    public void setFlightSchedules(List<FlightSchedule> flightSchedules) {
        this.flightSchedules = flightSchedules;
    }
    

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Long getFlightReservationId() {
        return flightReservationId;
    }

    public void setFlightReservationId(Long flightReservationId) {
        this.flightReservationId = flightReservationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (flightReservationId != null ? flightReservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FlightReservation)) {
            return false;
        }
        FlightReservation other = (FlightReservation) object;
        if ((this.flightReservationId == null && other.flightReservationId != null) || (this.flightReservationId != null && !this.flightReservationId.equals(other.flightReservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FlightReservation[ id=" + flightReservationId + " ]";
    }

    /**
     * @return the passengers
     */
    public List<Passenger> getPassengers() {
        return passengers;
    }

    /**
     * @param passengers the passengers to set
     */
    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }

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
     * @return the ccNum
     */
    public String getCcNum() {
        return ccNum;
    }

    /**
     * @param ccNum the ccNum to set
     */
    public void setCcNum(String ccNum) {
        this.ccNum = ccNum;
    }

    /**
     * @return the ccExpiryDate
     */
    public String getCcExpiryDate() {
        return ccExpiryDate;
    }

    /**
     * @param ccExpiryDate the ccExpiryDate to set
     */
    public void setCcExpiryDate(String ccExpiryDate) {
        this.ccExpiryDate = ccExpiryDate;
    }

    /**
     * @return the cvv
     */
    public int getCvv() {
        return cvv;
    }

    /**
     * @param cvv the cvv to set
     */
    public void setCvv(int cvv) {
        this.cvv = cvv;
    }

    /**
     * @return the fareAmount
     */
    public List<BigDecimal> getFareAmount() {
        return fareAmount;
    }

    /**
     * @param fareAmount the fareAmount to set
     */
    public void setFareAmount(List<BigDecimal> fareAmount) {
        this.fareAmount = fareAmount;
    }

    /**
     * @return the fareBasisCode
     */
    public List<String> getFareBasisCode() {
        return fareBasisCode;
    }

    /**
     * @param fareBasisCode the fareBasisCode to set
     */
    public void setFareBasisCode(List<String> fareBasisCode) {
        this.fareBasisCode = fareBasisCode;
    }
    
}
