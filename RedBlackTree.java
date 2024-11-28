import java.io.PrintWriter;
import java.util.Arrays;

public class RedBlackTree {
    Node root = null;
    int count = 0;
    private PrintWriter outputWriter;

    // Constructor for RedBlackTree
    public RedBlackTree(PrintWriter outputWriter) {
        this.outputWriter = outputWriter;
    }

    // Method to reserve a seat for a user
    public void reserve(int userId, int priority, AvailabilityHeap availabilityHeap, WaitlistedHeap waitlistedHeap) {
        if (availabilityHeap.count == 0) {
            outputWriter.println("User " + userId + " is added to the waiting list");
            waitlistedHeap.add(userId, priority);
            return;
        }
        count++;
        int seatId = availabilityHeap.delete();
        outputWriter.println("User " + userId + " reserved seat " + seatId);
        outputWriter.flush();
        if (root == null)
            root = new Node(userId, seatId, null, true);
        else {
            insertRedBlackNode(root, userId, seatId);
        }
    }

    // Method to release seats for a user
    public void releaseSeats(int userId, AvailabilityHeap availabilityHeap, WaitlistedHeap waitlistedHeap) {
        Node release_node = search(userId, root);
        if (release_node == null)
            return;
        int seatId = release_node.seat;
        deleteRedBlackNode(release_node);
        count--;
        int added_user = waitlistedHeap.delete();
        if (added_user != -1) {
            insertRedBlackNode(root, added_user, seatId);
            outputWriter.println("User " + added_user + " reserved seat " + seatId);
            count++;
        } else {
            availabilityHeap.add(seatId);
        }
    }

    // Method to print all reservations
    public void PrintReservations() {
        int arr[][] = new int[count][2];
        inorderTraversal(arr, root, new int[]{0});
        Arrays.sort(arr, (a, b) -> a[1] - b[1]);
        for (int iter_i = 0; iter_i < arr.length; iter_i++) {
            outputWriter.println("Seat " + arr[iter_i][1] + ", User " + arr[iter_i][0]);
            outputWriter.flush();
        }
    }

    // Helper method for inorder traversal
    public void inorderTraversal(int arr[][], Node node, int index[]) {
        if (node == null)
            return;
        inorderTraversal(arr, node.left_child, index);
        arr[index[0]][0] = node.userId;
        arr[index[0]++][1] = node.seat;
        inorderTraversal(arr, node.right_child, index);
    }

    // Method to cancel a reservation
    public void Cancel(int seatId, int userId, AvailabilityHeap availabilityHeap, WaitlistedHeap waitlistedHeap) {
        Node to_Cancel = search(userId, root);
        int seat = to_Cancel.seat;
        if (to_Cancel == null)
            outputWriter.println("User " + userId + " has no reservation to cancel");
        else if (to_Cancel.seat != seatId)
            outputWriter.println("User " + userId + " has no reservation for seat " + seatId + " to cancel");
        else {
            outputWriter.println("User " + userId + " canceled their reservation");
            deleteRedBlackNode(to_Cancel);
            count--;
            int added_user = waitlistedHeap.delete();
            if (added_user != -1) {
                insertRedBlackNode(root, added_user, seat);
                outputWriter.println("User " + added_user + " reserved seat " + seatId);
                count++;
            } else {
                availabilityHeap.add(seat);
            }
        }
    }

