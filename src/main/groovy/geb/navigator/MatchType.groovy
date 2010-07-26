package geb.navigator

enum MatchType {

	EXISTING({ String value, String valueToLookFor ->
		value
	}),
	EQUALS({ String value, String valueToLookFor ->
		value == valueToLookFor
	}),
	CONTAINED_WITH_WHITESPACE({ String value, String valueToLookFor ->
		value =~ /(^|\s)$valueToLookFor($|\s)/
	}),
	STARTING_WITH({ String value, String valueToLookFor ->
		value?.startsWith(valueToLookFor)
	}),
	ENDING_WITH({ String value, String valueToLookFor ->
		value?.endsWith(valueToLookFor)
	}),
	CONTAINING({ String value, String valueToLookFor ->
		value?.contains(valueToLookFor)
	}),
	CONTAINED_WITH_HYPHENS({ String value, String valueToLookFor ->
		value =~ /(^|-)$valueToLookFor($|-)/
	})

	private final rule

	private MatchType(rule) {
		this.rule = rule
	}

	boolean isMatch(String value, String valueToLookFor) {
		rule(value, valueToLookFor)
	}

}
