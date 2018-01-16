# Remedy TSI Integration Library
Remedy Entry as TSI Event Integration library

## Prerequisite 
```
1. Java Sdk 1.8
2. Maven*
3. Remedy Api Java sdk*
(*Only required if you want to contribute and build the code)
```

## How to build it ?

Step 1: Clone the repository.
Step 2: Obtain the Remedy Java API SDK jar.
Step 3: Add the jar to local m2 repository.
```
mvn install:install-file -DgroupId=com.bmc.arsys.api -DartifactId=api80_build002 -Dversion=8.0 -Dpackaging=jar -Dfile=<directorylocation>\api80_build002-8.0.jar -DgeneratePom=true
```

Step 4: run maven install command to build
```
mvn clean compile install
```