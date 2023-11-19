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
import java.util.Comparator;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.FlightRouteNotFoundException;

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
        returnFlightRoute.setReturnRoute(mainFlightRoute);

        returnFlightRoute.setIsReturn(true);
        mainFlightRoute.setIsReturn(true);

    }
    
    @Override
    public FlightRoute hasReturnFlightRoute(FlightRoute flightRoute) {
        try {
             Query query = em.createQuery("SELECT fr FROM FlightRoute fr WHERE fr.originAirport = :inputOrigin AND fr.destinationAirport = :inputDestination");
            query.setParameter("inputOrigin", flightRoute.getDestinationAirport());
            query.setParameter("inputDestination", flightRoute.getDestinationAirport());
            return (FlightRoute) query.getSingleResult();
        }catch (NoResultException e) {
            return null;
        }
       
            
    }

    @Override
    public boolean isDuplicateFlightRoute(FlightRoute flightRoute) {
        try {
            Query query = em.createQuery("SELECT fr FROM FlightRoute fr WHERE fr.originAirport = :inputOrigin AND fr.destinationAirport = :inputDestination");
            query.setParameter("inputOrigin", flightRoute.getOriginAirport());
            query.setParameter("inputDestination", flightRoute.getDestinationAirport());

            query.getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    @Override
    public List<FlightRoute> getFlightRoutes() {
        /**
         * Query query = em.createQuery("SELECT fr FROM FlightRoute fr WHERE
         * fr.originAirport"); List<FlightRoute> routes = query.getResultList();
         * List<String> names = new ArrayList<>(); for (FlightRoute f: routes) {
         * names.add(f.getOriginAirport().getAirportName()); }
         */
        Query query = em.createQuery("SELECT DISTINCT fr FROM FlightRoute fr "
                + "LEFT JOIN fr.returnRoute rfr "
                + "WHERE (rfr IS NULL OR rfr.flightRouteId < fr.flightRouteId)");
       

        List<FlightRoute> result = query.getResultList();
        List<String> flightRouteNames;

        //Query query = em.createQuery("SELECT fr.originAirport.airportName FROM FlightRoute fr");
        //List<String> names = query.getResultList();
        Collections.sort(result, new FlightRouteComparator());
        List<FlightRoute> sortedRoutes = new ArrayList<>();
        /**for (String name : result) {
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
        }*/
        
        
        for (FlightRoute fr : result) {
            sortedRoutes.add(fr);
            if (fr.getReturnRoute() != null) {
                sortedRoutes.add(fr.getReturnRoute());
            }
        }
        return sortedRoutes;
    }

  
    @Override
    public void deleteFlightRoute(String origin, String destination) throws FlightRouteNotFoundException {
        
        Query query = em.createQuery("SELECT fr FROM FlightRoute fr WHERE "
                + "fr.originAirport.iataCode = :inputOrigin AND "
                + "fr.destinationAirport.iataCode = :inputDestination");

        query.setParameter("inputOrigin", origin).setParameter("inputDestination", destination);
         
        FlightRoute currRoute;
        
        try {
            currRoute = (FlightRoute) query.getSingleResult();
        } catch (NoResultException ex) {
            throw new FlightRouteNotFoundException("Flight route with the given origin-destination airport does not exist!");
        }
        
        if (currRoute.getFlights().size() == 0) { //not used by any flights
            em.remove(currRoute);
            if (currRoute.getReturnRoute() != null) { //remove return flight route as well
                em.remove(currRoute.getReturnRoute());
            }
        } else {
            currRoute.setIsDisabled(true);
            if (currRoute.getReturnRoute() != null) {
                FlightRoute returnRoute = currRoute.getReturnRoute();
                returnRoute.setIsDisabled(true);
            }
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

class FlightRouteComparator implements Comparator<FlightRoute> {

    @Override
    public int compare(FlightRoute fr1, FlightRoute fr2) {
        return fr1.getOriginAirport().getAirportName().compareTo(fr2.getOriginAirport().getAirportName());
    }

}
