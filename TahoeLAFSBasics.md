# Tahoe-LAFS #



Tahoe-LAFS`[1]` is an open-source secure decentralized data store. It uses capabilities for access control,
cryptography for preserving confidentiality and integrity and erasure coding for preserving fault tolerance.
## Overview ##

Tahoe provides a mechanism to store files and directories on multiple machines to increase the chance
of getting data back in case of failures. It provides a provider-independent security where the storage
provider does not have access to the unencrypted data because all data is encrypted, erasure-coded, and
hashed before leaving the client's machine thus preserving condentiality, availability, and integrity of
the data.

Tahoe was designed following the Principle of Least Authority(POLA) `[2]`; Every program and every privileged user of the system should operate using the least amount of privilege necessary to complete
the job.

### System Overview ###

When a group of friends need to setup their own data store using their machines, Tahoe could be used for such a use case. To setup the grid, users need to understand different roles for Tahoe's node such
as introducer, client, storage roles as illustrated in [Tahoe Grid](#Tahoe_Grid.md), but as a start consider that the nodes
have been setup appropriately as shown in the next figure.

<img src='http://nilestore.googlecode.com/svn/wiki/img/friendsgrid.png' align='middle' title='Eight machines are connected together to construct a Tahoe grid; one introducer node, 6 client+storage nodes, and 1 client node' width='50%' />

Now whenever a user "for example Shady" wants to upload a file to the grid, the user node will start
processing and sending the file as illustrated in the next figure. The upload process will end by receiving a
capability uri of the form (URI:CHK:...:.:..) at the user node who uploaded the le. This uri could be
considered for now as the path of the le in the data store, in the next sections we are going to discuss the capabilities in greater details. Users should keep the capability uri to use it to access that file again.

<img src='http://nilestore.googlecode.com/svn/wiki/img/friendsupload.png' align='middle' title="Description of steps encountered in an uploading process, Shady uploads a file 'nature.png' into the grid" width='90%' />

All users who have the capability uri for "nature.png" can download that file, when a download process is initiated the system will start processing and downloading the file as illustrated in next figure.

<img src='http://nilestore.googlecode.com/svn/wiki/img/friendsdownload.png' align='middle' title="Description of steps encountered in a downloading process, Mahmoud downloads a file 'nature.png' from the grid" width='90%' />

## Access Control ##

Tahoe uses the Capability-based Access Control model to manage access to files and directories. Each file/directory in a Tahoe grid is identified by a capability which is a _short_ and _unique_ string that
designates the file/directory and gives the user the authority to perform a specific set of actions (reading or writing) on that file/directory. This access scheme is known as "capability as keys" or "cryptographic capabilities".

The elements of the access model are files and directories and the operations supported by the access model are sharing and revocation and are described as follows:

**File** could be either Immutable which is created once in the grid and read for multiple times or Mutable "slot" "container" which has a read and write access.
> Immutable file has two capabilities, the read-cap for reading the file and the verify-cap for checking the file and allows untrusted nodes to preform checking/repairing on the file without having the ability to read the content of the file.

> Mutable file has three capabilities, the read-only cap for retrieving last version of the file, the read-write cap for reading the file like read-only cap and also to write a new version of the file , and the verify cap. read-only cap could be derived from read-write cap and a verify cap could be derived from a read-only cap. This is called _diminishining a capability_.

**Directory** is a mutable file containing a list of entries, each entry defines a child by its name, metadata, read-cap and read-write-cap. directories have the property of transitive read-only which enable users who have read-write access to a directory to have a read-write access to its children, but users who have read-only access to a directory will have a read-only access to its children.

**Sharing** is done simply by revealing the capability of the shared file/directory, transitive read-only
property will limit the access to sub-directories according to the type of the revealed capability.

**Revocation** is done by deep-copying "recursively copying" of the shared folder to another location and reveal the cap of the new location to the authorized users only. The unauthorized users still will continue to have access to the old directory but cannot see the new changes.

## Architecture ##

Tahoe could be viewed as collection of three layers:
  1. Key-Value store : in which each key "capability" identifies a file/directory "value" on the grid. it provides PUT and GET operations.
```
key = PUT(data)
data = GET(key)
```
  1. Decentralized File System Layer : The key-value store provides the mapping from URI to data, decentralized file system layer turn this into a graph of directories and files where files are leaves. It needs to keep track of directory nodes to maintain a graph of files and directories.
  1. Application Layer : Application specic features that use the underlying layers for example Allmydata.com used it for a backup service: the application periodically copies files from the local disk onto the decentralized file system.

<img src='http://nilestore.googlecode.com/svn/wiki/img/architecture.png' align='middle' title="Tahoe's Architecture Layers" width='40%' />

### File Encoding ###

Whenever a user initiates a put operation of a file into the grid, the client will encrypt the file producing a ciphertext. the resulted ciphertext is broken up into multiple number of small segments to reduce memory footprint. after that the client will apply erasure coding on each segment producing (N) blocks where a subset (K) of these blocks is sufficient to recover the file. Each block is sent to a different server. the set of blocks on a given server constitutes a Share.

<img src='http://nilestore.googlecode.com/svn/wiki/img/fileencoding.png' align='middle' title='File Encoding' width='40%' />

Tahoe uses Merkle hash trees to defend against problems in _file validity_. there are different trees used in Tahoe as listed below :
  * **Blocks Hash Tree** which is computed over the blocks in the same share. it used to validate the blocks of the share. the root of this tree is used to identify that share "_block root hash_" and used to construct another higher level tree _Shares Hash Tree_.

  * **Shares Hash Tree** which is computed over the shares. if integrity check fails, the _Shares Hash Tree_ will allow the client to know which shares were corrupted so that it can reconstruct using other shares.Each share holds part of this tree which used to validate itself, for example in next Figure Share 1 needs hash number 4 and 2 to validate itself as Following:
```
h1 = Hash(share1 | h4)
myroot = Hash( h1 | h2)
```

> if myroot hash does not equal to the share root hash then this share is corrupted.

  * **Ciphertext Hash Tree** which is computed over the file segments to check the validity of the ciphertext and to ensure one-to-one mapping between the verify-cap and the file content i.e. to prevent cases where the initial creator of the immutable file generate some shares from a file and other shares from another file and combine both into the same set of _Shares Hash Tree_.


These hash trees are saved inside a small data structure called _URI Extension Block (UEB)_ which saved aside shares on servers. UEB also contains ciphertext hash, original file size, and the encoding parameters.

<img src='http://nilestore.googlecode.com/svn/wiki/img/hashtrees.png' align='middle' title='Example of Blocks Hash Tree and Shares Hash Tree for a file consists of 4 segments and N set to 4' />

### Capabilities ###

Each file or directory in the grid is identified by a URI includes a read/write/verify capability. Capability URI provides both identification and location properties which means holding a URI is sufficient for locating, validating and retrieving the data.

**Immutable file** uses a symmetric encryption key, and has read-cap and verify-cap.

| **Capability** |  **Format** | **description** |
|:---------------|:------------|:----------------|
| read-cap       | URI:CHK:(key):(hash):(needed-shares):(total-shares):(size) | **key** is the 16-byte AES encryption key encoded in Base32 encoding, <br /> **hash** is the SHA265d hash of the URI Extension Block, <br /> **needed-shares** is the number of shares needed to reconstruct the file, <br /> **total-shares** is the number of total-shares distributed in the grid for that file, <br /> **size** is the size of the file in bytes.|
| verify-cap     | URI:CHK-Verifier:(storageindex):(hash):(needed-shares):(total-shares):(size) | **storageindex** is the SHA265d hash of the encryption key truncated to 16-byte.|
| LIT-URI        | URI:LIT:(data) | LIT URIs are used for files smaller than 55 bytes in which the file could be stored in the URI itself.<br /> **data** is the file hashed using SHA265d and encoded in base32 encoding.|


<img src='http://nilestore.googlecode.com/svn/wiki/img/immutablecaps.png' align='middle' title='Immutable File Capabilities' width='70%' />


**Mutable file** uses RSA public key technology and it has write-cap, read-cap and verify-cap.

| **Capability** |  **Format** | **description** |
|:---------------|:------------|:----------------|
| write-cap      | URI:SSK:(writekey):(fingerprint)} | **writekey** is 16-byte AES encryption key encoded in Base32 encoding,<br /> writekey is generated by the SHA256d hashing of the RSA private key truncated to 16-byte. <br /> writekey is used to encrypt the RSA private key.<br /> **fingerprint** is 32-byte SHA256d hash of the RSA public key encoded in Base32 encoding.|
|read-cap        | URI:SSK-RO:(readkey):(fingerprint) | **readkey** is 16-byte AES encryption key encoded in Base32 encoding, is derived by SHA256d hashing of the writekey.|
| verify-cap     | URI:SSK-Verifier:(storageindex):(fingerprint) |    **storageindex** is the SHA265d hash of the readkey truncated to 16-byte.|


