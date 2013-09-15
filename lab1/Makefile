# A makefile for lab1

# Transactional IO Directory
TRANS_DIR = TransactionalIO/ 

# Java compiler variable
JC = javac

# RMI compiler variable
RMI = rmic

# Any compilation flags
JFLAGS = -g

.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
		MigratableProcess.java \
		TimedProcess.java \
		ProcessManager.java \
		MasterServerInterface.java \
		ProcessManagerClientInterface.java \
		ProcessManagerClient.java \
		TransactionalFileOutputStream.java \
		TransactionalFileInputStream.java \
		ProcessIO.java \
		ProcessDelegationServer.java \
		TestingClient.java \
		GrepProcess.java

default: classes
		$(RMI) ProcessManagerClient
		$(RMI) ProcessDelegationServer

classes: $(CLASSES:.java=.class)

clean:
	find . -type f -name "*.class" -delete
