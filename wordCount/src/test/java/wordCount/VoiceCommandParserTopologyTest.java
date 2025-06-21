package wordCount;

import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import translation.*;


@ExtendWith(MockitoExtension.class)
public class VoiceCommandParserTopologyTest {
	
	private TopologyTestDriver topologyTestDriver;
	private TestInputTopic<String, VoiceCommand> voideCommandInputTopic;
	private TestOutputTopic<String, ParsedVoiceCommand> recognizedVoiceOutputTopic;
	private TestOutputTopic<String, ParsedVoiceCommand> unRecognizedVoiceOutputTopic;

	@Mock
	SpeechToTextService speachToTextService;
	
	@Mock
	VoiceTranslation translationService;

	@InjectMocks
	VoiceCommandParserTopology topology;
	
	@BeforeEach
	void setUp() {
		// Initialize the TopologyTestDriver with the topology
		var voiceCommandJsonSerde = new JsonSerde<>(VoiceCommand.class);
		var parseVoiceCommandJsonSerde = new JsonSerde<>(ParsedVoiceCommand.class);
		var props=new Properties();
		props.put("bootstrap.servers", "dummy:1234");
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
		topologyTestDriver = new TopologyTestDriver(topology.buildTopology(),props);
		voideCommandInputTopic=topologyTestDriver.createInputTopic(
				VoiceCommandParserTopology.VOICE_COMMANDS_TOPIC,
				Serdes.String().serializer()
				, voiceCommandJsonSerde.serializer());
		
		
		recognizedVoiceOutputTopic=topologyTestDriver.createOutputTopic(VoiceCommandParserTopology.RECOGNIZED_COMMANDS_TOPIC, Serdes.String().deserializer(),
				parseVoiceCommandJsonSerde.deserializer()
		);
		
//		unRecognizedVoiceOutputTopic=topologyTestDriver.createOutputTopic(VoiceCommandParserTopology.UNRECOGNIZED_COMMAND_TOPIC, Serdes.String().deserializer(),
//				parseVoiceCommandJsonSerde.deserializer()
//				);
	}

	@Test
	void testEnglishVoiceCommand() {
		
		byte[] voiceCommandJson = new byte[20];
		new Random().nextBytes(voiceCommandJson);
		var command =VoiceCommand.builder().id(UUID.randomUUID().toString())
		.language("en-US")
		.audioData(voiceCommandJson)
		.audioCodec("FLAC")
		.build();

		given(speachToTextService.parseVoiceCommand(command))
				.willReturn(ParsedVoiceCommand.builder().id(command.getId())
						.language("en-US")
						.text("call john").build());

		voideCommandInputTopic.pipeInput(command);
		
		var parsedVoiceCommand = recognizedVoiceOutputTopic.readRecord().value();
		
		assertEquals(command.getId(), parsedVoiceCommand.getId());
		assertEquals("call john", parsedVoiceCommand.getText());
		
		
	}
	
	@Test
	void testSpanichVoiceCommand() throws Exception {
		
		byte[] voiceCommandJson = new byte[20];
		new Random().nextBytes(voiceCommandJson);
		var command =VoiceCommand.builder().id(UUID.randomUUID().toString())
				.language("es-ES")
				.audioData(voiceCommandJson)
				.audioCodec("FLAC")
				.build();
		
		ParsedVoiceCommand englishValue = ParsedVoiceCommand.builder().id(command.getId()).language("en-US").probability(0.9)
				.text("call john").build();
		ParsedVoiceCommand spanishValue = ParsedVoiceCommand.builder().id(command.getId()).text("llamar a john").language("es-ES")
				.probability(0.9)
				.build();
		
		given(speachToTextService.parseVoiceCommand(command))
		.willReturn(spanishValue);
		
		given(translationService.translate(spanishValue))
				.willReturn(englishValue);
		
		voideCommandInputTopic.pipeInput(command);
		
		var parsedVoiceCommand = recognizedVoiceOutputTopic.readRecord().value();
		
		assertEquals(command.getId(), parsedVoiceCommand.getId());
		assertEquals("call john", parsedVoiceCommand.getText());
		
		
	}



	@Test
	void testSpanichVoiceCommandWithTransaltionPropbability() throws Exception {

		byte[] voiceCommandJson = new byte[20];
		new Random().nextBytes(voiceCommandJson);
		var command =VoiceCommand.builder().id(UUID.randomUUID().toString())
				.language("es-ES")
				.audioData(voiceCommandJson)
				.audioCodec("FLAC")
				.build();

		ParsedVoiceCommand englishValue = ParsedVoiceCommand.builder().id(command.getId()).language("en-US").probability(0.9)
				.text("call john").build();
		ParsedVoiceCommand spanishValue = ParsedVoiceCommand.builder().id(command.getId()).text("llamar a john").language("es-ES")
				.probability(0.9)
				.build();

		given(speachToTextService.parseVoiceCommand(command))
				.willReturn(spanishValue);

		given(translationService.translate(spanishValue))
				.willReturn(englishValue);

		voideCommandInputTopic.pipeInput(command);

		var parsedVoiceCommand = recognizedVoiceOutputTopic.readRecord().value();

		assertEquals(command.getId(), parsedVoiceCommand.getId());
		assertEquals("call john", parsedVoiceCommand.getText());
		
		verify(translationService).translate(any(ParsedVoiceCommand.class));


	}
	@Test
	void testVoiceCommandWithoutTransaltionPropbability() throws Exception {
		
		byte[] voiceCommandJson = new byte[20];
		new Random().nextBytes(voiceCommandJson);
		var command =VoiceCommand.builder().id(UUID.randomUUID().toString())
				.language("es-ES")
				.audioData(voiceCommandJson)
				.audioCodec("FLAC")
				.build();
		
		ParsedVoiceCommand englishValue = ParsedVoiceCommand.builder().id(command.getId()).language("en-US").probability(0.5)
				.text("call john").build();
		
		given(speachToTextService.parseVoiceCommand(command))
		.willReturn(englishValue);
		
		
		voideCommandInputTopic.pipeInput(command);
		
		 assertTrue(recognizedVoiceOutputTopic.isEmpty());
		
		verify(translationService,never()).translate(any(ParsedVoiceCommand.class));
		
		
	}
	
}
