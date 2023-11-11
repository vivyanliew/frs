/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.AircraftConfig;
import entity.CabinClass;
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
    
    @Override
    public List<AircraftConfig> getAllAircraftConfigs() {
        Query query =  em.createQuery("SELECT ac FROM AircraftConfig ac ORDER BY ac.aircraftType.aircraftTypeName ASC, ac.aircraftConfigName ASC");
        List<AircraftConfig> results = query.getResultList();
        for (AircraftConfig a : results) {
            a.getAircraftType().getAircraftTypeName();
            a.getAircraftConfigName();
        }
        return results;

    }
    
    @Override
    public AircraftConfig viewAircraftConfigDetails(String aircraftConfigName) {
        Query query = em.createQuery("SELECT a FROM AircraftConfig a WHERE a.aircraftConfigName = :name");
        
//        Query query =  em.createQuery("SELECT ac FROM AircraftConfig ac WEHRE ac.aircraftConfigName = :name");
        query.setParameter("name",aircraftConfigName);
        AircraftConfig a = (AircraftConfig)query.getSingleResult();
        a.getAircraftConfigName();
        a.getAircraftType().getAircraftTypeName();
        a.getCabinClasses().size();
        for (CabinClass c: a.getCabinClasses()){
            c.getNumRows();
            c.getNumSeatsAbreast();
            c.getActualSeatConfigPerCol();
            c.getMaxSeatCapacity();
        }
        return a;
    }
}
