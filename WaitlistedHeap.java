import java.io.PrintWriter;import java.io.PrintWriter;

public class WaitlistedHeap {
    // Array to represent the heap for storing users in the waitlist.
    HeapTuple[] waitlistHeap = new HeapTuple[10000];
    int count = 0;         // Tracks the number of users in the heap.
    int timestamp = 0;     // Used to record insertion order for users with equal priority.
    private PrintWriter outputWriter;   // Writer to output messages.

    // Constructor to initialize the heap with an output writer.
    public WaitlistedHeap(PrintWriter outputWriter) {
        this.outputWriter = outputWriter;
    }

    // Adds a new user to the waitlist with given priority, auto-generating a timestamp.
    public void add(int userId, int priority) {
        count++;
        waitlistHeap[count] = new HeapTuple(userId, priority, ++timestamp);

        // Adjust the heap to maintain the max-heap property based on priority and timestamp.
        int index = count;
        while (index > 1) {
            int parent = index / 2;
            if (waitlistHeap[parent].priority < waitlistHeap[index].priority) {
                swapHeapTuple(waitlistHeap, parent, index);
                index = parent;
            } else {
                break;
            }
        }
    }

    // Adds a user with a specific priority and timestamp.
    public void add(int userId, int priority, int timestamp) {
        count++;
        waitlistHeap[count] = new HeapTuple(userId, priority, timestamp);

        // Adjusts the heap to maintain max-heap properties.
        int index = count;
        while (index > 1) {
            int parent = index / 2;
            if (waitlistHeap[parent].priority < waitlistHeap[index].priority) {
                swapHeapTuple(waitlistHeap, parent, index);
                index = parent;
            } else {
                break;
            }
        }
    }

    // Swaps two elements in the heap at given positions.
    public void swapHeapTuple(HeapTuple[] waitlistHeap, int pos1, int pos2) {
        HeapTuple temp = new HeapTuple(waitlistHeap[pos1].userId, waitlistHeap[pos1].priority, waitlistHeap[pos1].timestamp);
        waitlistHeap[pos1].priority = waitlistHeap[pos2].priority;
        waitlistHeap[pos1].userId = waitlistHeap[pos2].userId;
        waitlistHeap[pos1].timestamp = waitlistHeap[pos2].timestamp;
        waitlistHeap[pos2].priority = temp.priority;
        waitlistHeap[pos2].userId = temp.userId;
        waitlistHeap[pos2].timestamp = temp.timestamp;
    }

    // Removes and returns the user with the highest priority (root of the heap).
    public int delete() {
        if (count == 0) {
            return -1;  // Return -1 if the heap is empty.
        }

        int removedWaitListedUser = waitlistHeap[1].userId;
        waitlistHeap[1] = new HeapTuple(waitlistHeap[count].userId, waitlistHeap[count].priority, waitlistHeap[count].timestamp);
        waitlistHeap[count] = null;
        count--;

        // Re-adjusts the heap to maintain the max-heap property.
        int index = 1;
        while (waitlistHeap[index] != null) {
            int leftChildIdx = 2 * index > count ? -1 : 2 * index;
            int rightChildIdx = 2 * index + 1 > count ? -1 : 2 * index + 1;

            // No children, break out of the loop.
            if (leftChildIdx == -1 && rightChildIdx == -1) break;

            // Left child only case.
            if (rightChildIdx == -1) {
                if (waitlistHeap[leftChildIdx].priority > waitlistHeap[index].priority ||
                        (waitlistHeap[leftChildIdx].priority == waitlistHeap[index].priority && waitlistHeap[leftChildIdx].timestamp < waitlistHeap[index].timestamp)) {
                    swapHeapTuple(waitlistHeap, leftChildIdx, index);
                    index = leftChildIdx;
                } else break;
            }
            // Right child only case.
            else if (leftChildIdx == -1) {
                if (waitlistHeap[rightChildIdx].priority > waitlistHeap[index].priority ||
                        (waitlistHeap[rightChildIdx].priority == waitlistHeap[index].priority && waitlistHeap[rightChildIdx].timestamp < waitlistHeap[index].timestamp)) {
                    swapHeapTuple(waitlistHeap, rightChildIdx, index);
                    index = rightChildIdx;
                } else break;
            }
            // Both children exist.
            else if (waitlistHeap[leftChildIdx].priority > waitlistHeap[index].priority &&
                    (waitlistHeap[leftChildIdx].priority > waitlistHeap[rightChildIdx].priority ||
                            (waitlistHeap[leftChildIdx].priority == waitlistHeap[rightChildIdx].priority && waitlistHeap[leftChildIdx].timestamp < waitlistHeap[rightChildIdx].timestamp))) {
                swapHeapTuple(waitlistHeap, leftChildIdx, index);
                index = leftChildIdx;
            } else if (waitlistHeap[rightChildIdx].priority > waitlistHeap[index].priority &&
                    (waitlistHeap[rightChildIdx].priority > waitlistHeap[leftChildIdx].priority ||
                            (waitlistHeap[rightChildIdx].priority == waitlistHeap[leftChildIdx].priority && waitlistHeap[rightChildIdx].timestamp < waitlistHeap[leftChildIdx].timestamp))) {
                swapHeapTuple(waitlistHeap, rightChildIdx, index);
                index = rightChildIdx;
            }   else if (waitlistHeap[rightChildIdx].priority == waitlistHeap[index].priority && waitlistHeap[rightChildIdx].timestamp<waitlistHeap[index].timestamp )
            {
                swapHeapTuple(waitlistHeap, rightChildIdx, index);
                index = rightChildIdx;
                continue;
            }
            else if (waitlistHeap[leftChildIdx].priority == waitlistHeap[index].priority && waitlistHeap[leftChildIdx].timestamp<waitlistHeap[index].timestamp )
            {
                swapHeapTuple(waitlistHeap, leftChildIdx, index);
                index = leftChildIdx;
                continue;
            }
            else break;
        }
        return removedWaitListedUser;
    }

