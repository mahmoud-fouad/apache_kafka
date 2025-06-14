package translation;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

public class JsonSerde<T> implements Serde<T> {
	
	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private Class<T> type;
	
	public JsonSerde(Class<T> type) {
		this.type = type;
	}

	@Override
	public Serializer<T> serializer() {
		// TODO Auto-generated method stub
		return (topic , data) -> serialize(data);
	}

	@SneakyThrows
	private byte[] serialize(T data) {
		// TODO Auto-generated method stub
		return OBJECT_MAPPER.writeValueAsBytes(data);
	}

	@Override
	public Deserializer<T> deserializer() {
		// TODO Auto-generated method stub
		return (topic , data) -> deserialize(data);
	}

	@SneakyThrows
	private T deserialize(byte[] data) {
		// TODO Auto-generated method stub
		return OBJECT_MAPPER.readValue(data, type);
	}

}
