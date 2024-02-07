package fr.catcore.server.translations.api.polyfill;

public enum ResourcePackActivationType {
	NORMAL,
	DEFAULT_ENABLED,
	ALWAYS_ENABLED;

	public boolean isEnabledByDefault() {
		return this == DEFAULT_ENABLED || this == ALWAYS_ENABLED;
	}
}
