/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Airport;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.AirportNotFoundException;

/**
 *
 * @author liewvivyan
 */
@Stateless
public class AirportSessionBean implements AirportSessionBeanRemote, AirportSessionBeanLocal {

    @PersistenceContext(unitName = "MerlionAirlines-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public Airport createNewAirport(Airport airport) {
        em.persist(airport);
        em.flush();
        return airport;
    }
    @Override
    public Airport retrieveAirportByIATACode(String code) throws AirportNotFoundException {
        
        Query query = em.createQuery("SELECT ap FROM Airport ap WHERE ap.iataCode = :inputCode").setParameter("inputCode", code);
        
        try {
            return (Airport) query.getSingleResult();
        } catch (NoResultException ex) {
            throw new AirportNotFoundException("No airport found in records!");
        }
        /**if (query!=null) {
            return (Airport) query.getSingleResult();
        } else {
            throw new AirportNotFoundException("No airport found in records!");
        }*/
        
    }
}