    // Method to delete a node from the Red-Black tree
    public void deleteRedBlackNode(Node node) {
        Node parent = node.parent;
        boolean parentLeftChild = false;
        if (parent != null) {
            if (parent.left_child == node) parentLeftChild = true;
        }

        // Case 1: Node is a leaf
        if (node.left_child == null && node.right_child == null) {
            Node u = node;
            boolean rb = false;
            if (!u.black) rb = true;
            if (parent == null) {
                root = null;
                return;
            }
            if (parent.left_child == node) parent.left_child = null;
            else parent.right_child = null;
            if (!rb) bbCaseColor(parent);
        }
        // Case 2: Node has only right child
        else if (node.left_child == null) {
            Node u = node;
            Node v = node.right_child;
            boolean rb = false;
            if (u.black && !v.black) rb = true;
            if (parent == null) {
                root = node.right_child;
                root.black = true;
            } else if (parentLeftChild) {
                parent.left_child = node.right_child;
                node.right_child.parent = parent;
                if (rb) parent.left_child.black = true;
                else bbCaseColor(parent);
            } else {
                parent.right_child = node.right_child;
                node.right_child.parent = parent;
                if (rb) parent.right_child.black = true;
                else bbCaseColor(parent);
            }
        }
        // Case 3: Node has only left child
        else if (node.right_child == null) {
            Node u = node;
            Node v = node.right_child;
            boolean rb = false;
            if (u.black && !v.black) rb = true;
            if (parent == null) {
                root = node.left_child;
                root.black = true;
            } else if (parentLeftChild) {
                parent.left_child = node.left_child;
                node.left_child.parent = parent;
                if (rb) parent.left_child.black = true;
                else bbCaseColor(parent);
            } else {
                parent.right_child = node.left_child;
                node.left_child.parent = parent;
                if (rb) parent.right_child.black = true;
                else bbCaseColor(parent);
            }
        }
        // Case 4: Node has both children
        else {
            // Find inorder successor (rightmost node in left subtree)
            Node inorderSuccessor = node.left_child;
            boolean parent_left_child = true;
            while (inorderSuccessor.right_child != null) {
                inorderSuccessor = inorderSuccessor.right_child;
                parent_left_child = false;
            }
            // Replace node's data with inorder successor's data
            node.userId = inorderSuccessor.userId;
            node.seat = inorderSuccessor.seat;
            // Remove inorder successor
            if (parent_left_child) inorderSuccessor.parent.left_child = null;
            else inorderSuccessor.parent.right_child = null;
            if (inorderSuccessor.black == false) return;
            else bbCaseColor(inorderSuccessor.parent);
        }
    }

    // Method to handle black-black case after deletion
    public void bbCaseColor(Node u) {
        Node parent = u;
        Node s = null;
        Node r = null;
        int count_red_children = 0;
        boolean r_parentLeftChild = false;
        boolean u_parentLeftChild = false;
        if (parent.left_child == null) u_parentLeftChild = true;
        boolean s_parentLeftChild = !u_parentLeftChild;
        if (s_parentLeftChild) s = parent.left_child;
        else s = parent.right_child;

        // Count red children of sibling
        if (s.left_child != null && !s.left_child.black) {
            count_red_children++;
            r_parentLeftChild = true;
            r = s.left_child;
        }
        if (s.right_child != null && !s.right_child.black) {
            count_red_children++;
            r_parentLeftChild = false;
            r = s.right_child;
        }

        // Case 1: Sibling is black and has at least one red child
        if (s.black && count_red_children > 0) {
            Node temp = null;
            if (s_parentLeftChild && r_parentLeftChild) {
                temp = LL_Rotation(parent);
            } else if (s_parentLeftChild && !r_parentLeftChild) {
                temp = LR_Rotation(parent);
            } else if (!s_parentLeftChild && r_parentLeftChild) {
                temp = RL_Rotation(parent);
            } else {
                temp = RR_Rotation(parent);
            }
            if (temp.parent == null) root = temp;
            r.black = true;
            root.black = true;
        }
        // Case 2: Sibling is black and has no red children
        else if (s.black && count_red_children == 0) {
            s.black = false;
            if (s.parent.black == false) {
                s.parent.black = true;
                return;
            } else {
                if (s.parent.parent != null)
                    bbCaseColor(s.parent.parent);
            }
        }
        // Case 3: Sibling is red
        else {
            Node temp = null;
            parent.black = false;
            s.black = true;
            if (s_parentLeftChild) {
                temp = LL_Rotation(s.parent);
                if (temp.parent == null) root = temp;
                rotateAndRecolor(temp.right_child);
            } else {
                temp = RR_Rotation(s.parent);
                if (temp.parent == null) root = temp;
                rotateAndRecolor(temp.left_child);
            }
        }
    }

    // Method to search for a node in the Red-Black tree
    public Node search(int userId, Node node) {
        if (node == null) return null;
        if (userId == node.userId) return node;
        if (userId > node.userId) return search(userId, node.right_child);
        return search(userId, node.left_child);
    }

    // Method to insert a new node into the Red-Black tree
    public void insertRedBlackNode(Node node, int userId, int seat) {
        if (node == null) {
            node = new Node(userId, seat, node, true);
            root = node;
        }
        if (userId < node.userId) {
            if (node.left_child != null) {
                insertRedBlackNode(node.left_child, userId, seat);
            } else {
                node.left_child = new Node(userId, seat, node, false);
                rotateAndRecolor(node.left_child);
            }
        } else if (userId > node.userId) {
            if (node.right_child != null) {
                insertRedBlackNode(node.right_child, userId, seat);
            } else {
                node.right_child = new Node(userId, seat, node, false);
                rotateAndRecolor(node.right_child);
            }
        }
    }

