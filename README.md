
This a template project for a Command Line Interface (CLI) application.

Overview
========
Many times it is necessary to write utilities which run from the command line.
While the traditional approach is just to write a simple project with one class
that has a main method, ther are times when multiple utility classes need to be 
written. This often involves duplication of code in different projects.

This project template allow the developer to create a set of actions which can 
be invoked from the command line as a part of one application; adding as many 
actions as needed all with the ability to specify and parse their own command
line arguments.

### Why A Command Line Interface
Beyond the simplicity of calling a simple command to do your bidding from any
terminal window (you don't need a windowing manager so CLIs are great for *nix 
tasks), CLI utilities can be placed in scripts for automated processing.

So you have a command which does something useful; maybe it's a part of you 
daily or weekly routine. Now imagine creating a cron or scheduled job to 
perform that task without you. CLI utilities allow your logic to be called 
almost anywhere, at any time; with or without an operator.  This is true 
utility.

In many ways, a CLI is far more useful than a web interface.

Structure
=========
Java code is placed into 'Actions' which can be called from the command line 
interface (CLI). The AbstractAction class contains most of the generic utility
logic all actions can share. Extending this class makes it relatively easy to
create new actions.

The application use Spring IoC to make new actions available to the CLI. This 
is controled by the CoreContext.xml file in the configuration directory /cfg.

Adding new actions involve creating a new Action class which extends 
AbstractAction or at least implements the Action interface. Place your logic in 
the Action.execute() method. Then update the CoreContext.xml file to register 
your new action with the application.

Information about the different data processing environments (DEV, ST, PROD)
are stored in properties files which are easily edited.

NOTE: The passwords in the property files are encrypted. To change these values
you need to use the "cliqs encrypt -token <value>" action to encrypt the values. 
There is no decrypt action for the sake of security.

Configuration
=============
Everything is configured through system properties. The are loaded from the 
command line of course, but also from a properties file located in the /cfg
directory in the root of the application.

The app will search for the named file in 4 locations, each subsequent found 
file being used to augment and over write the properties of previously loaded 
properties files:

 * currently set class path "/cfg"
 * home directory of the user running the JVM
 * directory specified by the "cfg.dir" system property
 * current working directory
 
 
Building
========
This project uses Gradle as its build tool. If you have Gradle installed, great.
If not, then just use the Gradle wrapper (gradlew) included in this project:
    gradlew.sh - for Unix and Mac systems
    gradlew.bat - for Windows systems

The default tasks are clean, build, and installApp so running just the above 
wrapper script should build everything.

The 'installApp' target/task will create a complete installation directory under
<projectDir>/build/install and can be copied to its own application directory.
Everything needed to run the code from the command line is included.

Installing
==========
Just copy the contents of the <projectDir>/build/install directory to the
desired host directory (e.g., /opt on Unix) and make sure the bin directory is
on the execution path if you want to execute the Get command from anywhere.

On Unix systems, it is necessary to adjust file permissions to get the commands 
to execute: "chmod 755 Get"

Don't forget to user the proper version of Java for this utility. Many of the 
hosts (DEV, ST, UAT, PROD) may use a different version of the JVM while this 
utility requires the use of Java 7. This should not be much of an issue for 
most as this utility will most likely be run from development workstations and 
not one of the hosts in the data center. If this is to be the case, it should 
be a simple matter to install a modern JRE locally and set JAVA_HOME and PATH 
appropriately to use the proper Java version. 
 