package com.ch.frame.mongodb;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.sql.Date;

public class DateCodec implements Codec<Date> {

	@Override
	public void encode(BsonWriter arg0, Date t, EncoderContext arg2) {
		if(t != null)
			arg0.writeDateTime(t.getTime());
	}

	@Override
	public Class<Date> getEncoderClass() {
		return Date.class;
	}

	@Override
	public Date decode(BsonReader arg0, DecoderContext arg1) {
		return new Date(arg0.readDateTime());
	}

}
