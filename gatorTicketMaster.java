import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class gatorTicketMaster {


    private PrintWriter outputWriter;


    public gatorTicketMaster(PrintWriter outputWriter) {
        this.outputWriter = outputWriter;
    }


    private void printOutput(String message) {
        System.out.println(message);
        outputWriter.println(message);
    }

    // Process commands from the input file
    public void processCommands(String fileName, AvailabilityHeap availabilityHeap, RedBlackTree redBlackTree, WaitlistedHeap waitlistedHeap) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("[(), ]+");
                String command = parts[0];

                switch (command) {
                    case "Initialize":

                        int numSeats;
                        try {
                            numSeats = Integer.parseInt(parts[1]);
                            if (numSeats < 0) {
                                outputWriter.println("Invalid input. Please provide a valid number of seats.");
                                outputWriter.flush();
                                System.exit(0);
                            }


                            availabilityHeap.initialize(numSeats);
                        } catch (NumberFormatException e) {
                            outputWriter.println("Invalid input. Please provide a valid number of seats.");
                            return; // Exit the method or handle it as needed
                        }


                        break;

                    case "Reserve":
                        int userId = Integer.parseInt(parts[1]);
                        int seatType = Integer.parseInt(parts[2]);
                        redBlackTree.reserve(userId, seatType, availabilityHeap, waitlistedHeap);
                        break;

                    case "Available":
                        redBlackTree.Available(waitlistedHeap, availabilityHeap);
                        break;

                    case "ExitWaitlist":
                        int exitUserId = Integer.parseInt(parts[1]);
                        waitlistedHeap.exitWaitlist(exitUserId);
                        break;

                    case "UpdatePriority":
                        int priorityUserId = Integer.parseInt(parts[1]);
                        int newPriority = Integer.parseInt(parts[2]);
                        waitlistedHeap.updatePriority(priorityUserId, newPriority);
                        break;

                    case "AddSeats":
                        int seatsToAdd;
                        try {
                            seatsToAdd = Integer.parseInt(parts[1]);
                            availabilityHeap.addSeats(seatsToAdd, waitlistedHeap, redBlackTree);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please provide a valid number of seats to add.");
                        }
                        break;

                    case "Cancel":
                        int cancelUserId = Integer.parseInt(parts[1]);
                        int cancelSeatType = Integer.parseInt(parts[2]);
                        redBlackTree.Cancel(cancelUserId, cancelSeatType, availabilityHeap, waitlistedHeap);
                        break;

                    case "ReleaseSeats":

                        try {
                            int releaseUserId1 = Integer.parseInt(parts[1]);
                            int releaseUserId2 = Integer.parseInt(parts[2]);
                            redBlackTree.ReleaseSeats(releaseUserId1, releaseUserId2, redBlackTree, availabilityHeap, waitlistedHeap);
                        } catch (NumberFormatException e) {
                            outputWriter.println("Invalid input. Please provide a valid number of seats.");
                            return;
                        }
                        break;

                    case "PrintReservations":
                        redBlackTree.PrintReservations();
                        break;
                    // Stop further processing after Quit
                    case "Quit":
                        redBlackTree.Quit();
                        return;

                    default:
                        printOutput("Unknown command: " + command);
                        break;
                }
            }


        } catch (IOException e) {
            printOutput("Error reading file: " + e.getMessage());
        } finally {
            outputWriter.flush();
            outputWriter.close();
        }
    }

    public static void main(String[] args) {

        //generating the output file name as per the project draft
        String fileName = args[0].length() == 0 ? "null.txt" : args[0];
        String output_fileName = fileName.substring(0, fileName.length() - 4) + "_" + "output_file.txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(output_fileName))) {

            //Creating objects for implementing the reservation system
            AvailabilityHeap availabilityHeap = new AvailabilityHeap(writer);
            RedBlackTree redBlackTree = new RedBlackTree(writer);
            WaitlistedHeap waitlistedHeap = new WaitlistedHeap(writer);
            gatorTicketMaster gtm = new gatorTicketMaster(writer);

            if (args.length != 1) {
                writer.println("Usage: java GatorTicketMaster <file_name>");
                return;
            }
            gtm.processCommands(fileName, availabilityHeap, redBlackTree, waitlistedHeap); // Call processCommands with the required arguments
        } catch (IOException e) {
            System.out.println("Error initializing output file: " + e.getMessage());
        }
    }
}
