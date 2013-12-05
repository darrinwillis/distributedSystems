# A makefile for lab2

# Java compiler variable
JC = mpijavac

# RMI compiler variable
RMI = rmic

# ECHO
ECHO = (echo)

# Any compilation flags
JFLAGS = -g -cp /usr/local/lib/openmpi/lib/mpi.jar:.

.SUFFIXES: .java .class
.java.class:
	@$(JC) $(JFLAGS) $*.java

CLASSES = \
	DataInterface.java \
	Pair.java \
	SequentialKMeans.java \
	ParallelKMeans.java \
	DNA.java \
	Hello.java


default: classes 
		@echo "Project Built"


classes: 	$(CLASSES:.java=.class)

tests:		$(TESTS:.java=.class)

clean:
	@find . -type f -name "*.class" -delete
	@echo "All clean!"
