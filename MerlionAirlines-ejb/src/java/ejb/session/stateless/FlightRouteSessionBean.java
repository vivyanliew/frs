/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.FlightRoute;
import java.util.ArrayList;
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
public class FlightRouteSessionBean implements FlightRouteSessionBeanRemote, FlightRouteSessionBeanLocal {

    @PersistenceContext(unitName = "MerlionAirlines-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public FlightRoute createNewFlightRoute(FlightRoute flightRoute) {
        em.persist(flightRoute);
        em.flush();
        return flightRoute;
    }
    
    @Override
    public void setReturnRoute(FlightRoute mainFlightRoute, FlightRoute returnFlightRoute) {
        mainFlightRoute = em.find(FlightRoute.class, mainFlightRoute.getFlightRouteId());
        returnFlightRoute = em.find(FlightRoute.class, returnFlightRoute.getFlightRouteId());
        mainFlightRoute.setReturnRoute(returnFlightRoute);
        //returnFlightRoute.setReturnRoute(mainFlightRoute);
    }
    
//    public List<FlightRoute> getFlightRoutes() {
//        Query query =  em.createQuery("SELECT fr FROM FlightRoute fr");
//        List<FlightRoute> routes = query.getResultList();
//        List<FlightRoute> sortedRoutes = new ArrayList<>();
//        for (FlightRoute f: routes) {
//            if (f.getReturnRoute()!=null) {
//                
//            }
//        }
//    }
}
