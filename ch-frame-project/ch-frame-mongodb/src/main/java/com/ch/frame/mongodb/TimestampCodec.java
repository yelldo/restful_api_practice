package com.ch.frame.mongodb;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.sql.Timestamp;

public class TimestampCodec implements Codec<Timestamp> {

	@Override
	public void encode(BsonWriter arg0, Timestamp t, EncoderContext arg2) {
		if(t != null)
			arg0.writeDateTime(t.getTime());
	}

	@Override
	public Class<Timestamp> getEncoderClass() {
		return Timestamp.class;
	}

	@Override
	public Timestamp decode(BsonReader arg0, DecoderContext arg1) {
		return new Timestamp(arg0.readDateTime());
	}

}