    // Removes a user from a specific index in the heap.
    public int deleteFromSpecificIndex(int index) {
        int removedWaitListedUser = waitlistHeap[index].userId;
        waitlistHeap[index] = new HeapTuple(waitlistHeap[count].userId, waitlistHeap[count].priority, waitlistHeap[count].timestamp);
        waitlistHeap[count] = null;
        count--;

        // Adjust the heap after removal.
        while (waitlistHeap[index] != null) {
            int leftChildIdx = 2 * index > count ? -1 : 2 * index;
            int rightChildIdx = 2 * index + 1 > count ? -1 : 2 * index + 1;

            // No children case.
            if (leftChildIdx == -1 && rightChildIdx == -1) break;

            // Left child only case.
            if (rightChildIdx == -1) {
                if (waitlistHeap[leftChildIdx].priority > waitlistHeap[index].priority) {
                    swapHeapTuple(waitlistHeap, leftChildIdx, index);
                    index = leftChildIdx;
                } else break;
            }
            // Right child only case.
            else if (leftChildIdx == -1) {
                if (waitlistHeap[rightChildIdx].priority > waitlistHeap[index].priority) {
                    swapHeapTuple(waitlistHeap, rightChildIdx, index);
                    index = rightChildIdx;
                } else break;
            }
            // Both children exist case.
            else if (waitlistHeap[leftChildIdx].priority > waitlistHeap[index].priority &&
                    (waitlistHeap[leftChildIdx].priority > waitlistHeap[rightChildIdx].priority ||
                            (waitlistHeap[leftChildIdx].priority == waitlistHeap[rightChildIdx].priority && waitlistHeap[leftChildIdx].timestamp < waitlistHeap[rightChildIdx].timestamp))) {
                swapHeapTuple(waitlistHeap, leftChildIdx, index);
                index = leftChildIdx;
            } else if (waitlistHeap[rightChildIdx].priority > waitlistHeap[index].priority &&
                    (waitlistHeap[rightChildIdx].priority > waitlistHeap[leftChildIdx].priority ||
                            (waitlistHeap[rightChildIdx].priority == waitlistHeap[leftChildIdx].priority && waitlistHeap[rightChildIdx].timestamp < waitlistHeap[leftChildIdx].timestamp))) {
                swapHeapTuple(waitlistHeap, rightChildIdx, index);
                index = rightChildIdx;
            } else break;
        }
        return removedWaitListedUser;
    }

