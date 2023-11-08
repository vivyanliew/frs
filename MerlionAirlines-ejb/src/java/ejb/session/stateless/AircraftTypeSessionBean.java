/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.AircraftType;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author liewvivyan
 */
@Stateless
public class AircraftTypeSessionBean implements AircraftTypeSessionBeanRemote, AircraftTypeSessionBeanLocal {

    @PersistenceContext(unitName = "MerlionAirlines-ejbPU")
    private EntityManager em;
    
    @Override
    public AircraftType createNewAircraftType(AircraftType aircraftType) {
        em.persist(aircraftType);
        em.flush();
        return aircraftType;
    }
    
   public AircraftType retrieveAircraftById(Long id) {
       AircraftType a = em.find(AircraftType.class,id);
       return a;
   }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
}

