/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
    @ManyToOne (optional = false)
    @JoinColumn(nullable = false)
    private Flight flight;

    public FlightReservation() {
    }

    public FlightReservation(Customer customer, Flight flight) {
        this.customer = customer;
        this.flight = flight;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
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
    
}
