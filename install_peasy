# peasycam 302 is downloadable from here
# http://mrfeinberg.com/peasycam/peasycam_302.zip
# unzip the file here (creating a peasycam directory
# and then run the following to install the files to local maven repo

mvn install:install-file \
    -Dfile=peasycam/library/peasycam.jar \
    -DgroupId=peasy \
    -DartifactId=peasycam \
    -Dversion=302 \
    -Dpackaging=jar \
    -DgeneratePom=true

mvn install:install-file \
    -Dfile=peasycam/library/peasy-math.jar \
    -DgroupId=peasy \
    -DartifactId=peasy-math \
    -Dversion=302 \
    -Dpackaging=jar \
    -DgeneratePom=true
