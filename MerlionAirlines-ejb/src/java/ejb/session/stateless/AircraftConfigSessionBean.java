/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.AircraftConfig;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author liewvivyan
 */
@Stateless
public class AircraftConfigSessionBean implements AircraftConfigSessionBeanRemote, AircraftConfigSessionBeanLocal {

    @PersistenceContext(unitName = "MerlionAirlines-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public AircraftConfig createNewAircraftConfig(AircraftConfig aircraftConfig) {
        em.persist(aircraftConfig);
        em.flush();
        return aircraftConfig;
    }
}
