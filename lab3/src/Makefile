# A makefile for lab2

# Java compiler variable
JC = javac

# RMI compiler variable
RMI = rmic

# ECHO
ECHO = (echo)

# Any compilation flags
JFLAGS = -g

.SUFFIXES: .java .class
.java.class:
		@$(JC) $(JFLAGS) $*.java

CLASSES = \
		FileServerInterface.java \
		MasterFileServerInterface.java \
		Config.java \
		MasterServer.java \
		RMIOutputStreamInterf.java \
		RMIInputStreamInterf.java \
		RMIOutputStreamImpl.java \
        RMIInputStreamImpl.java \
		RMIOutputStream.java \
        RMIInputStream.java \
		RMIPipe.java \
		TestClient.java \
		FileIO.java

default: classes 
		@echo "Project Built"


classes: 	$(CLASSES:.java=.class)

tests:		$(TESTS:.java=.class)

clean:
	@find . -type f -name "*.class" -delete
	@echo "All clean!"
