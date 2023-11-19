/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import util.enumeration.FlightSchedulePlanType;

/**
 *
 * @author liewvivyan
 */
@Entity
public class FlightSchedulePlan implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightSchedulePlanId;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Flight flight;
    @OneToMany (mappedBy = "flightSchedulePlan", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<FlightSchedule> flightSchedules;

    @Column(nullable = false)
    private boolean isDisabled;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FlightSchedulePlanType flightSchedulePlanType;
    @Column(nullable = false)
    private boolean isReturn;
    @Column
    private int layoverHours;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Fare> fares;

    @OneToOne
    private FlightSchedulePlan returnFlightSchedulePlan;
    
    // attributes that will only apply to recurrent flights
    @Column
    private DayOfWeek dayOfWeek;
    @Column
    private int recurrentNDay;
    @Column
    private LocalDateTime startDate;
    @Column
    private LocalDateTime endDate;

    public FlightSchedulePlan() {
        this.flightSchedules = new ArrayList<FlightSchedule>();
        this.fares = new ArrayList<Fare>();
    }
    public FlightSchedulePlan(FlightSchedulePlanType flightSchedulePlanType, DayOfWeek dayOfWeek, LocalDateTime startDate, LocalDateTime endDate, Flight flight) {
        this();
        this.flightSchedulePlanType = flightSchedulePlanType;
        this.dayOfWeek = dayOfWeek;
        this.startDate = startDate;
        this.endDate = endDate;
        this.flight = flight;
    }
    public FlightSchedulePlan(FlightSchedulePlanType flightSchedulePlanType, int recurrentNDay, LocalDateTime startDate, LocalDateTime endDate, Flight flight) {
        this.flightSchedulePlanType = flightSchedulePlanType;
        this.recurrentNDay = recurrentNDay;
        this.startDate = startDate;
        this.endDate = endDate;
        this.flight = flight;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Long getFlightSchedulePlanId() {
        return flightSchedulePlanId;
    }

    public List<FlightSchedule> getFlightSchedules() {
        return flightSchedules;
    }

    public void setFlightSchedules(List<FlightSchedule> flightSchedules) {
        this.flightSchedules = flightSchedules;
    }

    public void setFlightSchedulePlanId(Long flightSchedulePlanId) {
        this.flightSchedulePlanId = flightSchedulePlanId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (flightSchedulePlanId != null ? flightSchedulePlanId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FlightSchedulePlan)) {
            return false;
        }
        FlightSchedulePlan other = (FlightSchedulePlan) object;
        if ((this.flightSchedulePlanId == null && other.flightSchedulePlanId != null) || (this.flightSchedulePlanId != null && !this.flightSchedulePlanId.equals(other.flightSchedulePlanId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FlightSchedulePlan[ id=" + flightSchedulePlanId + " ]";
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
     * @return the fares
     */
    public List<Fare> getFares() {
        return fares;
    }

    /**
     * @param fares the fares to set
     */
    public void setFares(List<Fare> fares) {
        this.fares = fares;
    }

    /**
     * @return the returnFlightSchedulePlan
     */
    public FlightSchedulePlan getReturnFlightSchedulePlan() {
        return returnFlightSchedulePlan;
    }

    /**
     * @param returnFlightSchedulePlan the returnFlightSchedulePlan to set
     */
    public void setReturnFlightSchedulePlan(FlightSchedulePlan returnFlightSchedulePlan) {
        this.returnFlightSchedulePlan = returnFlightSchedulePlan;
    }

    /**
     * @return the flightSchedulePlanType
     */
    public FlightSchedulePlanType getFlightSchedulePlanType() {
        return flightSchedulePlanType;
    }

    /**
     * @param flightSchedulePlanType the flightSchedulePlanType to set
     */
    public void setFlightSchedulePlanType(FlightSchedulePlanType flightSchedulePlanType) {
        this.flightSchedulePlanType = flightSchedulePlanType;
    }

    /**
     * @return the isReturn
     */
    public boolean isIsReturn() {
        return isReturn;
    }

    /**
     * @param isReturn the isReturn to set
     */
    public void setIsReturn(boolean isReturn) {
        this.isReturn = isReturn;
    }

    /**
     * @return the layoverHours
     */
    public int getLayoverHours() {
        return layoverHours;
    }

    /**
     * @param layoverHours the layoverHours to set
     */
    public void setLayoverHours(int layoverHours) {
        this.layoverHours = layoverHours;
    }

    /**
     * @return the dayOfWeek
     */
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * @param dayOfWeek the dayOfWeek to set
     */
    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    /**
     * @return the recurrentNDay
     */
    public int getRecurrentNDay() {
        return recurrentNDay;
    }

    /**
     * @param recurrentNDay the recurrentNDay to set
     */
    public void setRecurrentNDay(int recurrentNDay) {
        this.recurrentNDay = recurrentNDay;
    }

    /**
     * @return the startDate
     */
    public LocalDateTime getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public LocalDateTime getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

}
