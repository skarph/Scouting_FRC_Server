import json
import csv
import sys
qList = None;
outFile = None;
jsonData = None;
try:
    outFile = open(sys.argv[2],"w+");
    with open("questions.json") as file:
        qList = json.loads(file.read());
    
    with open (sys.argv[1],"r") as file:
        jsonData = json.loads(file.read());
except:
    print("Incorrect usage! Please use:\nPython3 converter.py <PATH_TO_INPUT> <PATH_TO_OUTPUT>");
    sys.exit(1);
teamData = {};
avgData = {};
    
for deviceName in jsonData:
    if deviceName == "__TEAMLIST__":
        continue;
    entryCount = int(jsonData[deviceName]["entryCount"]);
    print(deviceName + " has " + str(entryCount) + " entries!");
    for i in range(0, entryCount):
        try:
            jsonData[deviceName][str(i)] = json.loads(jsonData[deviceName][str(i)]);
            ### PLACE CODE HERE FOR IT TO ITERATE OVER EVERY ENTRY
            teamNum = jsonData[deviceName][str(i)]["teamID"];
            
            try:
                test = teamData[teamNum]
                
            except KeyError:
                teamData[teamNum] = {};
                teamData[teamNum]["AVG"] = {};
                teamData[teamNum]["__COUNT__"] = "0";

            teamData[teamNum]["__COUNT__"] = str(int(teamData[teamNum]["__COUNT__"])+1);
            teamData[teamNum][teamData[teamNum]["__COUNT__"]] = jsonData[deviceName][str(i)];
            del teamData[teamNum][teamData[teamNum]["__COUNT__"]]["teamID"];
            
            for key in teamData[teamNum][teamData[teamNum]["__COUNT__"]]:
                if type(teamData[teamNum][teamData[teamNum]["__COUNT__"]][key]) == str:
                    if not(key == "comments"):
                        newKVpair = teamData[teamNum][teamData[teamNum]["__COUNT__"]][key].split(": ");
                        teamData[teamNum][teamData[teamNum]["__COUNT__"]][key] = int(newKVpair[1]);

                if type(teamData[teamNum][teamData[teamNum]["__COUNT__"]][key]) == bool:
                    try:
                        test = teamData[teamNum]["AVG"][key];
                    except KeyError:
                        teamData[teamNum]["AVG"][key] = [0,0,None];
                    teamData[teamNum]["AVG"][key][2] = bool;
                    
                    teamData[teamNum]["AVG"][key][1] += 1;
                    if teamData[teamNum][teamData[teamNum]["__COUNT__"]][key]:
                        teamData[teamNum]["AVG"][key][0] += 1;
                    else:
                        teamData[teamNum]["AVG"][key][0] -= 1;
                if type(teamData[teamNum][teamData[teamNum]["__COUNT__"]][key]) == int:
                    try:
                        test = teamData[teamNum]["AVG"][key];
                    except KeyError:
                        teamData[teamNum]["AVG"][key] = [0,0,None];
                    teamData[teamNum]["AVG"][key][0] += teamData[teamNum][teamData[teamNum]["__COUNT__"]][key];
                    teamData[teamNum]["AVG"][key][1] += 1;
                    teamData[teamNum]["AVG"][key][2] = int;
                if type(teamData[teamNum][teamData[teamNum]["__COUNT__"]][key]) == str:   
                    try:
                        test = teamData[teamNum]["AVG"][key];
                    except KeyError:
                        teamData[teamNum]["AVG"][key] = "";
                    teamData[teamNum]["AVG"][key] = teamData[teamNum]["AVG"][key] + teamData[teamNum][teamData[teamNum]["__COUNT__"]][key] + "; \n"
                        
            ### END ITERATION PLACE
        except KeyError:
            print(deviceName + " has no entry at key "+str(i)+", skipping...");

for team in teamData:
     for element in teamData[team]["AVG"]:
        if type(teamData[team]["AVG"][element])==list:
            try:
                if teamData[team]["AVG"][element][2] == bool:
                    teamData[team]["AVG"][element] = str(teamData[team]["AVG"][element][0]) + "/" + str(teamData[team]["AVG"][element][1]);
                else:
                    teamData[team]["AVG"][element] = float(teamData[team]["AVG"][element][0]) / float(teamData[team]["AVG"][element][1]);
            except IndexError:
                pass;
            #
        #
     avgData[team] = teamData[team]["AVG"];
print("\n");

outWriter = csv.writer(outFile);
outWriter.writerow(["Team ID"]+list(qList.values())+["Times Scouted"]);

for team in avgData:
    outWriter.writerow([team]+list(avgData[team].values())+[teamData[team]["__COUNT__"]]);
    outFile.flush();
    
outFile.close();
