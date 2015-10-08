Team members:  
Programmer :Yu Ma, Luyao Ma  
Protocol Disginer: Yu Ma, Luyao Ma  

Breif summary:  
Our discovery server support such commands: ADD, REMOVE, LOOKUP  

Supported protocol:
ADD <unit1> <unit2> <hostip> <port>     //Add new conversion server with ip and port  
REMOVE <hostip> <port>                  //Remove existed conversion server with units (order is not sensitive)  
LOOKUP <unit1> <unit2>                  //Lookup existed conversion server with its ip and port (order is not sensitive)  


Suggest test commands:  
ADD $ y 192.168.0.1 1234  
ADD oz $ 192.168.0.2 1233  
LOOKUP $ y   
REMOVE 192.168.0.2 1233  
LOOKUP OZ $  
