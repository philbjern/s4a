package pl.philbjern;
import java.io.*;
import java.util.*;

public class AirlineManagement {

    static class Flight {
        TreeMap<Integer, Integer> passengerHistory = new TreeMap<>();
        TreeMap<Integer, Boolean> activeState = new TreeMap<>();

        void updatePassengers(int day, int passengers) {
//            System.out.printf("Updating passengers: day=%d, passengers=%d%n", day, passengers);

            // Update the value for the given day
            passengerHistory.put(day, passengers);

            // Fill in subsequent days if not already defined
            Map.Entry<Integer, Integer> nextEntry = passengerHistory.higherEntry(day);
            if (nextEntry == null) {
                passengerHistory.put(day + 1, passengers);
            }

            activeState.putIfAbsent(day, true); // Assume active unless explicitly deactivated
//            System.out.printf("Passenger history after update: %s%n", passengerHistory);
        }

        void deactivateFromDay(int day) {
//            System.out.printf("Deactivating flight from day=%d%n", day);
            activeState.put(day, false);
//            System.out.printf("Active state after deactivation: %s%n", activeState);
        }

        void activateFromDay(int day, int passengers) {
//            System.out.printf("Activating flight: day=%d, passengers=%d%n", day, passengers);
            passengerHistory.put(day, passengers);
            activeState.put(day, true);
//            System.out.printf("Passenger history after activation: %s%n", passengerHistory);
//            System.out.printf("Active state after activation: %s%n", activeState);
        }

        int getPassengers(int day) {
            int totalPassengers = 0;
//            System.out.printf("Calculating cumulative passengers for day=%d%n, for flight=%s", day, this.toString());

            for (Map.Entry<Integer, Integer> entry : passengerHistory.entrySet()) {
                int recordedDay = entry.getKey();
                if (recordedDay > day) break;

                Map.Entry<Integer, Boolean> activeEntry = activeState.floorEntry(recordedDay);
                if (activeEntry != null && activeEntry.getValue()) {
//                    System.out.printf("Adding passengers from day=%d: %d%n", recordedDay, entry.getValue());
                    totalPassengers += entry.getValue();
                }
            }

//            System.out.printf("Total passengers up to day=%d: %d%n", day, totalPassengers);
            return totalPassengers;
        }

        @Override
        public String toString() {
            return "Flight{" +
                    "passengerHistory=" + passengerHistory +
                    ", activeState=" + activeState +
                    '}';
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);

        String[] firstLine = br.readLine().split(" ");
        int n = Integer.parseInt(firstLine[0]);
        int q = Integer.parseInt(firstLine[1]);

        String[] passengersLine = br.readLine().split(" ");
        Flight[] flights = initializeFlights(n, passengersLine);

        for (int queryIdx = 0; queryIdx < q; queryIdx++) {
            String line = br.readLine();
            if (line == null || line.isBlank()) {
//                System.out.println("Input ended unexpectedly.");
                break;
            }
            String[] query = line.split(" ");
//            System.out.println("Processing query: " + Arrays.toString(query));
            char type = query[0].charAt(0);
            int i = Integer.parseInt(query[1]) - 1;

            switch (type) {
                case 'P': {
                    int p = Integer.parseInt(query[2]);
                    int t = Integer.parseInt(query[3]);
                    flights[i].updatePassengers(t, p);
                    break;
                }
                case 'C': {
                    int t = Integer.parseInt(query[2]);
                    flights[i].deactivateFromDay(t);
                    break;
                }
                case 'A': {
                    int p = Integer.parseInt(query[2]);
                    int t = Integer.parseInt(query[3]);
                    flights[i] = new Flight();
                    flights[i].activateFromDay(t, p);
                    break;
                }
                case 'Q': {
                    int j = Integer.parseInt(query[2]) - 1;
                    int t = Integer.parseInt(query[3]);
                    int sum = 0;
//                    System.out.printf("*** Query: from=%d to=%d up to day=%d%n", i, j, t);
                    for (int idx = i; idx <= j; idx++) {
                        sum += flights[idx].getPassengers(t);
                    }
                    pw.println(sum);
                    break;
                }
            }
        }

        pw.flush();
        pw.close();
    }

    private static Flight[] initializeFlights(int n, String[] passengersLine) {
        Flight[] flights = new Flight[n];
        for (int i = 0; i < n; i++) {
            flights[i] = new Flight();
            flights[i].updatePassengers(0, Integer.parseInt(passengersLine[i]));
//            System.out.printf("Initialized flight %d: %s%n", i + 1, flights[i]);
        }
        return flights;
    }
}
