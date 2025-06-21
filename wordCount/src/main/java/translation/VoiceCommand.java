package translation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoiceCommand {

	private String id;
	private String language;
	private byte[] audioData;
	private String audioCodec;
}
