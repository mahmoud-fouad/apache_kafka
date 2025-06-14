package translation;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;

public class VoiceCommandParserTopology {

	public static final double THRESHOLD = 0.85;
	public static final String VOICE_COMMANDS_TOPIC = "voice-commands";
	public static final String UNRECOGNIZED_COMMAND_TOPIC = "unrecognized-commands";
	public static final String RECOGNIZED_COMMANDS_TOPIC = "recognized-commands";
	private SpeechToTextService speechToTextService;
	private VoiceTranslation translationService;
	
	public VoiceCommandParserTopology(SpeechToTextService speechToTextService,VoiceTranslation translationService) {
		this.speechToTextService = speechToTextService;
		this.translationService = translationService;
	}
	
	
	public Topology buildTopology() {
		var streamsBuilder = new StreamsBuilder();

		// Define the topology
		var streamMap = streamsBuilder.<String,VoiceCommand>stream(VoiceCommandParserTopology.VOICE_COMMANDS_TOPIC,
						Consumed.with(Serdes.String(),new JsonSerde<VoiceCommand>(VoiceCommand.class)))
				.mapValues((readOnlyKey,value) -> speechToTextService.parseVoiceCommand(value))
				.split(Named.as("language-"))
				.branch((key,value) -> value.getLanguage().startsWith ("en"),
						Branched.as("english"))
				.defaultBranch(Branched.as("non-english"));
		
		streamMap.get("language-non-english").mapValues((key, value) -> {
				return translationService.translate(value);
		})
				.merge(streamMap.get("language-english"))
				
				.to(VoiceCommandParserTopology.RECOGNIZED_COMMANDS_TOPIC,
						Produced.with(Serdes.String(), new JsonSerde<ParseVoiceCommand>(ParseVoiceCommand.class)));


		return streamsBuilder.build();
	}

}
