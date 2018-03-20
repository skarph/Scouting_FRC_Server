# FRC Scouting Bluetooth Software (Serverside)
### Overview
This project is to remidie the problem where person scouting during FRC competitions may not have access to the internet, for whatever reason, and cannot communicate their 
information. FRC guildlines ban the use of personal wireless radios EXCEPT for those using the Bluetooth protocol. Thus this project was born to bridge that gap between people during 
competition at FRC events. The program is intended to run as a Server with it's acceptable client being found in its [sister project here](https://github.com/Notsure45/Scouting_FRC). 
The program is able to load acceptable JSON data into its database and share it with Android devices running the client, and can take information from the client and add it to the 
database. Finally, it can output all this data out into a neat little JSON formatted file.
### Building
This was originally inted for Linux x64 based systems, however with *minor* modifications it can be used on Windows x64 devices. This program has not been tested on ARM or x86 
devices, any problems with those I would love to fix. The process for building on a **Linux x64 Based System** is as follows:
1. Clone the repository
2. Run the enclosed ```./gradlew clean build```
3. The outputed Jar with all dependencies in it should be in ```./build/libs/ScoutingBluetoothServer.jar``` . Feel free to move it at this point.

For **Windows x64 Based Systems**
1. Clone the repository
2. Find the file ```WINDOWS.gradle```; delete ```build.gradle```
3. Rename ```WINDOWS.gradle``` to ```build.gradle```
4. Run the enclosed ```./gradlew clean build```
### Running the Program
From the Program's help message:
```
Welcome to the Bluetooth FIRST Scouting tool by Max Neyraval (2018)!
Simple program to handle scouters over bluetooth. Please turn on the bluetooth radio and give the program the appropriate permissions!

Anyway, here is the proper usage of this program:

java -jar ScoutingBluetoothServer.jar <LOAD DATA> <EXPORT PATH; <TIME BETWEEN SAVES>

Where <LOAD DATA> is a valid path to valid JSON data that can be loaded
	Data is a JSON file that either has been exported or has team initialization data. See example.
Where <EXPORT PATH&> is a path to the desired output (make sure you have proper permissions!)
And Where <TIME BETWEEN SAVES>; Is the time between autoexporting data, in minutes
	 Set this value to 0 to not export OR -1 to export every time a client issues a connection.
```
It is important to remember that the program must be given read and write permissions to the specified Load and Export files, and must also have access to the Bluetooth API. It is 
recomended to run the program as an administrator.







###### Copyright 2018 Max Neyraval under the Apache License Version 2.0
