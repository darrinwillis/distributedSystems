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
		Remote440.java \
		RemoteObjectReference.java \
		RMIMessage.java \
		Remote440Exception.java \
		RemoteStub.java \
		ProxyDispatcher.java \
		RegistryServer.java \
		Communicate.java 

TESTS = \
		PrintingObject.java \
		PrintingObject_stub.java \
		RMIMessageTestingClient.java \
		RMIMessageTestingServer.java \
		CommunicateTest.java \
		TestClient.java \
		TestServer.java 

default: classes \
		 tests
		@echo "Project Built"


classes: 	$(CLASSES:.java=.class)

tests:		$(TESTS:.java=.class)

clean:
	@find . -type f -name "*.class" -delete
	@echo "All clean!"
