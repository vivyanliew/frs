/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.FlightRoute;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author liewvivyan
 */
@Local
public interface FlightRouteSessionBeanLocal {

    public FlightRoute createNewFlightRoute(FlightRoute flightRoute);

    public void setReturnRoute(FlightRoute fr1, FlightRoute fr2);

    public List<FlightRoute> getFlightRoutes();

    public void deleteFlightRoute(Long flightRouteId);
    
}
