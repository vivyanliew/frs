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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 *
 * @author Su Shwe Yee
 */
@Entity
public class Flight implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightId;
    @Column(nullable = false, unique = true)
    @NotNull
    @Pattern(regexp = "ML[A-Za-z0-9]*")
    private String flightNumber;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private FlightRoute flightRoute;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private AircraftConfig aircraftConfig;
    
    @OneToMany
    private List<FlightReservation> flightReservations;
    @OneToOne(optional = false)
    private FlightSchedulePlan flightSchedulePlan;

    public Flight() {
    }

    public Flight(String flightNumber, FlightRoute flightRoute, AircraftConfig aircraftConfig) {
        this.flightNumber = flightNumber;
        this.flightRoute = flightRoute;
        this.aircraftConfig = aircraftConfig;
    }

    public FlightSchedulePlan getFlightSchedulePlan() {
        return flightSchedulePlan;
    }

    public void setFlightSchedulePlan(FlightSchedulePlan flightSchedulePlan) {
        this.flightSchedulePlan = flightSchedulePlan;
    }

    public List<FlightReservation> getFlightReservations() {
        return flightReservations;
    }

    public void setFlightReservations(List<FlightReservation> flightReservations) {
        this.flightReservations = flightReservations;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (flightId != null ? flightId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the flightId fields are not set
        if (!(object instanceof Flight)) {
            return false;
        }
        Flight other = (Flight) object;
        if ((this.flightId == null && other.flightId != null) || (this.flightId != null && !this.flightId.equals(other.flightId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Flight[ id=" + flightId + " ]";
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    /**
     * @return the flightNumber
     */
    public String getFlightNumber() {
        return flightNumber;
    }

    /**
     * @param flightNumber the flightNumber to set
     */
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    /**
     * @return the flightRoute
     */
    public FlightRoute getFlightRoute() {
        return flightRoute;
    }

    /**
     * @param flightRoute the flightRoute to set
     */
    public void setFlightRoute(FlightRoute flightRoute) {
        this.flightRoute = flightRoute;
    }

    /**
     * @return the aircraftConfig
     */
    public AircraftConfig getAircraftConfig() {
        return aircraftConfig;
    }

    /**
     * @param aircraftConfig the aircraftConfig to set
     */
    public void setAircraftConfig(AircraftConfig aircraftConfig) {
        this.aircraftConfig = aircraftConfig;
    }

}
