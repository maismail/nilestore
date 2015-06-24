# Installation #

**Requirments:** Nilestore is fully implemented in java so you need to install java runtime environment [(JRE)](http://java.com/) first to use it.

  1. download the nilestore\_0.0.1\_alpha.zip file from the download tab.

for linux users:
```
>> unzip nilestore_0.0.1_alpha.zip -d nilestore
>> ls nilestore
lib/  nilestore  nilestore-main-0.0.1-SNAPSHOT.jar
```

  1. set **NILESTORE\_HOME** environment variable with the path of the nilestore installation.
  1. add **NILESTORE\_HOME** to your **PATH**.
  1. now type nilestore in your terminal/cmd, you should got the following output
```
Usage: Nilestore [options] [command] [command options]
  Options:
    -cl, --compression-level   compression level for the compression filter
                               attached with the network component it takes values from 0
                               to 9
                               Default: 9
    -h, --help                 help for command
    -net, --network-cmp        network component to be used. either mina or
                               netty
                               Default: mina
    -t, --num-threads          num of used threads for kompics
                               Default: 8
  Commands:
    create-introducer   create an introducer node
    create-client       create a client node
    create-monitor      create a monitor node
    run-simulator       run nilestore simulator
    start               start a node
    stop                stop a node
    put                 upload a file into the nilestore grid
    get                 download a file from the nilestore grid using its capabaility

```
# Usage #

let's setup a simple Nilestore grid consisting of 2 Peers + Introducer
in the same machine as processes as follows:
  * **Introducer**
> introducer could be created using the command create-introducer to get help on using that command you can type
```
>>nilestore -h create-introducer
  create an introducer node
Usage: create-introducer [options]
  Options:
    -d, --node-directory   node directory
                           Default: /home/USER/.nilestore
```
> now we are going to create an introducer node on the directory introducernode as following
```
>>nilestore create-introducer -d introducernode
introducer node created @ introducernode
>>ls introducernode
log/  nilestore.conf
```
> introducernode directory contains:
    1. log directory which contains the log file and the log configuration file "Log4j Properties File"
    1. nilestore.conf which is the configuration file for nilestore
```
>>cat introducernode/nilestore.conf

[introducernode]
ip=127.0.0.1
networkport=12345
webport=8081
```

> now we can start the introducer node as follows
```
>>nilestore start -d introducernode
[14:32:03,017] INFO  {NsIntroducerMain} initiating minaNetwork with Address=0@localhost:12345,compressionLevel=9
[14:32:03,037] INFO  {NsIntroducerMain} introducer initiated @ 0@localhost:12345
[14:32:03,039] INFO  {NsIntroducerServer} Initiated @ 0@localhost:12345
```

> after creating the introducer node now we are going to create the other two nodes
  * **Node1**
```
>>nilestore create-client -d node1
client node created @ node1
>>ls node1
log/  nilestore.conf
```

> the default configuration stored inside node1 are:
```
>>cat node1/nilestore.conf
[introducer]
ip=127.0.0.1
port=12345

[monitor]
enabled=false
ip=127.0.0.1
port=12340
updateperiod=20000

[node]
ip=127.0.0.1
networkport=12346
nickname=node1
webport=8082

[shares]
shares.k=3
shares.n=10

[storage]
enabled=true

```

> nilestore.conf contains different sections;
    1. **introducer section** which defines the ip and port used to communicate with the introducer node,
    1. **monitor section** which defines if there are a monitor node to send our status to or not and its ip and port if it exists,
    1. **node section** which defines the parameters of our node "Node1",
    1. **shares section** which defines the encoding paramters used in uploading the files,
    1. **storage section** which defines if that node has storage enabled or not.

> now we can start node1 using the previous configuration
```
>>nilestore start -d node1
[15:21:30,935] INFO  {NsPeerMain} initiating minaNetwork with Address=0@localhost:12346,compressionLevel=9
[15:21:31,083] INFO  {NsPeerMain} peer initiated @ 0@localhost:12346
[15:21:31,092] INFO  {NsWebServer} initiated @ http://localhost:8082
[15:21:31,119] INFO  {NsPeer} node1 Started
[15:21:31,240] INFO  {NsStorageServer} initiated with homeDir node1/
[15:21:31,241] INFO  {NsCentralizedAvailablePeers} started with storage sever enabled 
[15:21:31,243] INFO  {NsImmutableManager} started with encoding parameters = 131072-3-10
[15:21:31,243] INFO  {NsConnectionFailureDetector} intitiated with delay=10000 msec and retries=2
[15:21:31,283] INFO  {NsWebApplication} initiated
[15:21:32,040] INFO  {NsCentralizedAvailablePeers} got announcement from introducer with peers [node1(0@localhost:12346)]
[15:21:32,040] INFO  {NsCentralizedAvailablePeers} Current List of Servers 
[15:21:32,040] INFO  {NsCentralizedAvailablePeers} =====================================================
[15:21:32,041] INFO  {NsCentralizedAvailablePeers} 	node1(0@localhost:12346)
[15:21:32,041] INFO  {NsCentralizedAvailablePeers} =====================================================
```
> after starting node1 you can interact with the node either using the web browser at address http://localhost:8082 or using command line "**nilestore put** and **nilestore get**".

> ![http://nilestore.googlecode.com/svn/wiki/img/clientWebScreenShot.png](http://nilestore.googlecode.com/svn/wiki/img/clientWebScreenShot.png)

> following the previous steps we could create node2, but we have to change the configuration file as follows:
```
[node]
ip=127.0.0.1
networkport=12347
nickname=node2
webport=8083
```

> Note that the previous changes of the port and the webport is only needed now because we are running a local processes but in case of actual deployment it isn't required to do so.

> let's start node2
```
>>nilestore start -d node2
[15:31:49,334] INFO  {NsPeerMain} initiating minaNetwork with Address=0@localhost:12347,compressionLevel=9
[15:31:49,471] INFO  {NsPeerMain} peer initiated @ 0@localhost:12347
[15:31:49,500] INFO  {NsWebServer} initiated @ http://localhost:8083
[15:31:49,500] INFO  {NsPeer} node2 Started
[15:31:49,695] INFO  {NsStorageServer} initiated with homeDir node2/
[15:31:49,712] INFO  {NsCentralizedAvailablePeers} started with storage sever enabled 
[15:31:49,716] INFO  {NsWebApplication} initiated
[15:31:49,717] INFO  {NsConnectionFailureDetector} intitiated with delay=10000 msec and retries=2
[15:31:49,719] INFO  {NsImmutableManager} started with encoding parameters = 131072-3-10
[15:31:49,975] INFO  {NsCentralizedAvailablePeers} got announcement from introducer with peers [node1(0@localhost:12346),node2(0@localhost:12347)]
[15:31:49,975] INFO  {NsCentralizedAvailablePeers} Current List of Servers 
[15:31:49,975] INFO  {NsCentralizedAvailablePeers} =====================================================
[15:31:49,976] INFO  {NsCentralizedAvailablePeers} 	node1(0@localhost:12346)
[15:31:49,976] INFO  {NsCentralizedAvailablePeers} 	node2(0@localhost:12347)
[15:31:49,976] INFO  {NsCentralizedAvailablePeers} =====================================================

```

**Notes:**
  1. for now **nilestore start** command will run on the same process it will not create a process in the background so if you interrupted the execution using CTRL-C it will end that process, so as a temporary solution you can use the **&** for Linux Users as follows:
```
>>nilestore start -d introducernode &
[1] 15981
```
> > `15981` is the process id.
  1. web interface is only tested on google chrome