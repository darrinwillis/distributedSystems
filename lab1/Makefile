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
		ProcessManager.java \
		MigratableProcessManagerInterface.java \
		ProcessManagerClient.java \
		TransactionalIO/TransactionalFileOutputStream.java \
		TransactionalIO/TransactionalFileInputStream.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	find . -type f -name "*.class" -delete
