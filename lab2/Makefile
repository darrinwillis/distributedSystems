# A makefile for lab1

# Transactional IO Directory
TRANS_DIR = TransactionalIO/ 

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
		$(JC) $(JFLAGS) $*.java

CLASSES = \
		440Remote.java \
		RemoteObjectReference.java \
		RMIMessage.java

default: classes
		@echo "Project Built"


classes: $(CLASSES:.java=.class)

clean:
	@find . -type f -name "*.class" -delete
	@echo "All clean!"
