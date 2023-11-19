/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.Customer;
import entity.FlightReservation;
import entity.FlightSchedule;
import java.util.List;
import javax.ejb.Local;
import util.exception.NoFlightReservationsException;

/**
 *
 * @author liewvivyan
 */
@Local
public interface FlightReservationSessionBeanLocal {

   public FlightReservation createReservation(FlightReservation reservation);
       public List<FlightReservation> retrieveReservationsForFlightSchedule(FlightSchedule flightSchedule) throws NoFlightReservationsException;

        public List<FlightReservation> retrieveReservationsForCustomer(Customer c) throws NoFlightReservationsException;

}