    // Method to rotate and recolor nodes after insertion
    public void rotateAndRecolor(Node node) {
        if (node == null) return;
        if (node.parent == null) {
            root = node;
            node.black = true;
            return;
        }
        if (node.parent.black || node.parent.parent == null) return;

        Node parent = node.parent;
        Node grandparent = node.parent.parent;
        Node greatgrandparent = node.parent.parent.parent;
        boolean greatgrandparentLeftChild = false;
        boolean grandparentLeftChild = false;
        boolean parentLeftChild = false;
        if (grandparent.left_child == parent) grandparentLeftChild = true;
        if (parent.left_child == node) parentLeftChild = true;
        if (greatgrandparent != null && greatgrandparent.left_child == grandparent) greatgrandparentLeftChild = true;
        Node temp_node = null;

        // Case 1: Uncle is red
        if (grandparentLeftChild && ((grandparent.right_child != null && !grandparent.right_child.black))) {
            node.parent.black = true;
            grandparent.right_child.black = true;
            grandparent.black = false;
            rotateAndRecolor(grandparent);
        } else if (!grandparentLeftChild && (grandparent.left_child != null && !grandparent.left_child.black)) {
            node.parent.black = true;
            grandparent.left_child.black = true;
            grandparent.black = false;
            rotateAndRecolor(grandparent);
        }
        // Case 2: Uncle is black
        else if ((grandparent.right_child == null || grandparent.right_child.black) || (grandparent.left_child == null || grandparent.left_child.black)) {
            if (grandparentLeftChild == true && parentLeftChild == true) {
                temp_node = LL_Rotation(grandparent);
            } else if (grandparentLeftChild == true && parentLeftChild == false) {
                temp_node = LR_Rotation(grandparent);
            } else if (grandparentLeftChild == false && parentLeftChild == false) {
                temp_node = RR_Rotation(grandparent);
            } else if (grandparentLeftChild == false && parentLeftChild == true) {
                temp_node = RL_Rotation(grandparent);
            }
            if (greatgrandparent != null) {
                if (greatgrandparentLeftChild) greatgrandparent.left_child = temp_node;
                else greatgrandparent.right_child = temp_node;
            }
            temp_node.black = true;
            temp_node.left_child.black = false;
            temp_node.right_child.black = false;
            if (temp_node.parent == null) root = temp_node;
        }
    }

    // Left-Left rotation
    public Node LL_Rotation(Node node) {
        Node x = node.left_child;
        Node y = x.right_child;
        x.right_child = node;
        node.left_child = y;
        x.parent = node.parent;
        node.parent = x;
        if (y != null) y.parent = node;
        return x;
    }

    // Right-Right rotation
    public Node RR_Rotation(Node node) {
        Node x = node.right_child;
        Node y = x.left_child;
        x.left_child = node;
        node.right_child = y;
        x.parent = node.parent;
        node.parent = x;
        if (y != null) y.parent = node;
        return x;
    }

    public Node LR_Rotation(Node node) {

        node.left_child = RR_Rotation(node.left_child);
        return LL_Rotation(node);
    }

    public Node RL_Rotation(Node node) {
        node.right_child = LL_Rotation(node.right_child);
        return RR_Rotation(node);
    }


    public void Available(WaitlistedHeap waitlistedHeap, AvailabilityHeap availabilityHeap) {
        outputWriter.println("Total Seats Available : " + availabilityHeap.count + ", Waitlist : " + waitlistedHeap.count);
    }

    public void Quit() {

        outputWriter.println("Program Terminated!!");
        outputWriter.flush();
        outputWriter.close();
        System.exit(0);
    }

    public void ReleaseSeats(int id1, int id2, RedBlackTree redBlackTree, AvailabilityHeap availabilityHeap, WaitlistedHeap waitlistedHeap) {
        outputWriter.println("Reservations of the Users in the range [" + id1 + ", " + id2 + "] are released");
        for (int iter_i = id1; iter_i <= id2; iter_i++) {
            waitlistedHeap.releaseWaitList(iter_i);
        }

        for (int iter_i = id1; iter_i <= id2; iter_i++) {
            redBlackTree.releaseSeats(iter_i, availabilityHeap, waitlistedHeap);
        }
    }


    //logic for deleting node starts here


    class Node {
        int userId;
        int seat;
        boolean black;
        Node parent;
        Node left_child;
        Node right_child;

        public Node(int userId, int seat, Node parent, boolean black) {
            this.userId = userId;
            this.seat = seat;
            this.parent = parent;
            this.right_child = null;
            this.left_child = null;
            this.black = black;
        }

    }


}

