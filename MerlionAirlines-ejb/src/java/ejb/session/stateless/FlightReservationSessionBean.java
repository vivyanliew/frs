/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Customer;
import entity.FlightReservation;
import entity.FlightSchedule;
import entity.Passenger;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import util.exception.NoFlightReservationsException;

/**
 *
 * @author liewvivyan
 */
@Stateless
public class FlightReservationSessionBean implements FlightReservationSessionBeanRemote, FlightReservationSessionBeanLocal {

    @PersistenceContext(unitName = "MerlionAirlines-ejbPU")
    private EntityManager em;

    @Override
    public FlightReservation createReservation(FlightReservation reservation) {
        em.persist(reservation);

        List<FlightSchedule> fs = reservation.getFlightSchedules();
        List<Passenger> passengers = reservation.getPassengers();
        Customer customer = reservation.getCustomer();

        for (FlightSchedule flightSchedule : fs) {
            flightSchedule = em.find(FlightSchedule.class, flightSchedule.getFlightScheduleId());
            flightSchedule.getFlightReservations().add(reservation);
            reservation.getFlightSchedules().add(flightSchedule);
        }

        for (Passenger p : passengers) {
            em.persist(p);
            p.setFlightReservations(reservation);
        }

        customer = em.find(Customer.class, customer.getCustomerId());
        customer.getFlightReservations().add(reservation);

        em.flush();
        return reservation;

    }

    public List<FlightReservation> retrieveReservationsForFlightSchedule(FlightSchedule flightSchedule) throws NoFlightReservationsException {

        flightSchedule = em.find(FlightSchedule.class, flightSchedule.getFlightScheduleId());
        TypedQuery<FlightReservation> query = em.createQuery(
                "SELECT DISTINCT fr "
                + "FROM FlightReservation fr "
                + "JOIN fr.flightSchedules fs "
                + "WHERE fs = :flightSchedule", FlightReservation.class);

        query.setParameter("flightSchedule", flightSchedule);

        List<FlightReservation> flightReservations = query.getResultList();

        if (flightReservations.isEmpty()) {
            throw new NoFlightReservationsException();
        }
        return flightReservations;
    }

    @Override
    public List<FlightReservation> retrieveReservationsForCustomer(Customer c) throws NoFlightReservationsException {

        Query query = em.createQuery("SELECT fr FROM FlightReservation fr WHERE fr.customer = :inputCustomer").setParameter("inputCustomer", c);
        List<FlightReservation> flightReservations = query.getResultList();

        if (flightReservations.isEmpty()) {
            throw new NoFlightReservationsException();
        }
        return flightReservations;
    }

}
