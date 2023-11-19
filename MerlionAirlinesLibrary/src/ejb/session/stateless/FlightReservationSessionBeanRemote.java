/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.FlightReservation;
import entity.FlightSchedule;
import java.util.List;
import javax.ejb.Remote;
import util.exception.NoFlightReservationsException;

/**
 *
 * @author liewvivyan
 */
@Remote
public interface FlightReservationSessionBeanRemote {
   public FlightReservation createReservation(FlightReservation reservation);

    public List<FlightReservation> retrieveReservationsForFlightSchedule(FlightSchedule flightSchedule) throws NoFlightReservationsException;

}
