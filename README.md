# Kafka UDP Streamer

Things to try now:
- publish audio to the kafka topic (any special encoding needed?) format? wav for now since mp3 can't be easily split?
- Clicking play starts consuming from a topic and plays audio
- storage or caching of the audio
- artifacts, UI client (the consumer) vs the producer UI

Things to try later:
- chunk the messages into small (1k?) audio messages and streame audio over Kafka topic (but with UDP setting)
- performance testing, where the real fun begins. Is using Kafka too much plumbing to be fast enough? Is it an expensive means of transport?

Other Notes:
AudioInputStream.get
Apparently, you can clip parts of an audio (or otherwise inputstream) stream.
Example: `new AudioInputStream(clip, clip.getFormat, length )` - gets you a clip, serialize(if need be?) and send via kafka?
Then reassemble on the other side

The consumer on the other side gets the message(audio) and starts doing what?
- Maybe appending to another file that's currently streaming?
- how can we read this stream? is there an open format for reading a stream?
- what format does that need to be in?
- It seems like the AudioInputStream approach above is just passing around binary stream chunks and wouldn't care about it's format.

Example of a JavaFX media player pointed at a directory https://gist.github.com/jewelsea/5241462
