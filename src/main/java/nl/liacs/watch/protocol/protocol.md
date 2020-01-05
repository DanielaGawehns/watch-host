---
title: Watch communication protocol v1-alpha4
author:
 - Lieuwe Rooijakkers
 - Peter Bosch
---

# Watch-host discovery

When a watch wants to find the hosts on the network, it sends a UDP broadcast to the local network (255.255.255.255) on port 2112.
The payload of this broadcast MUST be `HelloWorld!`.

On receiving this message, the host MUST send an UDP unicast message to the sender of the broadcast on port 2113 with the contents `WatchSrvrPing`.

Now the watch can connect as described in the following section.

# Connection procedure

TODO: we should also be talking about SSL.

Host MUST open a TCP server on a fixed port (2114).
The watch can connect to the host at any moment.

After the socket is successfully opened, the watch MUST send the version number
as an 8 bit unsigned number.
If the version is unknown to the host, the host SHOULD close the connection.
Otherwise, if the version number is known, the host MUST continue to
communicate according to that protocol version.

After the host has accepted the version number, it MUST send an ACK ($06_{16}$).

Now the watch and host are free to communicate in any order, per the rest of
this document.

**Note**: A host is not required to support older version numbers, they are
free to do so. But it's up to the implementator to choose the version range to
support.

# Data specification

All data is sent in _big-endian_ order (_network byte order_).

## Universal message structure
| message reference ID | command ID | number of parameters | parameters... |
|:---|:---|:---|:---|
|unsigned 16 bit|unsigned 8 bit|unsigned 8 bit||

The reference ID is used for `REPLY` messages containing the reply to the sent message.
The ID can be any not already used integer as chosen by the sender of the message.
A reply is _only_ sent if and only if the ID is non-zero and a reply is specified for the message (i.e. the reply section in the specification is not "N/A").

Note: _not already used_ means not used by the sender of the message.
It's totally allowed for both parties to sent a message with the same ID and they should be treated as separate entities.

### Parameter
| length of value in bytes | value |
|:---|:---|
|unsigned 16 bit||

#### Value types
Specified by every command, possible are (short name given in the parentheses):

- Binary data string (`bytes`);
- **Non**-null terminated ASCII string (`str`);
- 32-bit signed integer (`i32`);
- Double precision IEEE float (`f64`).

## Message types
There are several message types, some bidirectional but most unidirectional.

Structure examples are in ASCII, the conversion to the real binary format should be trivial.
The command ID is stated after every command name.

### Bidirectional

#### PING (0)
This message can be send by either side in a self chosen interval.
The other side should respond within a maximum set amount of missed PINGs.
Generally this is set to 3, so the other side can miss two PINGs and only then respond to reset the miss counter.

Structure:
`PING`

Reply:
An unspecified but valid `REPLY` message.

#### REPLY (1)
Message representing the result (or error) of an operation.

The reference ID must be equal to the reference ID of the message that this
message is a reply to.
Note that replies cannot be sent on messages bearing a reference ID of 0.

The status code MUST be 0 for succes and any other non-zero number for failure.

The next is a status string, for an error this SHOULD be given.
For succes the status string MAY be an empty string.

The rest of the parameters are zero or more results of the operation.

Structure:
`REPLY <status code (i32)> <status string (str)> <results... (bytes)>`

Reply:
N/A

#### GET_VALUES (2)
Get the values with the given key from the other side.

The command MUST have an ID.
If it does not, the other side MAY ignore the message.

See the [key-value store section](#keyval) for more information.

Structure:
`GET_VALUES <key (str)>`

Reply:
`REPLY` message with the values in the store.
If the key does not exist, return an error.

#### SET_VALUES (3)
Set the values with the given key and value on the other side.

The command MUST have an ID.
If it does not, the other side MAY ignore the message.

See the [key-value store section](#keyval) for more information.

Structure:
`SET_VALUES <key (str)> <values... (bytes)>`

Reply:
`REPLY` message with the set array of values in the store.
If the key/values is not valid, return an error.

### Watch to host

#### INCREMENT (4)
An aggregated update to the live view on the host.
Should be send every `live.interval`, if any data is available.

The time delta is since the previous sent INCREMENT message in milliseconds.

Structure:
`INCREMENT <sensor (str)> <time delta (i32)> <data... (f64)>`

Reply:
N/A

#### PLAYBACK (5)
A data point for the full recorded session.

The time delta is since the start of the recording session in milliseconds.

Structure:
`PLAYBACK <sensor (str)> <time delta (f64)> <data... (f64)>`

Reply:
N/A

## Key-value store {#keyval}
The key value store is defined as a simple store containing pairs of the form:
(ASCII string, [binary data string]).

Although it doesn't require a structure to work, the protocol defines a little
structure that must be implemented.

Every value must be part of a namespace, root values are not permitted.

### Watch store
The namespace that must be implemented for the watch are the following:

- `system`: containing system variables.
- `sensor`: containing sensor settings.
- `live`: containing settings about the aggregated updates for the host's live dashboard.

#### `system` namespace
The system namespace contains variables about the watch.
Implemented must be:

- `system.uid`: The unique watch ID (`str`).

#### `sensor` namespace
The system namespace contains variables about the sensors on the watch.
Implemented must be:

- `sensor.list`: A list of every sensor name (one or more ASCII strings).

#### `live` namespace
The live namespace contains settings about the aggregated updates for the host's live dashboard.
Implemented must be:

- `live.interval`: The interval of the live sensor updates (`f64`).

### Host store
The namespace that must be implemented for the host are the following:

TODO

## Example
Accelerometer live view update, 100ms after the previous one.
Message reference ID is set to 0.
Values are in order of x,y,z acceleration.

In ASCII:
`INCREMENT accel 100 5.43894 3.47392 1.32419`

In binary:
`00 00 04 05 00 05 61 63 63 65 6c 00 04 00 00 00 64 00 08 40 15 c1 79 7c c3 9f fd 00 08 40 0b ca 96 91 a7 5c d1 00 08 3f f5 2f e1 da 7b 0b 39`
