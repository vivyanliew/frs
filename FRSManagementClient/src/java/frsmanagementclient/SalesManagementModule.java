/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package frsmanagementclient;

import ejb.session.stateless.FlightSessionBeanRemote;
import entity.Flight;
import entity.FlightSchedule;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import util.exception.FlightNotFoundException;
import util.exception.NoFlightSchedulePlansException;

/**
 *
 * @author liewvivyan
 */
public class SalesManagementModule {

    private FlightSessionBeanRemote flightSessionBeanRemote;

    public SalesManagementModule() {
    }

    public SalesManagementModule(FlightSessionBeanRemote flightSessionBeanRemote) {
        this.flightSessionBeanRemote = flightSessionBeanRemote;
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

    }

    void viewFlightReservations() {
    }
}
