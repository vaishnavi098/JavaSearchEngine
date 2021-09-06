rem del MySearchEngine.class
rem del Stemmer.class
rem del CheckFor.class
rem del configData.class

rem javac  Stemmer.java
rem javac  CheckFor.java
rem javac  configData.java
cd work
del tmpFile.txt intStemFile.txt intTernFile.txt
del *.txt
cd ..
del MySearchEngine.class
javac MySearchEngine.java
java MySearchEngine index "g:\kkk\docin"
