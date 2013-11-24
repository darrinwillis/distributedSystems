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
	WordCount.java \
	LongestLine.java


default: classes 
		@echo "Project Built"


classes: 	$(CLASSES:.java=.class)

tests:		$(TESTS:.java=.class)

clean:
	@find . -type f -name "*.class" -delete
	@echo "All clean!"