<img src='http://nilestore.googlecode.com/svn/wiki/img/mutablecaps.png' align='middle' title='Mutable File Capabilities' />


**Directories (Dirnodes)** are used by the vdrive layer, dirnodes are contained inside mutable files and have a write-cap, read-cap and verify-cap.
```
write-cap format:
URI:DIR2:(writekey):(fingerprint)
read-cap format:
URI:DIR2-RO:(readkey):(fingerprint)
verify-cap format:
URI:DIR2-Verifier:(storageindex):(fingerprint)
```

### Tahoe Grid ###

There are three main roles for a Tahoe node;

#### Introducer Node ####
It is the entry point for new nodes to discover existing nodes in the grid in order to communicate with. it follows the **publish/subscribe** pattern where every node upon start will subscribe to storage service on the introducer, if the node has a storage server inside then it will publish its storage service to the introducer, and the introducer will announces all the subscribed nodes with the newly published nodes. Introducer is a Single Point of Failure **SPOF** but there are current work on Tahoe to implement multiple introducers to avoid this problem.

<img src='http://nilestore.googlecode.com/svn/wiki/img/pubsub.png' align='middle' title='Public/Subscribe Pattern' width='300' height='300' />

#### Client ####
It is the gateway to put and get data remotely in Tahoe grid. each Client node has an **IntroducerClient** to communicate with the **IntroducerServer** to get information about existing nodes and newly added nodes.

