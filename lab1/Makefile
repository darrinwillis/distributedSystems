# A makefile for lab1


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
		ProcessManagerClient.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class

