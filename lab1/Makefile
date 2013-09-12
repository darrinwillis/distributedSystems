# A makefile for lab1

# Transactional IO Directory
TRANS_DIR = TransactionalIO/ 

# Java compiler variable
JC = javac

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
		ProcessDelegationServer.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	find . -type f -name "*.class" -delete
