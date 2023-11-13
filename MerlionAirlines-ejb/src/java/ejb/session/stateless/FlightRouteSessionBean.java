/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Flight;
import entity.FlightRoute;
import entity.FlightSchedule;
import entity.FlightSchedulePlan;
import java.util.ArrayList;
import java.util.Collections;
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
        //mainFlightRoute.setIsReturn(false);
        returnFlightRoute.setIsReturn(true);
        //returnFlightRoute.setReturnRoute(mainFlightRoute);
    }
    
    @Override
    public List<FlightRoute> getFlightRoutes() {
        /**
         * Query query = em.createQuery("SELECT fr FROM FlightRoute fr WHERE
         * fr.originAirport"); List<FlightRoute> routes = query.getResultList();
         * List<String> names = new ArrayList<>(); for (FlightRoute f: routes) {
         * names.add(f.getOriginAirport().getAirportName());
        }
         */
        Query query = em.createQuery("SELECT DISTINCT fr.originAirport.airportName FROM FlightRoute fr");
        List<String> names = query.getResultList();
        Collections.sort(names);
        List<FlightRoute> sortedRoutes = new ArrayList<>();
        for (String name : names) {
            Query query1 = em.createQuery("SELECT fr FROM FlightRoute fr WHERE fr.originAirport.airportName = :n");
            query1.setParameter("n", name);
            List<FlightRoute> flightRoute = query1.getResultList();
            sortedRoutes.addAll(flightRoute);
        }
        List<FlightRoute> ans = new ArrayList<>();
        for (FlightRoute f : sortedRoutes) {
            if (!f.isReturn()) {
                ans.add(f);
                if (f.getReturnRoute() != null) {
                    FlightRoute returnRoute = f.getReturnRoute();
                    ans.add(returnRoute);
                }
            }
        }
        return ans;
    }

    @Override
    public void deleteFlightRoute(Long flightRouteId) {
        FlightRoute currRoute = em.find(FlightRoute.class, flightRouteId);
        if (currRoute.getFlights().size() == 0) { //not used by any flights
            em.remove(currRoute);
        } else {
            currRoute.setIsDisabled(true);
            List<Flight> flights = currRoute.getFlights();
            for (Flight f : flights) {
                f.setIsDisabled(true);
                for (FlightSchedulePlan flightSchedulePlan : f.getFlightSchedulePlans()) {
                    flightSchedulePlan.setIsDisabled(true);
                    for (FlightSchedule fs : flightSchedulePlan.getFlightSchedules()) {
                        fs.setIsDisabled(true);
                    }
                }
            }        
        }
    }

    @Override
    public List<FlightRoute> retrieveAllFlightRoutes() {
        Query query = em.createQuery("SELECT fr FROM FlightRoute fr");
        return query.getResultList();
    }
}