#### Storage Server ####
It is a client which publish storage service. a storage server could be viewed as a dictionary where key is the **storageIndex** and value is the data itself. Every Storage Server announces its available space when it is connected to the introducer.

### Implementation ###
Tahoe is an open source project implemented in python and use a set of open source libraries :
  * **Twisted :** is used for event-loop and control-flow management, application support, daemonization, web services, and other purposes.
  * **Foolscap :** is used for inter-node communications.
  * **Nevow :** is used for HTML templating on the web interface.
  * **pycryptopp :** is used for data encryption and hashing (pycryptopp is python binding for the Crypto++ library).
  * **zfec :** is used for erasure coding (zfec is a python-wrapper around the Rizzo FEC library).


---

`[1]` Z. Wilcox-O'Hearn and B. Warner, "Tahoe: the least-authority filesystem," in Proceedings of the 4th ACM international workshop on Storage security and survivability, StorageSS '08, (New York, NY, USA), pp. 21{26, ACM, 2008.

`[2]` M. S. Miller, Robust Composition: Towards a Unied Approach to Access Control and Concurrency
Control. PhD thesis, Johns Hopkins University, Baltimore, Maryland, USA, May 2006.

`[3]` "Tahoe lafs documentation." http://tahoe-lafs.org/trac/tahoe-lafs/wiki/Doc.