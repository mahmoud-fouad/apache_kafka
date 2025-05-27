package com.mfouad.kafkaprod.partions;

import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.InvalidRecordException;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;

public class FloorPartioner implements Partitioner {

	@Override
	public void configure(Map<String, ?> configs) {
		
	}

	@Override
	public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
		
		List<PartitionInfo> floorPartions = cluster.partitionsForTopic(topic);
		int floorsNum = floorPartions.size();
		if(keyBytes == null || (!(key instanceof String)))
			throw new InvalidRecordException("We expect all messages " +
					"to have floor number as key");
		if(((String) key).equals("thirdFloor"))
			return floorsNum-1;
		
		return Math.abs(Utils.murmur2(keyBytes)) % (floorsNum - 1);
	}

	@Override
	public void close() {
		
	}

}
