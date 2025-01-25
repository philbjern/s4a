package pl.philbjern;

import java.io.*;
import java.util.*;

public class AirlineManagement {

    static class Flight {
        TreeMap<Integer, Integer> passengerHistory = new TreeMap<>();
        TreeMap<Integer, Boolean> activeState = new TreeMap<>();

        void updatePassengers(int day, int passengers) {
            passengerHistory.put(day, passengers);
            activeState.putIfAbsent(day, true); // Assume active unless explicitly deactivated
        }

        void deactivateFromDay(int day) {
            activeState.put(day, false);
        }

        void activateFromDay(int day, int passengers) {
            passengerHistory.put(day, passengers);
            activeState.put(day, true);
        }

        int getCumulativePassengers(int day) {
            int sum = 0;
            int lastPassengers = 0;
            boolean isActive = true;

            for (int d = 0; d <= day; d++) {
                // Update passengers if there's a record for the day
                if (passengerHistory.containsKey(d)) {
                    lastPassengers = passengerHistory.get(d);
                }

                // Update active state if there's a state change for the day
                if (activeState.containsKey(d)) {
                    isActive = activeState.get(d);
                }

                // Sum passengers only if active
                if (isActive) {
                    sum += lastPassengers;
                }
            }

            return sum;
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
            String[] query = br.readLine().split(" ");
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
                    flights[i].activateFromDay(t, p);
                    break;
                }
                case 'Q': {
                    int j = Integer.parseInt(query[2]) - 1;
                    int t = Integer.parseInt(query[3]);
                    int sum = 0;
                    for (int idx = i; idx <= j; idx++) {
                        sum += flights[idx].getCumulativePassengers(t);
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
        }
        return flights;
    }
}
