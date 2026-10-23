import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

// --- DOMAIN MODELS ---

class Flight {
    private String flightNumber;
    private String source;
    private String destination;
    private int totalSeats;
    private int availableSeats;
    private double baseFare;

    public Flight(String flightNumber, String source, String destination, int totalSeats, double baseFare) {
        this.flightNumber = flightNumber;
        this.source = source;
        this.destination = destination;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.baseFare = baseFare;
    }

    public String getFlightNumber() { return flightNumber; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public int getAvailableSeats() { return availableSeats; }
    public double getBaseFare() { return baseFare; }

    public boolean bookSeat(int count) {
        if (availableSeats >= count) {
            availableSeats -= count;
            return true;
        }
        return false;
    }

    public void releaseSeat(int count) {
        if (availableSeats + count <= totalSeats) {
            availableSeats += count;
        }
    }

    @Override
    public String toString() {
        return String.format("Flight: %s | %s -> %s | Seats Available: %d | Base Fare: $%.2f",
                flightNumber, source, destination, availableSeats, baseFare);
    }
}

class Ticket {
    private String pnr;
    private Flight flight;
    private String passengerName;
    private int seatsBooked;
    private double totalFare;

    public Ticket(Flight flight, String passengerName, int seatsBooked) {
        this.pnr = "PNR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.flight = flight;
        this.passengerName = passengerName;
        this.seatsBooked = seatsBooked;
        this.totalFare = flight.getBaseFare() * seatsBooked;
    }

    public String getPnr() { return pnr; }
    public Flight getFlight() { return flight; }
    public int getSeatsBooked() { return seatsBooked; }

    public void displayTicket() {
        System.out.println("\n========================================");
        System.out.println("          BOARDING PASS / TICKET        ");
        System.out.println("========================================");
        System.out.println("PNR Code       : " + pnr);
        System.out.println("Passenger Name : " + passengerName);
        System.out.println("Flight Number  : " + flight.getFlightNumber());
        System.out.println("Route          : " + flight.getSource() + " -> " + flight.getDestination());
        System.out.println("Seats Booked   : " + seatsBooked);
        System.out.println("Total Amount   : $" + String.format("%.2f", totalFare));
        System.out.println("========================================");
    }
}

// --- SYSTEM SERVICE LAYER ---

class FlightReservationSystem {
    private List<Flight> flights = new ArrayList<>();
    private List<Ticket> bookedTickets = new ArrayList<>();

    public FlightReservationSystem() {
        // Mock data initialization
        flights.add(new Flight("AI-101", "New York", "London", 50, 450.00));
        flights.add(new Flight("BA-202", "London", "Tokyo", 30, 850.00));
        flights.add(new Flight("EK-303", "Dubai", "New York", 20, 600.00));
    }

    public void showAvailableFlights() {
        System.out.println("\n--- Available Flights ---");
        for (Flight f : flights) {
            System.out.println(f);
        }
    }

    public Flight findFlight(String flightNumber) {
        for (Flight f : flights) {
            if (f.getFlightNumber().equalsIgnoreCase(flightNumber)) {
                return f;
            }
        }
        return null;
    }

    public void bookTicket(String flightNumber, String passengerName, int seats) {
        Flight flight = findFlight(flightNumber);
        if (flight == null) {
            System.out.println("Error: Flight not found!");
            return;
        }

        if (flight.bookSeat(seats)) {
            Ticket ticket = new Ticket(flight, passengerName, seats);
            bookedTickets.add(ticket);
            System.out.println("Booking Successful!");
            ticket.displayTicket();
        } else {
            System.out.println("Error: Not enough seats available! Only " + flight.getAvailableSeats() + " left.");
        }
    }

    public void cancelTicket(String pnr) {
        Ticket match = null;
        for (Ticket t : bookedTickets) {
            if (t.getPnr().equalsIgnoreCase(pnr)) {
                match = t;
                break;
            }
        }

        if (match != null) {
            match.getFlight().releaseSeat(match.getSeatsBooked());
            bookedTickets.remove(match);
            System.out.println("Success: Ticket with PNR " + pnr + " has been cancelled. Refund initialized.");
        } else {
            System.out.println("Error: PNR code invalid or ticket not found.");
        }
    }
}

// --- CORE ENTRY POINT ---

public class AirlineReservationSystem {
    public static void main(String[] args) {
        FlightReservationSystem system = new FlightReservationSystem();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== AIRLINE RESERVATION SYSTEM =====");
            System.out.println("1. View Available Flights");
            System.out.println("2. Book a Ticket");
            System.out.println("3. Cancel a Ticket");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    system.showAvailableFlights();
                    break;
                case 2:
                    System.out.print("Enter Flight Number (e.g., AI-101): ");
                    String fNum = scanner.nextLine().trim();
                    System.out.print("Enter Passenger Name: ");
                    String name = scanner.nextLine().trim();
                    System.out.print("Enter Number of Seats: ");
                    int seats = Integer.parseInt(scanner.nextLine());
                    
                    system.bookTicket(fNum, name, seats);
                    break;
                case 3:
                    System.out.print("Enter PNR Code to Cancel: ");
                    String pnr = scanner.nextLine().trim();
                    system.cancelTicket(pnr);
                    break;
                case 4:
                    System.out.println("Thank you for using our Reservation System. Goodbye!");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid option! Select between 1 and 4.");
            }
        }
    }
}