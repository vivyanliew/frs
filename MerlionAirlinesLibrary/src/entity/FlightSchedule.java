/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
//import java.util.Date;
import java.util.List;
import javafx.util.Pair;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author liewvivyan
 */
@Entity
public class FlightSchedule implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightScheduleId;
    @Column(nullable = false)
    @NotNull
    private LocalDateTime departureDateTime;
    @Column(nullable = false)
    @NotNull
    private Duration flightDurationHours;
    @Column(nullable = false)
    private boolean isDisabled;
    
    @OneToOne(optional = false,mappedBy = "flightSchedule", cascade = CascadeType.REMOVE)
    private SeatInventory seatInventory;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private FlightSchedulePlan flightSchedulePlan;
    @ManyToMany
    private List<FlightReservation> flightReservations;
    
    public FlightSchedule() {
    }

    public FlightSchedule(LocalDateTime departureDateTime, Duration flightDurationHours) {
        this.departureDateTime = departureDateTime;
        this.flightDurationHours = flightDurationHours;
    }
    public SeatInventory getSeatInventory() {
        return seatInventory;
    }

    public void setSeatInventory(SeatInventory seatInventory) {
        this.seatInventory = seatInventory;
    }

    public List<FlightReservation> getFlightReservations() {
        return flightReservations;
    }

    public void setFlightReservations(List<FlightReservation> flightReservations) {
        this.flightReservations = flightReservations;
    }
    public LocalDateTime getDepartureDateTime() {
        return this.departureDateTime;
    }

    public void setDepartureDateTime(LocalDateTime departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public Duration getFlightDurationHours() {
        return flightDurationHours;
    }

    public void setFlightDurationHours(Duration flightDurationHours) {
        this.flightDurationHours = flightDurationHours;
    }

    public Long getFlightScheduleId() {
        return flightScheduleId;
    }

    public void setFlightScheduleId(Long flightScheduleId) {
        this.flightScheduleId = flightScheduleId;
    }

    public LocalDateTime getArrivalDateTime() {
        LocalDateTime arrivalDateTime = this.departureDateTime.plus(this.flightDurationHours);
        return arrivalDateTime;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (flightScheduleId != null ? flightScheduleId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FlightSchedule)) {
            return false;
        }
        FlightSchedule other = (FlightSchedule) object;
        if ((this.flightScheduleId == null && other.flightScheduleId != null) || (this.flightScheduleId != null && !this.flightScheduleId.equals(other.flightScheduleId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FlightSchedule[ id=" + flightScheduleId + " ]";
    }

    /**
     * @return the isDisabled
     */
    public boolean isIsDisabled() {
        return isDisabled;
    }

    /**
     * @param isDisabled the isDisabled to set
     */
    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    /**
     * @return the flightSchedulePlan
     */
    public FlightSchedulePlan getFlightSchedulePlan() {
        return flightSchedulePlan;
    }

    /**
     * @param flightSchedulePlan the flightSchedulePlan to set
     */
    public void setFlightSchedulePlan(FlightSchedulePlan flightSchedulePlan) {
        this.flightSchedulePlan = flightSchedulePlan;
    }

    /**
     * @return the flightSchedulePlan
     */
    public static class FlightScheduleComparator implements Comparator<FlightSchedule> {
        @Override
        public int compare(FlightSchedule fs1, FlightSchedule fs2) {
            if (fs1.getDepartureDateTime().compareTo(fs2.getDepartureDateTime())>0) {
                return 1;
            } else if (fs1.getDepartureDateTime().compareTo(fs2.getDepartureDateTime()) < 0) {
                return -1;
            } else {
                return 0;
            }
        }
    }
    
    public static class IndirectFlightScheduleComparator implements Comparator<Pair<FlightSchedule,FlightSchedule>> {
        @Override
        public int compare(Pair<FlightSchedule,FlightSchedule> f1, Pair<FlightSchedule,FlightSchedule> f2) {
            if (f1.getKey().getDepartureDateTime().compareTo(f2.getKey().getDepartureDateTime()) > 0) {
                return 1;
            }else if (f1.getKey().getDepartureDateTime().compareTo(f2.getKey().getDepartureDateTime()) < 0) {
                return -1;
            } else {
                return 0;
            }
        }
    }
    

}