    //same redundant function that can be used to remove from a specific position
    public int deleteFromSpeificIndex(int index) {

        int removedWaitListedUser=waitlistHeap[index].userId;
        waitlistHeap[index] = new HeapTuple(waitlistHeap[count].userId, waitlistHeap[count].priority,waitlistHeap[count].timestamp);
        waitlistHeap[count] = null;
        count--;

        //swapping the lower values with the root and bringing to its right position

        while (waitlistHeap[index] != null) {
            int left_child_index = 2 * (index) > count ? -1 : 2 * (index);
            int right_child_index = 2 * (index) + 1 > count ? -1 : 2 * (index) + 1;


            if (left_child_index == -1 && right_child_index == -1)
                break;
            if(right_child_index==-1)
            {
                if(waitlistHeap[left_child_index].priority>waitlistHeap[index].priority) {
                    swapHeapTuple(waitlistHeap, left_child_index, index);
                    index=left_child_index;
                    continue;
                }
                else
                    break;
            }
            if(left_child_index==-1)
            {
                if(waitlistHeap[right_child_index].priority>waitlistHeap[index].priority) {
                    swapHeapTuple(waitlistHeap, right_child_index, index);
                    index=right_child_index;
                    continue;
                }
                else
                    break;
            }

            if ( waitlistHeap[left_child_index].priority > waitlistHeap[index].priority && ( waitlistHeap[left_child_index].priority > waitlistHeap[right_child_index].priority ||(waitlistHeap[left_child_index].priority == waitlistHeap[right_child_index].priority && waitlistHeap[left_child_index].timestamp < waitlistHeap[right_child_index].timestamp))) {
                swapHeapTuple(waitlistHeap, left_child_index, index);
                index = left_child_index;
                continue;
            } else if ( waitlistHeap[right_child_index].priority > waitlistHeap[index].priority &&  (waitlistHeap[right_child_index].priority > waitlistHeap[left_child_index].priority ||(waitlistHeap[right_child_index].priority == waitlistHeap[left_child_index].priority && waitlistHeap[right_child_index].timestamp < waitlistHeap[left_child_index].timestamp))) {
                swapHeapTuple(waitlistHeap, right_child_index, index);
                index = right_child_index;
                continue;
            }
            else if (waitlistHeap[right_child_index].priority == waitlistHeap[index].priority && waitlistHeap[right_child_index].timestamp<waitlistHeap[index].timestamp )
            {
                swapHeapTuple(waitlistHeap, right_child_index, index);
                index = right_child_index;
                continue;
            }
            else if (waitlistHeap[left_child_index].priority == waitlistHeap[index].priority && waitlistHeap[left_child_index].timestamp<waitlistHeap[index].timestamp )
            {
                swapHeapTuple(waitlistHeap, left_child_index, index);
                index = left_child_index;
                continue;
            }else break;

        }
        return removedWaitListedUser;

//in the above handle the duplicate priority case later on

    }

    public void updatePriority(int userId,int userPriority)
    {
        boolean updated=false;
        for(int iter_i=1;iter_i<=count;iter_i++)
        {
            if(userId==waitlistHeap[iter_i].userId) {

                //this would be incorrect since it wouldnt move up the heap
                //Perform sequence of operation remove and insert
                int timestamp=waitlistHeap[iter_i].timestamp;
                deleteFromSpeificIndex(iter_i);
                add(userId,userPriority,timestamp);
//                waitlistHeap[iter_i].priority = userPriority;
                updated=true;
                break;
            }
        }
        if(updated)
            outputWriter.println("User "+userId+" priority has been updated to "+ userPriority);
        else outputWriter.println("User "+userId+" priority is not updated");
    }

    //how do u exit the user in the waitlist
    public void exitWaitlist(int userId)
    {
        boolean updated=false;
        for(int iter_i=1;iter_i<=count;iter_i++)
        {
            if(userId==waitlistHeap[iter_i].userId) {
                deleteFromSpeificIndex(iter_i);
                updated=true;
            }
        }
        if(updated)
            outputWriter.println("User "+userId+" is removed from the waiting list ");
        else outputWriter.println("User "+userId+" is not in waitlist");
    }
    public void releaseWaitList(int userId)
    {

        for(int iter_i=1;iter_i<=count;iter_i++)
        {
            if(userId==waitlistHeap[iter_i].userId) {
                deleteFromSpeificIndex(iter_i);

            }
        }
    }

    class HeapTuple {
        int userId;
        int priority;
        int timestamp;


        public HeapTuple(int userId, int priority,int timestamp) {
            this.userId = userId;
            this.priority = priority;
            this.timestamp=timestamp;
        }

    }
}

