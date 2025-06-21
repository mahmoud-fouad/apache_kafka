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

	public VoiceCommandParserTopology(SpeechToTextService speechToTextService, VoiceTranslation translationService) {
		this.speechToTextService = speechToTextService;
		this.translationService = translationService;
	}
	
	public VoiceCommandParserTopology() {
		this.speechToTextService = new MockSttClient();
		this.translationService = new VoiceTranslationImpl();
	}

	public Topology buildTopology() {
		System.out.println("Building topology for voice command parser...");
		var streamsBuilder = new StreamsBuilder();

		// Define the topology
		var branchesMap = streamsBuilder
				.<String, VoiceCommand>stream(VoiceCommandParserTopology.VOICE_COMMANDS_TOPIC,
						Consumed.with(Serdes.String(), new JsonSerde<VoiceCommand>(VoiceCommand.class)))
				.mapValues((readOnlyKey, value) -> speechToTextService.parseVoiceCommand(value))
				.split(Named.as("branches-"))
				.branch((key, value) -> (value.getProbability()!=null && value.getProbability() < THRESHOLD), Branched.as("not-recognized"))
				.defaultBranch(Branched.as("recognized"));
		
		 branchesMap.get("branches-not-recognized")
         .to(VoiceCommandParserTopology.UNRECOGNIZED_COMMAND_TOPIC, Produced.with(Serdes.String(), new JsonSerde<ParsedVoiceCommand>(ParsedVoiceCommand.class)));
		 
		 var languages = branchesMap.get("branches-recognized")
	                .split(Named.as("language-"))
	                .branch((key, voiceCommand) -> voiceCommand.getLanguage().startsWith("en"), Branched.as("english"))
	                .defaultBranch(Branched.as("non-english"));


		languages.get("language-non-english").mapValues((key, value) -> {
			return translationService.translate(value);
		}).merge(languages.get("language-english"))

				.to(VoiceCommandParserTopology.RECOGNIZED_COMMANDS_TOPIC,
						Produced.with(Serdes.String(), new JsonSerde<ParsedVoiceCommand>(ParsedVoiceCommand.class)));

		return streamsBuilder.build();
	}

}
