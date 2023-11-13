/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.FlightSchedule;
import entity.FlightSchedulePlan;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author liewvivyan
 */
@Stateless
public class FlightSchedulePlanSessionBean implements FlightSchedulePlanSessionBeanRemote, FlightSchedulePlanSessionBeanLocal {

    @PersistenceContext(unitName = "MerlionAirlines-ejbPU")
    private EntityManager em;
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public void deleteFlightSchedulePlan(Long flightSchedulePlanId) {
        FlightSchedulePlan flightSchedulePlan = em.find(FlightSchedulePlan.class,flightSchedulePlanId);
        List<FlightSchedule> flightSchedules = flightSchedulePlan.getFlightSchedules();
        
        for (FlightSchedule fs : flightSchedules) {
            if (fs.getFlightReservations().size()==0) {
                em.remove(fs);
            } else {
                fs.setIsDisabled(true);
                for (FlightSchedulePlan fsp : fs.getFlightSchedulePlans()) {
                    fsp.setIsDisabled(true);
                }
            }
        }
        if (!flightSchedulePlan.isIsDisabled()) {
            em.remove(flightSchedulePlan);
        }
        
    }
}
