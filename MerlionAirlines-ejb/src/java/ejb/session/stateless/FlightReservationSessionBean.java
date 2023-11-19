/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.FlightReservation;
import entity.FlightSchedule;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author liewvivyan
 */
@Stateless
public class FlightReservationSessionBean implements FlightReservationSessionBeanRemote, FlightReservationSessionBeanLocal {

    @PersistenceContext(unitName = "MerlionAirlines-ejbPU")
    private EntityManager em;

    @Override
    public FlightReservation createReservation(FlightReservation reservation, List<Long> flightScheduleIds) {
        em.persist(reservation);
        for (Long fsId : flightScheduleIds) {
            FlightSchedule fs = em.find(FlightSchedule.class, fsId);
            fs.getFlightReservations().add(reservation);
            reservation.getFlightSchedules().add(fs);
        }
        
        em.flush();
        return reservation;
        
    }
}
