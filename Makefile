JFLAGS= -g
JC =javac
.SUFFIXES:.java .class
.java.class:
	$(JC) $(JFLAGS)  $*.java

CLASSES = \
	Barrier.java \
	PrefixSum.java

default: classes

classes: $(CLASSES:.java=.class)

clean:\
	$(RM) *.class
