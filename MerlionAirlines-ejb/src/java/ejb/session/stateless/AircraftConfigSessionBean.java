/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.AircraftConfig;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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
    
    //public List<AircraftConfig> getAllAircraftConfigs() {
        //Query query =  em.createQuery("SELECT ac FROM AircraftConfig ac WHERE mg.student.studentNumber = :inStudentNumber");

    //}
}
