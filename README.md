# Kafka UDP Streamer

# Tech Thursday Experiment
You've landed here and might be thinking, WAT? This repo served as a 1 day hack event at work where we got to choose our fun. This app 
may not function and it not considered stable. It requires a running Kafka cluster and Mongo database.

# Topic/Producer configuration
Until we try splitting audio files into small chunks, it will be necessary to increase the `max.message.bytes` to something large 
enough to support some songs.
Example:
`kafka-topics --zookeeper local.docker:2181 --partitions 1 --replication-factor 1 --create --topic TT-AUDIO-2 --config max.message.bytes=8000000`
Then, in the producer you'll also need to increase the `max.request.size`:
`config.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 8000000);`

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
