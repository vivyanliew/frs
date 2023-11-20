/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package frsmanagementclient;

import ejb.session.stateless.FlightReservationSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import entity.CabinClass;
import entity.Flight;
import entity.FlightReservation;
import entity.FlightSchedule;
import entity.FlightSchedule.FlightScheduleComparator;
import entity.Passenger;
import entity.SeatInventory;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import javafx.util.Pair;
import util.exception.FlightNotFoundException;
import util.exception.NoFlightReservationsException;
import util.exception.NoFlightSchedulePlansException;

/**
 *
 * @author liewvivyan
 */
public class SalesManagementModule {

    private FlightSessionBeanRemote flightSessionBeanRemote;
     private FlightReservationSessionBeanRemote flightReservationSessionBeanRemote;

    public SalesManagementModule() {
    }

    public SalesManagementModule(FlightSessionBeanRemote flightSessionBeanRemote,FlightReservationSessionBeanRemote flightReservationSessionBeanRemote ) {
        this.flightSessionBeanRemote = flightSessionBeanRemote;
         this.flightReservationSessionBeanRemote = flightReservationSessionBeanRemote;
    }

    void mainMenu() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        while (true) {

            System.out.println("*** Sales Management ***\n");
            System.out.println("1: View Seats Inventory");
            System.out.println("2: View Flight Reservations");
            System.out.println("3: Exit");
            response = 0;
            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = sc.nextInt();

                if (response == 1) {
                    try {
                        viewSeatsInventory();
                    } catch (NoFlightSchedulePlansException ex) {
                        System.out.println("No Flight Schedule Plans " + ex.getMessage());
                    } catch (FlightNotFoundException ex) {
                        System.out.println("No Flight Found " + ex.getMessage());
                    }
                    
                } else if (response == 2) {
                    viewFlightReservations();

                } else if (response ==3) {
                    break;
                }
            }
            if (response ==3) {
                break;
            }
        }
    }

    void viewSeatsInventory() throws NoFlightSchedulePlansException, FlightNotFoundException {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** View Seats Inventory ***\n");
        System.out.println("Enter flight number> ");
        String flightNum = sc.nextLine().trim();
        Flight flight;
        while (true) {
            try {
                flight = flightSessionBeanRemote.retrieveFlightByFlightNumber(flightNum);
                break;
            } catch (FlightNotFoundException ex) {
                System.out.println(ex.getMessage());
                System.out.println("Please re-enter Flight No.>");
                flightNum = sc.nextLine().trim();
            }
        }
       
        List<FlightSchedule> flightSchedules = new ArrayList<>();
        try {
            flightSchedules = flightSessionBeanRemote.retrieveFlightSchedules(flightNum);
        } catch (NoFlightSchedulePlansException ex) {
             System.out.println(ex.getMessage());
        }
        
        //System.out.println(flightSchedules);
        for (FlightSchedule fs : flightSchedules) {
            System.out.println(fs.getSeatInventory().getAvailableSeats());
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM u h:mm a");
        flightSchedules.sort(new FlightScheduleComparator());

        int count = 1;
        System.out.println("Flight Schedules for Flight " + flightNum + ":");
        for (FlightSchedule fs : flightSchedules) {
            System.out.println("    " + count + ". Departure: " + fs.getDepartureDateTime().format(formatter) + " - " + fs.getArrivalDateTime().format(formatter));
            count++;
        }
        System.out.println("Select a Flight Schedule>");
        int index = sc.nextInt() - 1;
        sc.nextLine().trim();
        
        FlightSchedule fs = flightSchedules.get(index);
        System.out.println("Flight Schedule: " + fs.getDepartureDateTime().format(formatter) + " - " + fs.getArrivalDateTime().format(formatter));
        SeatInventory si = fs.getSeatInventory();
        for (int i = 0; i < si.getAllCabinClasses().size(); i++) {
            System.out.println("    Cabin Class " + si.getAllCabinClasses().get(i).getCabinClassName());
            if (si.getAvailableSeats().get(i).isEmpty()) {
                System.out.println("    Available Seats: N.A.");
            } else {
                System.out.println("    Available Seats: " + si.getAvailableSeats().get(i).toString());
            }
            if (si.getReservedSeats().get(i).isEmpty()) {
                System.out.println("    Reserved Seats: N.A.");
            } else {
                System.out.println("    Reserved Seats: " + si.getReservedSeats().get(i).toString());
            } 
            if (si.getBalanceSeats().get(i).isEmpty()) {
                System.out.println("    Balance Seats: N.A.");
            } else {
                System.out.println("    Balance Seats: " + si.getBalanceSeats().get(i).toString());
            }
        }

    }

       void viewFlightReservations() {
        Scanner sc = new Scanner(System.in);
        System.out.println("** View Flight Reservations **\n");
        System.out.print("Enter flight number > ");
        String flightNum = sc.nextLine().trim();
        Flight flight;
        while (true) {
            try {
                flight = flightSessionBeanRemote.retrieveFlightByFlightNumber(flightNum);
                break;
            } catch (FlightNotFoundException ex) {
                System.out.println(ex.getMessage());
                System.out.print("Please re-enter Flight No.> ");
                flightNum = sc.nextLine().trim();
            }
        }

        List<FlightSchedule> flightSchedules = new ArrayList<>();

        try {
            flightSchedules = flightSessionBeanRemote.retrieveFlightSchedules(flightNum);
        } catch (NoFlightSchedulePlansException ex) {
            System.out.println(ex.getMessage());
        } catch (FlightNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM u h:mm a");
        flightSchedules.sort(new FlightScheduleComparator());

        int count = 1;
        System.out.println("Flight Schedules for Flight " + flightNum + ":");
        for (FlightSchedule fs : flightSchedules) {
            System.out.println("    " + count + ". Departure: " + fs.getDepartureDateTime().format(formatter) + " - " + fs.getArrivalDateTime().format(formatter));
            count++;
        }

        System.out.print("Select a Flight Schedule > ");
        int index = sc.nextInt() - 1;
        sc.nextLine().trim();

        FlightSchedule fs = flightSchedules.get(index);
        System.out.println("Flight Schedule: " + fs.getDepartureDateTime().format(formatter) + " - " + fs.getArrivalDateTime().format(formatter));

        List<CabinClass> cabinClasses = fs.getSeatInventory().getAllCabinClasses();

        try {
            List<FlightReservation> reservationDetails = flightReservationSessionBeanRemote.retrieveReservationsForFlightSchedule(fs);

            for (CabinClass cc : cabinClasses) {
                System.out.println("    Reserved Seats for Cabin Class " + cc.getCabinClassName());
                List<Pair<Passenger, String>> printList = new ArrayList<>();
                int reservationCount = 0;
                for (FlightReservation fr : reservationDetails) {
                    int i = fr.getFlightSchedules().indexOf(fs);
                    if (fr.getCabinClassIds().get(i).equals(cc.getCabinClassId())) {
                        Passenger p = fr.getPassengers().get(i);
                        String fareBasisCode = fr.getFareBasisCode().get(i);
                        printList.add(new Pair<Passenger, String>(p, fareBasisCode));
                        reservationCount++;
                    }
                }
                if (reservationCount == 0) {
                    System.out.println("       No reservations for this cabin class.");
                    continue;
                }
                Collections.sort(printList, new Comparator<Pair<Passenger, String>>() {
                    @Override
                    public int compare(Pair<Passenger, String> pair1, Pair<Passenger, String> pair2) {
                        return pair1.getKey().getSeats().get(0).compareTo(pair2.getKey().getSeats().get(0));
                    }
                });
                int itemCount = 1;
                for (Pair<Passenger, String> printItem : printList) {
                    System.out.println("    " + itemCount + ". Seat Number: " + printItem.getKey().getSeats().get(0));
                    System.out.println("        Passenger: " + printItem.getKey().getFirstName() + " " + printItem.getKey().getLastName());
                    System.out.println("        Fare Basis Code: " + printItem.getValue());
                    itemCount++;
                }
            }
        } catch (NoFlightReservationsException ex) {
            System.out.println("There are currently no flight reservations for the selected flight schedule.");
        }

    }
}
