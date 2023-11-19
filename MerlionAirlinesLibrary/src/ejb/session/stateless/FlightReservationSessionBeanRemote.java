/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.FlightReservation;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author liewvivyan
 */
@Remote
public interface FlightReservationSessionBeanRemote {
   public FlightReservation createReservation(FlightReservation reservation, List<Long> flightScheduleIds);

}
