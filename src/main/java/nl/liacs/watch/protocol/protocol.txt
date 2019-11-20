# Watch communication protocol v1

# Watch-host discovery

When a watch wants to find the hosts on the network, it sends a UDP broadcast to the local network (255.255.255.255) on port 2112.
The payload of this broadcast MUST be "HelloWorld!".

On receiving this message, the host MUST send an UDP unicast message to the sender of the broadcast on port 2113 with the contents "WatchSrvrPing".

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

After the host has accepted the version number, it MUST send an ACK ($00000110_2$).

Now the watch and host are free to communicate in any order, per the rest of
this document.

**Note**: A host is not required to support older version numbers, they are
free to do so. But it's up to the implementator to choose the version range to
support.

## Universal message structure
| command ID | number of parameters | parameters... |
|:---|:---|:---|
|unsigned 8 bit|unsigned 8 bit||

### Parameter
| length of value in bytes | value |
|:---|:---|
|unsigned 16 bit||

#### Value types
Specified by every command, possible are:

- Binary data string;
- **Non**-null terminated ASCII string;
- Double precision IEEE float.

## Message types
There are several message types, some bidirectional but most unidirectional.

Structure examples are in ASCII, the conversion to the real binary format should be trivial.
The command ID is stated after every command name.

### Bidirectional

#### PING (0)
This message can be send by either side in a self chosen interval.
The other side should respond with a PONG within a maximum set amount of missed PINGs.
Generally this is set to 3, so the other side can miss two PINGs and only then respond with a PONG to reset the miss counter.

Structure:
`PING`

#### PONG (1)
The message to reply with a PING, should be sent in response to a PING.

Structure:
`PONG`

### Watch to host

#### INCREMENT (2)
An update to the live view on the host.
Should be send every LIVE_INTERVAL.

The time delta is since the previous sent INCREMENT message in milliseconds.

Structure:
`INCREMENT <sensor (ASCII string)> <time delta (double)> <data... (double)>`

#### PLAYBACK (3)
A data point for the full recorded session.

The time delta is since the start of the recording session in milliseconds.

Structure:
`PLAYBACK <sensor (ASCII string)> <time delta (double)> <data... (double)>`

### Host to watch

#### SENSOR_INTERVAL (4)
Set the interval for a given sensor in milliseconds.

Structure:
`SENSOR_INTERVAL <sensor (ASCII string)> <interval (double)>`

#### SENSOR_SETTING (5)
Set a specific sensor dependent setting for the watch.

Structure:
`SENSOR_SETTING <sensor (ASCII string)> <setting name (ASCII string)> <setting value (binary string)>`

##### Sensor settings
The settings available per sensor should be listed here.

#### LIVE_INTERVAL (6)
Set the interval in milliseconds for sending aggregated updates for the host's live dashboard.

Structure:
`LIVE_INTERVAL <interval (double)>`

## Example
Accelerometer live view update, 100ms after the previous one.
Values are in order of x,y,z acceleration.

In ASCII:
`INCREMENT 5 accel 100 5.43894 3.47392 1.32419`

In binary:
`02 05 00 05 61 63 63 65 6c 00 08 40 59 00 00 00 00 00 00 00 08 40 15 c1 79 7c c3 9f fd 00 08 40 0b ca 96 91 a7 5c d1 00 08 3f f5 2f e1 da 7b 0b 39`
