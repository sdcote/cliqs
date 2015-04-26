This is the main configuration directory for your application, it is read first
by the application and should contains your system properties.

This directory is also placed on the class path by the application. This means 
that any resources loaded by the class loaded can be placed here. Note this 
does not apply to JAR files and they will beed to be "exploded" into individual 
files and sub-directories for them to be found and loaded. To load your own or 
other dependent JAR files, you must build the project, adding the dependencies 
to the build file.
