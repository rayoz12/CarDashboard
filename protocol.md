# Bluetooth Sharing Protocol

## Shareable actions:
- Spotify (Music), all params should be in spotify uri's, action value is a spotify URI
  - Share a track, album or playlist to be played instantly
  - Add a song to queue 
- Location (to be opened in Waze or Maps), action value is the geo intent string
- Youtube Link, action value is the url
- Web page, action value is the url

## Serial Protocol:
### Symbols

A message is a single line with columns delimited by commas

|Symbol|Description|
---|---
|`#`|Signifies the start of the message|
|`\n` (newline)|Signifies the end of the message|
|`,` (comma)|field delimiter|
|`<action>`|One of the actions above|
|`<message>`|The actual message. It is base 64 encoded however so we avoid delimiter collision|

### Format 
`#<action>,<message>\n`

### Examples
These examples contain the message without base64 encoding

|Action|Example|Description|
---|---|---
|Share a track|#,spotify-play,spotify:track:6rqhFgbbKwnb9MLmUQDhG6,\n|
|Share an Album|#,spotify-play,spotify:album:4m2880jivSbbyEGAKfITCa,\n|
|Share a playlist|#,spotify-play,spotify:playlist:2q17dd27EffL3oiaicTRYS,\n|
|Add a song to queue|#,spotify-add,spotify:track:6rqhFgbbKwnb9MLmUQDhG6,\n|
|Location (to be opened in Waze or Maps)|#,location,geo:-33.746887,150.828035?q=19%20Tabitha%20Pl,%20Plumpton%20NSW%202761,\n| The q parameter is a friendly name for the location and must be URI encoded
|Youtube Link|#,youtube,https://www.youtube.com/watch?v=EwTZ2xpQwpA,\n|
|Web page|#,url,https://news.ycombinator.com/news,\n|


Location Example Encoded:
#,location,Z2VvOi0zMy43NDY4ODcsMTUwLjgyODAzNT9xPTE5JTIwVGFiaXRoYSUyMFBsLCUyMFBsdW1wdG9uJTIwTlNXJTIwMjc2MQ==,\n
