package fr.catcore.server.translations.api.polyfill;

public class InternalVersionParsingException extends Exception {
	public InternalVersionParsingException() {
		super();
	}

	public InternalVersionParsingException(Throwable t) {
		super(t);
	}

	public InternalVersionParsingException(String s) {
		super(s);
	}

	public InternalVersionParsingException(String s, Throwable t) {
		super(s, t);
	}
}
