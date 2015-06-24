# Nilestore #




## Overview ##
Nilestore is built using [Kompics](http://kompics.sics.se) framework, following the same design of [Tahoe-LAFS](http://tahoe-lafs.org/).
In this document we are describing the design of Nilestore.

**Note:** to understand the following sections you have to read the [programming manual](http://kompics.sics.se/trac/attachment/wiki/WikiStart/kompics-tutorial.pdf) first.

### Kompics Overview ###
briefly Kompics is a message-passing and component model framework for building distributed systems, it was designed to simplify the development of complex distributed systems.

<a href='http://www.youtube.com/watch?feature=player_embedded&v=G_LCPkY87rs' target='_blank'><img src='http://img.youtube.com/vi/G_LCPkY87rs/0.jpg' width='425' height=344 /></a>

## Nilestore Nodes ##

There are three main node types in Nilestore; _Peer_, _Introducer_, _Monitor_, and _Bootstrap_.

### Peer ###

It is the main node in Nilestore, it provides the functionality of the distributed storage system by allowing users to  put/get files into/from the grid.

<img src='http://nilestore.googlecode.com/svn/wiki/img/peer.png' align='middle' title='Peer Node' width='320' height='240'>

<b>NsPeer</b> is the main component in Nilestore, it requires <i>Network</i>, <i>NetworkControl</i>, and <i>Timer</i> ports and provides <i>NilestorePeer</i> and <i>Web</i> ports as shown in the next Figure below. it encapsulates different functional components that we will discuss each in details in the following sections.<br>
<br>
<img src='http://nilestore.googlecode.com/svn/wiki/img/nspeer.png' align='middle' title='NsPeer Component'>

<h4>Available Peers</h4>

<b>NsAvailablePeers</b> is an abstraction for the underlying overlay, it holds information about other living peers in the grid. it requires <i>Timer</i>, <i>Network</i>, and <i>FailureDetector</i> ports and provides <i>AvailablePeers</i> and <i>APStatus</i> ports as shown in next figure.<br>
<br>
<img src='http://nilestore.googlecode.com/svn/wiki/img/nsavailablepeers.png' align='middle' title='NsAvailablePeers Component'>


In Nilestore, we have two different overlay implementation centralized and decentralized. note that the main components that changes according to overlay type are <b>NsShareFinder</b> and <b>NsPeerSelector</b> because both try to solve either where to put the shares? or where to find the shares?.<br>
<br>
<ul><li><b>Centralized Version</b> in this case each peer upon start announces its presence to an Introducer node as illustrated in <a href='http://code.google.com/p/nilestore/wiki/TahoeLAFSBasics#Introducer_Node'>Tahoe-LAFSBasics</a>.</li></ul>

<blockquote>when a <i>GetPeers(peerselectionIndex)</i> is triggered at the <i>AvailablePeers</i> port, NsAvailablePeers will permutes the list of servers by Hash(peerid+peerselectionIndex) then it will triggers back a <i>GetPeersResponse(permutedservers)</i>.</blockquote>

<ul><li><b>Decentralized Version<code>[1]</code></b> in this case each peer -only who have a storage server- contribute in a DHT Chord ring. As described in the next figure, decentralized version of <b>NsAvailablePeers</b> has a <b>Chord</b> component which holds the information about the Chord Ring that we are part of and <b>BootstrapClient</b> which communicates with the <b>BootstrapServer</b> which is responsible for initializing the ring.</li></ul>

<img src='http://nilestore.googlecode.com/svn/wiki/img/nsavailablepeers2.png' align='middle' title='NsAvailablePeers Component (Decentralized Version)' width='320' height='240' />

<blockquote>when a <i>GetPeers(peerselectionIndex)</i> is triggered at the <b>AvailablePeers</b> port, NsAvailablePeers will triggers a <i>Chordlookup</i> event at <b>ChordSO</b> then, after receiving <i>ChordlookupResponse</i> it will triggers back a <i>GetPeersResponse(peer)</i> where peer is the closest peer to <i>peerselectionIndex</i> in the ring.</blockquote>

<blockquote>In case where a peer does not have storage enabled so, NsAvailablePeers will not create a <b>Chord</b> component as specified before but instead it will communicate with the <b>Bootstrap Node</b> to get a random node located inside the ring then, it will communicate with that node directly using <i>Network</i> and will ask for assistance to discover nodes to be used either for peer selection or share finding.</blockquote>

<h4>Connection Failure Detector</h4>

<b>NsConnectionFailureDetector (CFD)</b> is responsible for detecting connection failures by keeping track of the network status "Session Open, Session Close" through <i>NetControl</i> port and by using <i>Timer</i> timeout events and <b>PingFailureDetector</b> component "Kompics component tweaked by adding number of tries instead of pinging forever".<br>
<br>
<img src='http://nilestore.googlecode.com/svn/wiki/img/cfd.png' align='middle' title='NsConnectionFailureDetector Component' width='400' height='200' />

<b>CFD</b> acts as a wrapper for <b>PingFailureDetector</b> component as it holds it inside to be used by the  the <b>CFD</b> itself and provides it to other components by providing the <i>FailureDetector</i> port.<br>
<br>
when  <i>NotifyonFailure</i> event is triggered at the <i>CFailureDetector</i> port, <b>CFD</b> will schedule a timer event with a specified delay, if the timer timeout reached before receiving a <i>CancelNotifyonFailure</i> from the requester <b>CFD</b> will trigger a <i>StartProbePeer(addr,retries)</i> which will ask the <b>PingFailureDetector</b> to conclude if "addr" is alive or not by pinging for "retries" times. if  a failure received then, a <i>ConnectionFailure</i> will be triggered on the requester otherwise the <b>CFD</b> will schedule another timer event and so on.<br>
<br>
A sequence diagram of <b>CFD</b> operation is described in figure below. note that for clarity the Network part is not included.<br>
<br>
<img src='http://nilestore.googlecode.com/svn/wiki/img/cfdseq.png' align='middle' title='Connection Failure Detector Sequence Diagram' width='600' height='600' />

<h4>Redundancy</h4>

<b>NsRedundancy</b> encapsulates the redundancy logic, it communicates with other components through <i>Redundancy</i> port which accepts <i>Encode</i>/<i>Decode</i> requests and delivers <i>EncodeResp</i>/<i>DecodeResp</i> responses. currently NsRedundancy implements the redundancy logic using the onion network coders "Reed Solomon Codes".<br>
<br>
<img src='http://nilestore.googlecode.com/svn/wiki/img/redundancy.png' align='middle' title='NsRedundancy Component' width='320' height='160' />

For the first time <i>Encode</i>/<i>Decode</i> event is triggered at the Redundancy port, the NsRedundancy will create an onion network PureCode<code>[2]</code> object according to the specified replication parameters and add that object to a map as shown in next table. after that, for all encode/decode events triggered again with the same parameters the NsRedundancy will use the existing coder instead of creating a new one.<br>
<br>
<table><thead><th> (3,10) </th><th> PureCode1</th></thead><tbody>
<tr><td> (12,40) </td><td> PureCode2</td></tr>
<tr><td> (10,20) </td><td> PureCode3</td></tr></tbody></table>


<h4>Storage Server</h4>

<b>NsStorageServer</b> component is responsible for dealing (read/write/inquiry) with share files on disk through the usage of <b>NsBucketWriter</b> and <b>NsBucketReader</b> components which allows remote<br>
read and write operations.<br>
<br>
NsStorageServer requires <i>Network</i> and <i>CFailureDetector</i> ports, it accepts the following events on the <i>Network</i> port:<br>
<ul><li><i>AllocateBuckets</i> event which holds information for share files to be allocated, and triggers back a <i>AllocateBucketsResponse</i> holding the addresses (if possible) of the created <b>NsBucketWriters</b> to communicate with them directly.<br>
</li><li><i>GetBuckets</i> event which asks about existing shares for a particular storage index, and triggers back a <i>GetBucketsResponse</i> holding the addresses of the created <b>NSBucketReaders</b> to communicate with them directly.<br>
</li><li><i>HaveBuckets</i> event which asks about existing shares for a particular storage index, and triggers back a <i>HaveBucketsResponse</i> holding a list of the existing share numbers.</li></ul>

NsStorageServer provides <i>SSStatus</i> port which accepts <i>SSStatusRequest</i> event and delivers <i>SSStatusResponse</i> event holding the current status of the storage server.<br>
<br>
<b>NsBucketReader</b> accepts <i>RemoteRead</i> and <i>Close</i> events at the <i>Network</i> port and delivers <i>RemoteReadResponse</i>, <b>NsBucketWriter</b> accepts <i>RemoteWrite</i> and <i>Close</i> events at the <i>Network</i> port and delivers <i>RemoteWriteResponse</i>.<br>
<br>
<br>
<img src='http://nilestore.googlecode.com/svn/wiki/img/storageserver.png' align='middle' title='NsStorageServer Component' width='320' height='320' />

<h4>Immutable Manager</h4>

<b>NsImmutableManager</b> is responsible for creating/destroying uploaders and downloaders upon request at its provided <i>Immutable</i> port, also it carry information about current uploaders/downloaders.<br>
<br>
<img src='http://nilestore.googlecode.com/svn/wiki/img/immmanager.png' align='middle' title='NsImmutableManager Component' width='640' height='480' />

<h5>Uploader</h5>

<b>NsUploader</b> represents a single upload operation and it is initiated with the file handle of the file to be uploaded.<br>
<br>
<img src='http://nilestore.googlecode.com/svn/wiki/img/immuploader.png' align='middle' title='NsUploader Component' width='400' height='280' />


A sequence diagram of the main actions done during the upload process is described in next figure. Note that every status message returned from the NsWriteBucketProxy is evaluated to check BucketWriter failures but we didn't add them to the sequence diagram to make it clear.<br>
<br>
<img src='http://nilestore.googlecode.com/svn/wiki/img/immuploaderseq.png' align='middle' title='Uploading Sequence diagram' width='800' height='600' />

<h6>Peer Selector</h6>

<b>NsPeerSelector</b> is responsible for selecting the appropriate peers for the uploading process. NsPeerSelector is an abstraction for the peer selection process and it has different implementations, in case of using the Introducer node "centralized" then a <i>Tahoe2PeerSelector</i> algorithm is used otherwise in case of using Chord "decentralized" then a <i>ChordPeerSelector<code>[1]</code></i> is used.<br>
<br>
<img src='http://nilestore.googlecode.com/svn/wiki/img/immpeerselector.png' align='middle' title='NsPeerSelector Component' width='320' height='240' />


Despite the used type of NsPeerSelector, a <b>NsPeerTracker</b> components are used where each NsPeerTracker is responsible for communicating with one storage server to ask about existing shares and/or to create new shares.<br>
<br>
<img src='http://nilestore.googlecode.com/svn/wiki/img/peertracker.png' align='middle' title='NsPeerTracker Component' width='320' height='160' />


In Nilestore, we have different implementations of PeerSelectors; Tahoe2PeerSelector and ChordPeerSelector<code>[1]</code> which used in case of Chord ring overlay . ChordPeerSelection algorithm is inspired by the proposed Tahoe3PeerSelector algorithm and it's a gossip like algorithm where we ask random peers in the ring to hold the task of allocating a share for us either on their storage server or by propagating the request to other peers and finally returning back with a peer that accepted to hold that share. this algorithm theoretically could fit very well in large networks. In a decentralized version the <i>NsShareFinder</i> will use almost the same algorithm but instead of allocating a share it will search for a share.<br>
<br>
<h6>Write Bucket Proxy</h6>

<b>NsWriteBucketProxy</b> is responsible for writing share data on a storage server by communicating with NsBucketWriter component in the storage server. At the end of peer selection process the uploader will create a set of NsWriteBucketProxy components to pass their ids to the NsEncoder for further use.<br>
<br>
<img src='http://nilestore.googlecode.com/svn/wiki/img/wbp.png' align='middle' title='NsWriteBucketProxy Component' width='400' height='240' />

<h6>Encoder</h6>

<b>NsEncoder</b> is responsible for the actual uploading process where the file is encrypted, segmented and then adding redundant data using NsRedundancy, then sent to servers through the NsWriteBucketProxies created by the parent uploader. At the end of the process the Encoder will trigger an <i>EncoderDone</i> event holding the verifyCap of the uploaded file.<br>
<br>
<img src='http://nilestore.googlecode.com/svn/wiki/img/encoder.png' align='middle' title='NsEncoder Component' width='320' height='160' />

<h5>Downloader</h5>

<b>NsDownloader</b> is responsible for a single download operation and it's initiated with the capability uri of the file to be downloaded. it holds the NsDownloadNode component which is responsible for the actual downloading process, the NsDownloader is just a wrapper for NsDownloadNode with a decryption key to decrypt validated segments gathered by the download node.<br>
<br>
<img src='http://nilestore.googlecode.com/svn/wiki/img/nsdownloader.png' align='middle' title='NsDownloader Component' />


<b>NsDownloadNode</b> holds  a <i>DownloadCommon</i> object which holds the uri extension block(<b>UEB</b>), share hash tree, ciphertext hash tree and other parameters required to validate the shares.<br>
<br>
<img src='http://nilestore.googlecode.com/svn/wiki/img/nsdownloadnode.png' align='middle' title='NsDownloadNode Component' />


Notice that in our current implementation we fetch the whole block hash tree and ciphertext hash tree which conflict with the use of merkle trees "validating random nodes on the tree" but it's implemented that way for simplicity we could improve that in later versions.<br>
<br>
check the sequence diagram of the download process in the next figure.<br>
<br>
<img src='http://nilestore.googlecode.com/svn/wiki/img/downloadseq.png' align='middle' title='Downloading Sequence diagram' width='800' height='1000' />

<h6>Share Finder</h6>

<b>NsShareFinder</b> is responsible for locating shares on storage servers, it uses set of NsPeerTracker components to communicate with storage servers. it follows almost the same design as the share finder in Tahoe.<br>
<br>
<img src='http://nilestore.googlecode.com/svn/wiki/img/immsharefinder.png' align='middle' title='NsShareFinder Component' width='320' height='240' />

<h6>Bucket Reader</h6>

<b>NsReadBucketProxy</b> is responsible for reading share data located on a storage server by communicating with NsBucketReader component in the storage server, also responsible for validating blocks data against their block hash tree. NsDownloader creates NsReadBucketProxy components every time it got <i>GotShares</i> event from the share finder, and passes their ids to the NsSegementFetcher.<br>
<br>
<img src='http://nilestore.googlecode.com/svn/wiki/img/rbp.png' align='middle' title='NsReadBucketProxy Component' width='400' height='240' />


After initiating a NsReadBucketProxy component it will request the header data of the share file it is responsible for and any requests triggered at <i>ReadBP</i> port before getting the header data will be postponed until getting the header data. NsDownloader will trigger <i>SetCommonParams</i> event on the <i>ReadBP</i> port of read bucket proxies who don't have the number of segments and the size of the tail block.<br>
<br>
When <i>GetBlock</i> is triggered the read bucket proxy will try to fetch the required hashes to verify that segment and will triggers back the ciphertext hashes in a <i>GotCiphertextHashes</i> event to the downloader which validates them according to the ciphertext hash tree in DownloadCommon.<br>
<br>
<h6>Segment Fetcher</h6>

<b>NsSegmentFetcher</b> is responsible for fetching segments upon request. NsDownloader will request segments from NsSegmentFetcher by triggering <i>GetSegment</i> on the <i>SegmentFetcher</i> port event. NsSegmentFetcher will triggers a <i>GetSegmentResponse</i> event when it got a segment. it follows almost the same design as segment fetcher in Tahoe but instead of creating a new segment fetcher for each segment we used it for all segments by tweaking the segment fetcher design.<br>
<br>
<img src='http://nilestore.googlecode.com/svn/wiki/img/segmentfetcher.png' align='middle' title='NsSegmentFetcher Component' width='280' height='160' />

<h4>Web Application</h4>

<b>NsWebApplication</b> is responsible for handling web requests triggered at its <i>Web</i> port and figure which operation to be done; for example if operation is upload a file then NsWebApplication will trigger a <i>Upload</i> event at the <i>Immutable</i> port.<br>
<br>
<img src='http://nilestore.googlecode.com/svn/wiki/img/webapp.png' align='middle' title='NsWebApplication Component' width='160' height='80' />


<h3>Introducer</h3>

It is the entry point for new peers to discover already existing peers. It follows <b>Publish/Subscribe</b> pattern.<br>
<br>
<img src='http://nilestore.googlecode.com/svn/wiki/img/introducer.png' align='middle' title='Introducer Node' width='320' height='240'>

Introducer node has a NsIntroducerServer component which accepts <i>Publish</i> and <i>Subscribe</i> events through the <i>Network</i> port and delivers <i>Announce</i> event as shown in Figure below.<br>
<br>
<img src='http://nilestore.googlecode.com/svn/wiki/img/nsintroducerserver.png' align='middle' title='Introducer Server' width='320' height='160'>

<h3>Monitor</h3>

It is a server that collect data from peers whom enabled the monitoring feature in the grid, each peer will have a <b>NsMonitorClient</b> which communicates with other components inside the peer to get status then it will send a status every some period of time according to configuration to <b>NsMonitorServer</b>.<br>
<br>
<img src='http://nilestore.googlecode.com/svn/wiki/img/monitor.png' align='middle' title='Monitor Node' width='320' height='240'>


For our current implementation <b>NsMonitorClient</b> has a <i>SSStatus</i> port which used to get status information about storage server on each peer, all these information is sent to the <b>NsMonitorServer</b> which display these info in a visual representation. for now we have two visual representation; <b>Storage Matrix</b> which plot storage indeces against storage servers and display the count of shares as an intensity, and <b>Storage Grouped View</b> which plots the amount of contribution "<i>Used Space</i> and <i>Count of Shares</i>" by a storage node to the whole network.<br>
<br>
<img src='http://nilestore.googlecode.com/svn/wiki/img/monitor1.png' align='middle' title='Example of Storage Matrix'>

<img src='http://nilestore.googlecode.com/svn/wiki/img/monitor2.png' align='middle' title='Example of Storage Grouped View'>

<h3>Simulator</h3>

Simulator is a node that creates a small-scale Nilestore grid in one java process, it has an interactive web interface to communicate with the grid as shown below. Kompics provides a <b>P2pOrchestrator</b> component which provides a <i>Network</i> and <i>Timer</i> ports and derives the execution from <i>Experiment</i> port.<br>
<br>
<img src='http://nilestore.googlecode.com/svn/wiki/img/simulator.png' align='middle' title='Simulator Node' width='320' height='240'>

<img src='http://nilestore.googlecode.com/svn/wiki/img/nssimulator.png' align='middle' title='NsSimulator Component' width='320' height='240'>

<img src='http://nilestore.googlecode.com/svn/wiki/img/simulatorinterface.png' align='middle' title='Simulator Web Interface'>


<h2>Project Structure</h2>
Nilestore uses <a href='http://maven.apache.org/'>Maven</a> for build and dependency management, project has the following structure:<br>
<br>
<pre><code>nilestore/<br>
|-- nilestore-availablepeers<br>
|   |-- nilestore-availablepeers-centralized<br>
|   |   |-- nilestore-availablepeers-centralized-cmp<br>
|   |   `-- nilestore-availablepeers-centralized-port<br>
|   |-- nilestore-availablepeers-decentralized<br>
|   `-- nilestore-availablepeers-port<br>
|-- nilestore-common<br>
|-- nilestore-connectionfd<br>
|   |-- nilestore-connectionfd-cmp<br>
|   `-- nilestore-connectionfd-port<br>
|-- nilestore-cryptography<br>
|-- nilestore-immutable<br>
|   |-- nilestore-immutable-abstract-updown<br>
|   |-- nilestore-immutable-common<br>
|   |-- nilestore-immutable-downloader<br>
|   |   |-- nilestore-immutable-downloader-cmp<br>
|   |   |-- nilestore-immutable-downloader-node<br>
|   |   |   |-- nilestore-immutable-downloader-node-cmp<br>
|   |   |   `-- nilestore-immutable-downloader-node-port<br>
|   |   |-- nilestore-immutable-downloader-port<br>
|   |   |-- nilestore-immutable-downloader-reader<br>
|   |   |   |-- nilestore-immutable-downloader-reader-cmp<br>
|   |   |   `-- nilestore-immutable-downloader-reader-port<br>
|   |   |-- nilestore-immutable-downloader-segfetcher<br>
|   |   |   |-- nilestore-immutable-downloader-segfetcher-cmp<br>
|   |   |   `-- nilestore-immutable-downloader-segfetcher-port<br>
|   |   `-- nilestore-immutable-downloader-sharefinder<br>
|   |       |-- nilestore-immutable-downloader-sharefinder-port<br>
|   |       `-- nilestore-immutable-downloader-sharefinder-tahoe<br>
|   |-- nilestore-immutable-file<br>
|   |-- nilestore-immutable-manager<br>
|   |   |-- nilestore-immutable-manager-cmp<br>
|   |   `-- nilestore-immutable-manager-port<br>
|   |-- nilestore-immutable-peertracker<br>
|   |   |-- nilestore-immutable-peertracker-cmp<br>
|   |   `-- nilestore-immutable-peertracker-port<br>
|   `-- nilestore-immutable-uploader<br>
|       |-- nilestore-immutable-uploader-cmp<br>
|       |-- nilestore-immutable-uploader-encoder<br>
|       |   |-- nilestore-immutable-uploader-encoder-cmp<br>
|       |   `-- nilestore-immutable-uploader-encoder-port<br>
|       |-- nilestore-immutable-uploader-peerselector<br>
|       |   |-- nilestore-immutable-uploader-peerselector-port<br>
|       |   |-- nilestore-immutable-uploader-peerselector-simple<br>
|       |   `-- nilestore-immutable-uploader-peerselector-tahoe2<br>
|       |-- nilestore-immutable-uploader-port<br>
|       `-- nilestore-immutable-uploader-writer<br>
|           |-- nilestore-immutable-uploader-writer-cmp<br>
|           `-- nilestore-immutable-uploader-writer-port<br>
|-- nilestore-interfaces<br>
|-- nilestore-introducer<br>
|   |-- nilestore-introducer-port<br>
|   `-- nilestore-introducer-server<br>
|-- nilestore-main<br>
|-- nilestore-monitor<br>
|   |-- nilestore-monitor-client<br>
|   |-- nilestore-monitor-port<br>
|   `-- nilestore-monitor-server<br>
|-- nilestore-peer<br>
|   |-- nilestore-peer-cmp<br>
|   |-- nilestore-peer-main<br>
|   `-- nilestore-peer-port<br>
|-- nilestore-redundancy<br>
|   |-- nilestore-redundancy-onion-fec<br>
|   `-- nilestore-redundancy-port<br>
|-- nilestore-simulator<br>
|-- nilestore-storage<br>
|   |-- nilestore-storage-common<br>
|   |-- nilestore-storage-immutable<br>
|   |   |-- nilestore-storage-immutable-reader<br>
|   |   |   |-- nilestore-storage-immutable-reader-cmp<br>
|   |   |   `-- nilestore-storage-immutable-reader-port<br>
|   |   |-- nilestore-storage-immutable-sharefile<br>
|   |   `-- nilestore-storage-immutable-writer<br>
|   |       |-- nilestore-storage-immutable-writer-cmp<br>
|   |       `-- nilestore-storage-immutable-writer-port<br>
|   |-- nilestore-storage-port<br>
|   `-- nilestore-storage-server<br>
|-- nilestore-uri<br>
|-- nilestore-utils<br>
|-- nilestore-web-sharedresources<br>
|-- nilestore-webapp<br>
`-- nilestore-webserver<br>
    |-- nilestore-webserver-jetty<br>
    |-- nilestore-webserver-port<br>
    `-- nilestore-webserver-servlets<br>
</code></pre>

<hr />
<code>[1]</code> This Feature is still in development<br>
<br>
<code>[2]</code> PureCode is OnionNetwork's Java implementation of ReedSolomon Codes