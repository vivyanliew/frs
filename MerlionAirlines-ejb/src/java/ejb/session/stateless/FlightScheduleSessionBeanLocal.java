/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.Airport;
import entity.Fare;
import entity.FlightSchedule;
import java.time.LocalDateTime;
import java.util.List;
import javafx.util.Pair;
import javax.ejb.Local;
import util.exception.FlightNotFoundException;
import util.exception.FlightScheduleNotFoundException;

/**
 *
 * @author liewvivyan
 */
@Local
public interface FlightScheduleSessionBeanLocal {

    public List<FlightSchedule> getFlightSchedules(Airport departure, Airport destination, LocalDateTime date, String cabinPref) throws FlightNotFoundException;

    public Fare getSmallestFare(FlightSchedule flightSchedule, String cabinPref) throws FlightScheduleNotFoundException;
    List<Pair<FlightSchedule, FlightSchedule>> getIndirectFlightSchedules(String departureAirportCode, String destinationAirportCode, LocalDateTime departDate, String cabinPref) throws FlightNotFoundException;
}
