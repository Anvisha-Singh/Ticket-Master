import java.io.PrintWriter;

public class AvailabilityHeap {

    int[] heap = new int[10000];
    // Capacity of the heap (maximum number of seats available)
    int capacity = 0;
    // Current count of available seats
    int count = 0;

    private PrintWriter outputWriter;

    public AvailabilityHeap(PrintWriter outputWriter) {
        this.outputWriter = outputWriter;
    }

    // Initialize the heap with a given number of seats
    public void initialize(int capacity) {
        this.capacity = capacity;
        this.count = capacity;
        // Fill the heap with seat numbers from 1 to capacity
        for (int iter_i = 1; iter_i <= capacity; iter_i++)
            heap[iter_i] = iter_i;

        // Print a message depending on whether there's one or more seats
        if (capacity > 1)
            outputWriter.println(capacity + " Seats are made available for reservation");
        else
            outputWriter.println(capacity + " seat available for reservation");
    }

    // Add a given number of new seats to the availability heap
    public void addSeats(int newSeatCount, WaitlistedHeap waitlistedHeap, RedBlackTree redBlackTree) {
        for (int iter_i = 1; iter_i <= newSeatCount; iter_i++) {
            add(capacity + iter_i);
        }
        // Update the capacity of the heap
        capacity += newSeatCount;
        outputWriter.println("Additional " + newSeatCount + " Seats are made available for reservation");

        // Process waitlisted users and reserve seats for them
        for (int iter_i = 1; iter_i <= newSeatCount && waitlistedHeap.count > 0; iter_i++) {
            int seatId = delete();
            int userId = waitlistedHeap.delete();
            outputWriter.println("User " + userId + " reserved seat " + seatId);
            // Reserve the seat in the Red-Black Tree
            redBlackTree.insertRedBlackNode(redBlackTree.root, userId, seatId);
            redBlackTree.count++;
        }
    }

    // Add a new value (seat) to the heap and re-adjust the heap
    public void add(int value) {
        count++; // Increase the number of available seats
        heap[count] = value;

        int index = count;
        // Re-adjust the heap by moving the new value to its correct position
        while (index > 1) {
            int parent = index / 2;
            if (heap[parent] > heap[index]) {
                swap(heap, parent, index);
                index = parent;
                continue;
            }
            break;
        }
    }

    // Delete the root of the heap (the minimum seat) and re-adjust the heap
    public int delete() {
        if (count == 0) {
            outputWriter.println("No more available space");
            return -1; // Return -1 if the heap is empty
        }
        int seat = heap[1]; // The root of the heap is the smallest value (available seat)

        // Replace the root with the last element in the heap and remove the last element
        heap[1] = heap[count];
        heap[count] = -1;
        count--;

        int index = 1;
        // Re-adjust the heap by moving the root value to its correct position
        while (index <= count) {
            int leftChildIndex = 2 * index > count ? -1 : 2 * index;
            int rightChildIndex = 2 * index + 1 > count ? -1 : 2 * index + 1;

            // Exit if no children exist
            if (leftChildIndex == -1 && rightChildIndex == -1)
                break;

            // If there's only a left child, compare with the left child
            if (rightChildIndex == -1) {
                if (heap[leftChildIndex] < heap[index]) {
                    swap(heap, leftChildIndex, index);
                    index = leftChildIndex;
                    continue;
                } else {
                    break;
                }
            }

            // If there's only a right child, compare with the right child
            if (leftChildIndex == -1) {
                if (heap[rightChildIndex] < heap[index]) {
                    swap(heap, rightChildIndex, index);
                    index = rightChildIndex;
                    continue;
                } else {
                    break;
                }
            }

            // Compare both children and swap with the smaller one
            if (heap[leftChildIndex] < heap[index] && heap[leftChildIndex] < heap[rightChildIndex]) {
                swap(heap, leftChildIndex, index);
                index = leftChildIndex;
            } else if (heap[rightChildIndex] < heap[index] && heap[rightChildIndex] < heap[leftChildIndex]) {
                swap(heap, rightChildIndex, index);
                index = rightChildIndex;
            } else {
                break; // Exit if the heap property is satisfied
            }
        }
        return seat; // Return the reserved seat ID
    }

    // Swap two values in the heap
    public void swap(int[] heap, int pos1, int pos2) {
        int temp = heap[pos1];
        heap[pos1] = heap[pos2];
        heap[pos2] = temp;
    }

    // Print the current state of the heap
    public void printHeap() {
        for (int iter_i = 1; iter_i <= capacity; iter_i++) {
            outputWriter.print(heap[iter_i] + " ");
        }
        outputWriter.println();
    }
}
