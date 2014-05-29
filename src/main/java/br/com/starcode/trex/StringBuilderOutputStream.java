package br.com.starcode.trex;

import java.io.IOException;
import java.io.OutputStream;

public class StringBuilderOutputStream extends OutputStream {

	private StringBuilder sb;

	public StringBuilderOutputStream(StringBuilder sb) {
		this.sb = sb;
	}

	@Override
	public void write(int b) throws IOException {
		sb.append((char) b);
	}

}
