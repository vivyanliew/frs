/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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
    private Date departureDateTime;
    @Column(nullable = false)
    @NotNull
    private int flightDurationHours;

    @ManyToMany
    private List<FlightSchedulePlan> flightSchedulePlans;

    public FlightSchedule() {
    }

    public FlightSchedule(Date departureDateTime, int flightDurationHours) {
        this.departureDateTime = departureDateTime;
        this.flightDurationHours = flightDurationHours;
    }

    public List<FlightSchedulePlan> getFlightSchedulePlans() {
        return flightSchedulePlans;
    }

    public void setFlightSchedulePlans(List<FlightSchedulePlan> flightSchedulePlans) {
        this.flightSchedulePlans = flightSchedulePlans;
    }

    public String getDepartureDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(this.departureDateTime);
    }

    public void setDepartureDateTime(Date departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public int getFlightDurationHours() {
        return flightDurationHours;
    }

    public void setFlightDurationHours(int flightDurationHours) {
        this.flightDurationHours = flightDurationHours;
    }

    public Long getFlightScheduleId() {
        return flightScheduleId;
    }

    public void setFlightScheduleId(Long flightScheduleId) {
        this.flightScheduleId = flightScheduleId;
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

}
