package fr.catcore.server.translations.api.polyfill;

public class VersionParsingException extends InternalVersionParsingException {
	public VersionParsingException() {
		super();
	}

	public VersionParsingException(Throwable t) {
		super(t);
	}

	public VersionParsingException(String s) {
		super(s);
	}

	public VersionParsingException(String s, Throwable t) {
		super(s, t);
	}
}
