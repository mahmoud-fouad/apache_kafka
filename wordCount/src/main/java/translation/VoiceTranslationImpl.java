package translation;

public class VoiceTranslationImpl implements VoiceTranslation{
	

	@Override
	public ParsedVoiceCommand translate(ParsedVoiceCommand command) {
		return  ParsedVoiceCommand.builder().id(command.getId()).language("en-US").probability(0.9)
				.text("call john").build();
		
	}

}
