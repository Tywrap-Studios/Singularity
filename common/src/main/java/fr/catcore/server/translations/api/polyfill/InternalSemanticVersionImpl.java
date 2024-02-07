package fr.catcore.server.translations.api.polyfill;

import java.util.Optional;

public class InternalSemanticVersionImpl implements SemanticVersion {
	private final SemanticVersion parent;

	protected InternalSemanticVersionImpl() {
		parent = null;
	}

	public InternalSemanticVersionImpl(String version, boolean storeX) throws VersionParsingException {
		parent = SemanticVersion.parse(version);
	}

	@Override
	public int getVersionComponentCount() {
		return parent.getVersionComponentCount();
	}

	@Override
	public int getVersionComponent(int pos) {
		return parent.getVersionComponent(pos);
	}

	@Override
	public Optional<String> getPrereleaseKey() {
		return parent.getPrereleaseKey();
	}

	@Override
	public Optional<String> getBuildKey() {
		return parent.getBuildKey();
	}

	@Override
	public String getFriendlyString() {
		return parent.getFriendlyString();
	}

	@Override
	public boolean equals(Object o) {
		return parent.equals(o);
	}

	@Override
	public int hashCode() {
		return parent.hashCode();
	}

	@Override
	public String toString() {
		return parent.toString();
	}

	@Override
	public boolean hasWildcard() {
		return parent.hasWildcard();
	}

	public boolean equalsComponentsExactly(SemanticVersionImpl other) {
		for (int i = 0; i < Math.max(getVersionComponentCount(), other.getVersionComponentCount()); i++) {
			if (getVersionComponent(i) != other.getVersionComponent(i)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int compareTo(Version o) {
		return parent.compareTo(o);
	}
}
