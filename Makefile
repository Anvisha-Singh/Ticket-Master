# Compiler
JC = javac

# The Java source files
SRC = gatorTicketMaster.java \
      RedBlackTree.java \
      AvailabilityHeap.java \
      WaitlistedHeap.java

# The corresponding class files
CLASSES = $(SRC:.java=.class)

# Default target to compile all classes
default: classes

# Target to compile all Java source files
classes: $(CLASSES)

# Rule to compile each .java file into a .class file
%.class: %.java
	$(JC) $<

# Clean up all the compiled .class files
clean:
	rm -f *.class

# Run the Java program (gatorTicketMaster)
run: gatorTicketMaster.class
	java gatorTicketMaster
