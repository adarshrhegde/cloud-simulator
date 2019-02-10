#Cloud Simulator

##Setup Instructions:
1. Add lib/cloudsim-4.0.jar to the classpath of the project
2. SBT version : 1.0
3. Scala version : 2.12.6
4. Java version : jdk 1.8

####Note : I was getting a StackOverflow error with Intellij. 
This is because Intellij Scala compiler overrides the XSS settings.
If you get the same issue please increase the stack size.

##Logging:
SLF4J + Logback has been setup in the build.sbt. Log is written into 
file cloudsim.log in the project directory.


##Configuration:
Configuration is achieved using Pure Config(wrapper over Type Safe Config).
All configurations are described within the application.conf

##How to Run:

Open Main.scala and run the CloudSim App

##How to Test:

Open CloudSimTest and execute all test cases. 

##Description

#####DomainObjects : 
This contains scala representation of all cloudsim elements.
This helps in conceptualizing all the elements.

#####ConfigReader: 
This class is used to load the configuration
This maps configuration directly to scala objects. This type 
of design is used to add flexibility to the configuration.

#####JavaObjectMapperFunctions: 
Since cloudsim is written in Java, we need to work with Java 
data structures. Therefore I have written mapper functions for
all elements in cloudsim. The mapper converts the Scala objects 
to Java objects and in the process also creates the Simulation
elements. 

#####Simulator:
This is the class that performs the simulation with the help of 
all the above classes.
It calls the ConfigReader to load all the configurations and create
the elements in CloudSim.
This class is responsible for handling the simulation

#####Platform as a service:
Currently I have tried to model the Paas model into the implementation.
In the Paas model the third party provides infrastructure and system 
level support. Vm and OS management is also taken care of. 

Thus I have modelled a scenario where datacenters will have hosts and 
the system will have a list of preconfigured VMs that will be allocated to 
Hosts. 

The end user will only send a cloudlet(representing an application on the platform)
and the cloudlet will be scheduled on the best possible VM.

#####Role of the Simulator:
The simulator acts as a broker that suggests the best cloud provider in 
this case represented as a Datacenter. The simulator runs the same cloudlet on
all the available datacenter and helps the user in deciding the best provider.
The most optimal datacenter is suggested to the end user to minimize costs.

#####Results:
All the results including the cost of execution and the best fit datacenter 
is displayed in the logs. 

 