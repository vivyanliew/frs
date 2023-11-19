/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB31/SingletonEjbClass.java to edit this template
 */
package ejb.session.singleton;

import ejb.session.stateless.AircraftConfigSessionBeanLocal;
import ejb.session.stateless.AircraftTypeSessionBeanLocal;
import ejb.session.stateless.AirportSessionBeanLocal;
import ejb.session.stateless.CabinClassSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.FlightRouteSessionBeanLocal;
import ejb.session.stateless.FlightSchedulePlanSessionBeanLocal;
import ejb.session.stateless.FlightSessionBeanLocal;
import ejb.session.stateless.PartnerSessionBeanLocal;
import entity.AircraftConfig;
import entity.AircraftType;
import entity.Airport;
import entity.CabinClass;
import entity.Employee;
import entity.Fare;
import entity.Flight;
import entity.FlightRoute;
import entity.FlightSchedule;
import entity.FlightSchedulePlan;
import entity.Partner;
import entity.SeatInventory;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.EmployeeUserRole;
import util.enumeration.FlightSchedulePlanType;
import util.exception.NonUniqueFlightNumException;

/**
 *
 * @author liewvivyan
 */
@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {

    @EJB(name = "AircraftTypeSessionBeanLocal")
    private AircraftTypeSessionBeanLocal aircraftTypeSessionBeanLocal;

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    @EJB
    private PartnerSessionBeanLocal partnerSessionBeanLocal;

    @EJB
    private AirportSessionBeanLocal airportSessionBeanLocal;

    @EJB
    private AircraftConfigSessionBeanLocal aircraftConfigSessionBeanLocal;

    @EJB
    private CabinClassSessionBeanLocal cabinClassSessionBeanLocal;

    @EJB
    private FlightRouteSessionBeanLocal flightRouteSessionBeanLocal;

    @EJB
    private FlightSessionBeanLocal flightSessionBeanLocal;

    @PersistenceContext(unitName = "MerlionAirlines-ejbPU")
    private EntityManager em;

    @EJB
    private FlightSchedulePlanSessionBeanLocal flightSchedulePlanSessionBeanLocal;
    

    @PostConstruct
    public void postConstruct() {
        
        Query query = em.createQuery("SELECT e FROM Employee e");
        List<Employee> employeeList = query.getResultList();
        if (employeeList.isEmpty()) {
            doDataInit();
        }
        
        
     
    }
    void doDataInit() {
        
            employeeSessionBeanLocal.createNewEmployee(new Employee("Fleet Manager", "fleetmanager@mlair.com.sg", "password", EmployeeUserRole.FLEETMANAGER));
            employeeSessionBeanLocal.createNewEmployee(new Employee("Route Planner", "routeplanner@mlair.com.sg", "password", EmployeeUserRole.ROUTEPLANNER));
            employeeSessionBeanLocal.createNewEmployee(new Employee("Schedule Manager", "schedulemanager@mlair.com.sg", "password", EmployeeUserRole.SCHEDULEMANAGER));
            employeeSessionBeanLocal.createNewEmployee(new Employee("Sales Manager", "salesmanager@mlair.com.sg", "password", EmployeeUserRole.SALESMANAGER));

            partnerSessionBeanLocal.createNewPartner(new Partner("Holiday.com", "mlair@holiday.com", "password"));

            Airport sin = airportSessionBeanLocal.createNewAirport(new Airport("Changi", "SIN", "Singapore", "Singapore", "Singapore"));
            Airport hkg = airportSessionBeanLocal.createNewAirport(new Airport("Hong Kong", "HKG", "Chek Lap Kok", "Hong Kong", "China"));
            Airport tpe = airportSessionBeanLocal.createNewAirport(new Airport("Taoyuan", "TPE", "Taoyuan", "Taipei", "Taiwan R.O.C."));
            Airport nrt = airportSessionBeanLocal.createNewAirport(new Airport("Narita", "NRT", "Narita", "Chiba", "Japan"));
            Airport syd = airportSessionBeanLocal.createNewAirport(new Airport("Sydney", "SYD", "Sydney", "New South Wales", "Australia"));

            AircraftType at1 = aircraftTypeSessionBeanLocal.createNewAircraftType(new AircraftType("Boeing 737", 200));
            AircraftType at2 = aircraftTypeSessionBeanLocal.createNewAircraftType(new AircraftType("Boeing 747", 400));

            CabinClass cc1 = new CabinClass("Y", 1, 30, 6, "3-3");
            CabinClass cc2 = new CabinClass("F", 1, 5, 2, "1-1");
            CabinClass cc3 = new CabinClass("J", 1, 5, 4, "2-2");
            CabinClass cc4 = new CabinClass("Y", 1, 25, 6, "3-3");
            CabinClass cc5 = new CabinClass("Y", 2, 38, 10, "3-4-3");
            CabinClass cc6 = new CabinClass("J", 2, 5, 6, "2-2-2");
            CabinClass cc7 = new CabinClass("Y", 2, 32, 10, "3-4-3");

            cc1 = cabinClassSessionBeanLocal.createCabinClass(cc1);
            cc2 = cabinClassSessionBeanLocal.createCabinClass(cc2);
            cc3 = cabinClassSessionBeanLocal.createCabinClass(cc3);
            cc4 = cabinClassSessionBeanLocal.createCabinClass(cc4);
            cc5 = cabinClassSessionBeanLocal.createCabinClass(cc5);
            cc6 = cabinClassSessionBeanLocal.createCabinClass(cc6);
            cc7 = cabinClassSessionBeanLocal.createCabinClass(cc7);

            // Boeing 737 All Economy 
            AircraftConfig ac1 = new AircraftConfig(at1, "Boeing 737 All Economy", 1);
            at1.getAircraftConfigs().add(ac1);
            ac1.getCabinClasses().add(cc1);
            //ac1.updateMaxSeatCapacity();

            // Boeing 737 Three Classes
            AircraftConfig ac2 = new AircraftConfig(at1, "Boeing 737 Three Classes", 3);
            at1.getAircraftConfigs().add(ac2);
            ac2.getCabinClasses().add(cc2);
            ac2.getCabinClasses().add(cc3);
            ac2.getCabinClasses().add(cc4);
            //ac2.updateMaxSeatCapacity();

            // Boeing 747 All Economy
            AircraftConfig ac3 = new AircraftConfig(at2, "Boeing 747 All Economy", 1);
            at2.getAircraftConfigs().add(ac3);
            ac3.getCabinClasses().add(cc5);
            //ac3.updateMaxSeatCapacity();

            // Boeing 747 Three Classes
            AircraftConfig ac4 = new AircraftConfig(at2, "Boeing 747 Three Classes", 3);
            at2.getAircraftConfigs().add(ac4);
            ac4.getCabinClasses().add(cc2);
            ac4.getCabinClasses().add(cc6);
            ac4.getCabinClasses().add(cc7);
            //ac4.updateMaxSeatCapacity();

            aircraftConfigSessionBeanLocal.createNewAircraftConfig(ac1);
            aircraftConfigSessionBeanLocal.createNewAircraftConfig(ac2);
            aircraftConfigSessionBeanLocal.createNewAircraftConfig(ac3);
            aircraftConfigSessionBeanLocal.createNewAircraftConfig(ac4);

            FlightRoute sinHKG = flightRouteSessionBeanLocal.createNewFlightRoute(new FlightRoute(sin, hkg));
            FlightRoute hkgSIN = flightRouteSessionBeanLocal.createNewFlightRoute(new FlightRoute(hkg, sin));
            flightRouteSessionBeanLocal.setReturnRoute(sinHKG, hkgSIN);

            FlightRoute sinTPE = flightRouteSessionBeanLocal.createNewFlightRoute(new FlightRoute(sin, tpe));
            FlightRoute tpeSIN = flightRouteSessionBeanLocal.createNewFlightRoute(new FlightRoute(tpe, sin));
            flightRouteSessionBeanLocal.setReturnRoute(sinTPE, tpeSIN);

            FlightRoute sinNRT = flightRouteSessionBeanLocal.createNewFlightRoute(new FlightRoute(sin, nrt));
            FlightRoute nrtSIN = flightRouteSessionBeanLocal.createNewFlightRoute(new FlightRoute(nrt, sin));
            flightRouteSessionBeanLocal.setReturnRoute(sinNRT, nrtSIN);

            FlightRoute hkgNRT = flightRouteSessionBeanLocal.createNewFlightRoute(new FlightRoute(hkg, nrt));
            FlightRoute nrtHKG = flightRouteSessionBeanLocal.createNewFlightRoute(new FlightRoute(nrt, hkg));
            flightRouteSessionBeanLocal.setReturnRoute(hkgNRT, nrtHKG);

            FlightRoute tpeNRT = flightRouteSessionBeanLocal.createNewFlightRoute(new FlightRoute(tpe, nrt));
            FlightRoute nrtTPE = flightRouteSessionBeanLocal.createNewFlightRoute(new FlightRoute(nrt, tpe));
            flightRouteSessionBeanLocal.setReturnRoute(tpeNRT, nrtTPE);

            FlightRoute sinSYD = flightRouteSessionBeanLocal.createNewFlightRoute(new FlightRoute(sin, syd));
            FlightRoute sydSIN = flightRouteSessionBeanLocal.createNewFlightRoute(new FlightRoute(syd, sin));
            flightRouteSessionBeanLocal.setReturnRoute(sinSYD, sydSIN);

            FlightRoute sydNRT = flightRouteSessionBeanLocal.createNewFlightRoute(new FlightRoute(syd, nrt));
            FlightRoute nrtSYD = flightRouteSessionBeanLocal.createNewFlightRoute(new FlightRoute(nrt, syd));
            flightRouteSessionBeanLocal.setReturnRoute(sydNRT, nrtSYD);

            
            try {
                Flight ml111 = new Flight("ML111", sinHKG, ac2);
            ml111 = flightSessionBeanLocal.createNewFlight(ml111, sinHKG);
            Flight ml112 = new Flight("ML112", hkgSIN, ac2);
            ml112 = flightSessionBeanLocal.createReturnFlight(ml111, ml112);

            Flight ml211 = new Flight("ML211", sinTPE, ac2);
            ml211 = flightSessionBeanLocal.createNewFlight(ml211, sinTPE);
            Flight ml212 = new Flight("ML212", tpeSIN, ac2);
            ml212 = flightSessionBeanLocal.createReturnFlight(ml211, ml212);

            Flight ml311 = new Flight("ML311", sinNRT, ac4);
            ml311 = flightSessionBeanLocal.createNewFlight(ml311, sinNRT);
            Flight ml312 = new Flight("ML312", nrtSIN, ac4);
            ml312 = flightSessionBeanLocal.createReturnFlight(ml311, ml312);

            Flight ml411 = new Flight("ML411", hkgNRT, ac2);
            ml411 = flightSessionBeanLocal.createNewFlight(ml411, hkgNRT);
            Flight ml412 = new Flight("ML412", nrtHKG, ac2);
            ml412 = flightSessionBeanLocal.createReturnFlight(ml411, ml412);

            Flight ml511 = new Flight("ML511", tpeNRT, ac2);
            ml511 = flightSessionBeanLocal.createNewFlight(ml511, tpeNRT);
            Flight ml512 = new Flight("ML512", nrtTPE, ac2);
            ml512 = flightSessionBeanLocal.createReturnFlight(ml511, ml512);

            Flight ml611 = new Flight("ML611", sinSYD, ac2);
            ml611 = flightSessionBeanLocal.createNewFlight(ml611, sinSYD);
            Flight ml612 = new Flight("ML612", sydSIN, ac2);
            ml612 = flightSessionBeanLocal.createReturnFlight(ml611, ml612);

            Flight ml621 = new Flight("ML621", sinSYD, ac1);
            ml621 = flightSessionBeanLocal.createNewFlight(ml621, sinSYD);
            Flight ml622 = new Flight("ML622", sydSIN, ac1);
            ml622 = flightSessionBeanLocal.createReturnFlight(ml621, ml622);

            Flight ml711 = new Flight("ML711", sydNRT, ac4);
            ml711 = flightSessionBeanLocal.createNewFlight(ml711, sydNRT);
            Flight ml712 = new Flight("ML712", nrtSYD, ac4);
            ml712 = flightSessionBeanLocal.createReturnFlight(ml711, ml712);
            
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yy, h:mm a");

            FlightSchedulePlan fsp1 = new FlightSchedulePlan(FlightSchedulePlanType.RECURRENTWEEKLY, DayOfWeek.MONDAY,
                    LocalDateTime.parse("1 Dec 23, 9:00 AM", formatter), LocalDateTime.parse("31 Dec 23, 9:00 AM", formatter), ml711);
            List<Fare> fares1 = new ArrayList<Fare>();
            fares1.add(new Fare(new BigDecimal(6000), cc2));
            fares1.add(new Fare(new BigDecimal(3000), cc6));
            fares1.add(new Fare(new BigDecimal(1000), cc7));
            fsp1.setFares(fares1);
            fsp1.setFlightSchedules(createRecurrentWeeklyFlightSchedule(fsp1, parseDurationString("14 Hours 0 Minutes")));
            fsp1 = flightSchedulePlanSessionBeanLocal.createFlightSchedulePlan(fsp1);
            FlightSchedulePlan fsp1Return = flightSchedulePlanSessionBeanLocal.createReturnFlightSchedulePlan(fsp1, 2);
            // return is ml712 ac4 -> cc2 $6000, cc6 $3000, cc7 $1000

            FlightSchedulePlan fsp2 = new FlightSchedulePlan(FlightSchedulePlanType.RECURRENTWEEKLY, DayOfWeek.SUNDAY,
                    LocalDateTime.parse("1 Dec 23, 12:00 PM", formatter), LocalDateTime.parse("31 Dec 23, 12:00 PM", formatter), ml611);
            List<Fare> fares2 = new ArrayList<Fare>();
            fares2.add(new Fare(new BigDecimal(3000), cc2));
            fares2.add(new Fare(new BigDecimal(1500), cc3));
            fares2.add(new Fare(new BigDecimal(500), cc4));
            fsp2.setFares(fares2);
            fsp2.setFlightSchedules(createRecurrentWeeklyFlightSchedule(fsp2, parseDurationString("8 Hours 0 Minutes")));
            fsp2 = flightSchedulePlanSessionBeanLocal.createFlightSchedulePlan(fsp2);
            FlightSchedulePlan fsp2Return = flightSchedulePlanSessionBeanLocal.createReturnFlightSchedulePlan(fsp2, 2);
            // return is ml612 ac2 -> cc2  $3000, cc3 $1500, cc4 $500

            FlightSchedulePlan fsp3 = new FlightSchedulePlan(FlightSchedulePlanType.RECURRENTWEEKLY, DayOfWeek.TUESDAY,
                    LocalDateTime.parse("1 Dec 23, 10:00 AM", formatter), LocalDateTime.parse("31 Dec 23, 10:00 AM", formatter), ml621);
            List<Fare> fares3 = new ArrayList<Fare>();
            fares3.add(new Fare(new BigDecimal(700), cc1));
            fsp3.setFares(fares3);
            fsp3.setFlightSchedules(createRecurrentWeeklyFlightSchedule(fsp3, parseDurationString("8 Hours 0 Minutes")));
            fsp3 = flightSchedulePlanSessionBeanLocal.createFlightSchedulePlan(fsp3);
            FlightSchedulePlan fsp3Return = flightSchedulePlanSessionBeanLocal.createReturnFlightSchedulePlan(fsp3, 2);
            // return is ml622 ac1 -> cc1 $700

            FlightSchedulePlan fsp4 = new FlightSchedulePlan(FlightSchedulePlanType.RECURRENTWEEKLY, DayOfWeek.MONDAY,
                    LocalDateTime.parse("1 Dec 23, 10:00 AM", formatter), LocalDateTime.parse("31 Dec 23, 10:00 AM", formatter), ml311);
            List<Fare> fares4 = new ArrayList<Fare>();
            fares4.add(new Fare(new BigDecimal(3100), cc2));
            fares4.add(new Fare(new BigDecimal(1600), cc6));
            fares4.add(new Fare(new BigDecimal(600), cc7));
            fsp4.setFares(fares4);
            fsp4.setFlightSchedules(createRecurrentWeeklyFlightSchedule(fsp4, parseDurationString("6 Hours 30 Minutes")));
            fsp4 = flightSchedulePlanSessionBeanLocal.createFlightSchedulePlan(fsp4);
            FlightSchedulePlan fsp4Return = flightSchedulePlanSessionBeanLocal.createReturnFlightSchedulePlan(fsp4, 3);
            // return is ml312 ac4 -> cc2 $3100, cc6 $1600, cc7 $600

            FlightSchedulePlan fsp5 = new FlightSchedulePlan(FlightSchedulePlanType.RECURRENTNDAY, 2,
                    LocalDateTime.parse("1 Dec 23, 1:00 PM", formatter), LocalDateTime.parse("31 Dec 23, 1:00 PM", formatter), ml411);
            List<Fare> fares5 = new ArrayList<Fare>();
            fares5.add(new Fare(new BigDecimal(2900), cc2));
            fares5.add(new Fare(new BigDecimal(1400), cc3));
            fares5.add(new Fare(new BigDecimal(400), cc4));
            fsp5.setFares(fares5);
            fsp5.setFlightSchedules(createRecurrentNFlightSchedule(fsp5, parseDurationString("4 Hours 0 Minutes")));
            fsp5 = flightSchedulePlanSessionBeanLocal.createFlightSchedulePlan(fsp5);
            FlightSchedulePlan fsp5Return = flightSchedulePlanSessionBeanLocal.createReturnFlightSchedulePlan(fsp5, 4);
            // return is ml412 ac2 -> cc2 $2900, cc3 $1400, cc4 $400

            FlightSchedulePlan fsp6 = new FlightSchedulePlan();
            fsp6.setFlight(ml511);
            fsp6.setFlightSchedulePlanType(FlightSchedulePlanType.MANUALMULTIPLE);
            List<Fare> fares6 = new ArrayList<Fare>();
            fares6.add(new Fare(new BigDecimal(3100), cc2));
            fares6.add(new Fare(new BigDecimal(1600), cc3));
            fares6.add(new Fare(new BigDecimal(600), cc4));
            fsp6.setFares(fares6);
            fsp6.getFlightSchedules().add(createFlightSchedule(fsp6.getFlight(), LocalDateTime.parse("7 Dec 23, 5:00 PM", formatter), parseDurationString("3 Hours 0 Minutes")));
            fsp6.getFlightSchedules().add(createFlightSchedule(fsp6.getFlight(), LocalDateTime.parse("8 Dec 23, 5:00 PM", formatter), parseDurationString("3 Hours 0 Minutes")));
            fsp6.getFlightSchedules().add(createFlightSchedule(fsp6.getFlight(), LocalDateTime.parse("9 Dec 23, 5:00 PM", formatter), parseDurationString("3 Hours 0 Minutes")));
            fsp6 = flightSchedulePlanSessionBeanLocal.createFlightSchedulePlan(fsp6);
            FlightSchedulePlan fsp6Return = flightSchedulePlanSessionBeanLocal.createReturnFlightSchedulePlan(fsp6, 2);
            // return is ml512 ac2 -> cc2 $3100, cc3 $1600, cc4 $600

        
            } catch(NonUniqueFlightNumException ex) {
                System.out.println(ex.getMessage());
            }

    
    }

    private FlightSchedule createFlightSchedule(Flight flight, LocalDateTime departureDateTime, Duration flightHours) {
        FlightSchedule flightSchedule = new FlightSchedule(departureDateTime, flightHours);
        flightSchedule.setSeatInventory(createSeatInventory(flight, flightSchedule));
        return flightSchedule;
    }

    private List<FlightSchedule> createRecurrentWeeklyFlightSchedule(FlightSchedulePlan fsp, Duration flightHours) {
        List<FlightSchedule> schedules = new ArrayList<>();

        // Calculate the difference in days between the start date and the next occurrence of the specified day
        int daysUntilFlightDay = (fsp.getDayOfWeek().getValue() - fsp.getStartDate().getDayOfWeek().getValue() + 7) % 7;

        // Create recurrent FlightSchedules
        LocalDateTime currentDateTime = fsp.getStartDate().plusDays(daysUntilFlightDay);
        while (currentDateTime.isBefore(fsp.getEndDate())) {
            FlightSchedule fs = new FlightSchedule(currentDateTime, flightHours);
            fs.setSeatInventory(createSeatInventory(fsp.getFlight(), fs));
            schedules.add(fs);
            currentDateTime = currentDateTime.plusWeeks(1);
        }
        return schedules;
    }

    List<FlightSchedule> createRecurrentNFlightSchedule(FlightSchedulePlan fsp, Duration flightHours) {
        List<FlightSchedule> schedules = new ArrayList<FlightSchedule>();

        // Create recurrent FlightSchedules
        LocalDateTime currentDateTime = fsp.getStartDate();
        while (!currentDateTime.isAfter(fsp.getEndDate())) {
            FlightSchedule fs = new FlightSchedule(currentDateTime, flightHours);
            fs.setSeatInventory(createSeatInventory(fsp.getFlight(), fs));
            schedules.add(fs);
            currentDateTime = currentDateTime.plusDays(fsp.getRecurrentNDay());
        }
        return schedules;
    }

    private SeatInventory createSeatInventory(Flight f, FlightSchedule fs) {
        SeatInventory seatInventory = new SeatInventory();
        fs.setSeatInventory(seatInventory);
        seatInventory.getAllCabinClasses().addAll(f.getAircraftConfig().getCabinClasses());
        int numCabinClasses = f.getAircraftConfig().getNumCabinClasses();

        seatInventory.setAvailableSeats(new ArrayList<List<String>>(numCabinClasses));
        seatInventory.setBalanceSeats(new ArrayList<List<String>>(numCabinClasses));
        seatInventory.setReservedSeats(new ArrayList<List<String>>(numCabinClasses));

        for (int i = 0; i < numCabinClasses; i++) {
            CabinClass c = seatInventory.getAllCabinClasses().get(i);
            int numRows = c.getNumRows();
            int numSeatsAbreast = c.getNumSeatsAbreast();
            seatInventory.getAvailableSeats().add(new ArrayList<String>(numRows * numSeatsAbreast));
            seatInventory.getReservedSeats().add(new ArrayList<String>(numRows * numSeatsAbreast));
            seatInventory.getBalanceSeats().add(new ArrayList<String>(numRows * numSeatsAbreast));
            for (int j = 1; j <= numRows; j++) {
                for (int k = 0; k < numSeatsAbreast; k++) {
                    char alphabet = (char) ('A' + k);
                    String seat = j + String.valueOf(alphabet);
                    seatInventory.getAvailableSeats().get(i).add(seat);
                }
            }
        }
        return seatInventory;
    }

    private static Duration parseDurationString(String durationString) {
        Pattern pattern = Pattern.compile("(\\d+)\\s*Hours?\\s*(\\d+)?\\s*Minutes?");
        Matcher matcher = pattern.matcher(durationString);

        if (matcher.matches()) {
            int hours = Integer.parseInt(matcher.group(1));
            int minutes = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;

            return Duration.ofHours(hours).plusMinutes(minutes);
        } else {
            throw new IllegalArgumentException("Invalid duration string format: " + durationString);
        }

    
}
}