package pl.philbjern;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AirlineManagementTest {

    @Test
    public void testAirlineManagementSampleData1() throws Exception {
        // Test input as per the example
        String input = """
                5 7
                1 2 3 2 4
                Q 1 5 2
                Q 2 3 2
                C 2 3
                P 3 5 3
                Q 2 4 4
                A 2 5 6
                Q 1 5 8
                """;
        String expectedOutput = """
                24
                10
                22
                100
                """;

        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setIn(inputStream);
        System.setOut(new PrintStream(outputStream));

        AirlineManagement.main(new String[0]);

        // Capture actual output
        String actualOutput = outputStream.toString().trim();

        // Include detailed error message on failure
        assertEquals(expectedOutput.trim(), actualOutput,
                String.format("Output mismatch:%nExpected:%n%s%nActual:%n%s%n", expectedOutput.trim(), actualOutput));
    }

    @Test
    public void testAirlineManagementSampleData2() throws Exception {
        // Test input as per the example
        String input = """
                1 7
                2
                Q 1 1 1
                C 1 1
                A 1 6 2
                Q 1 1 3
                Q 1 1 4
                Q 1 1 7
                Q 1 1 8
                """;
        String expectedOutput = """
                2
                6
                12
                30
                36
                """;

        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setIn(inputStream);
        System.setOut(new PrintStream(outputStream));

        AirlineManagement.main(new String[0]);

        // Capture actual output
        String actualOutput = outputStream.toString().trim();

        // Include detailed error message on failure
        assertEquals(expectedOutput.trim(), actualOutput,
                String.format("Output mismatch:%nExpected:%n%s%nActual:%n%s%n", expectedOutput.trim(), actualOutput));

    }

}