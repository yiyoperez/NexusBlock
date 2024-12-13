package xhyrom.nexusblock.structures.nexus;

public enum NexusState {
    RESET, DISABLED, COOLDOWN, SCHEDULED, UNKNOWN;

    public static NexusState matchState(String input) {
        if (input == null || input.isBlank()) {
            return NexusState.UNKNOWN;
        }

        // Normalize input for comparison
        String normalizedInput = input.trim().toLowerCase();

        // Check for exact match
        for (NexusState state : NexusState.values()) {
            if (state.name().equalsIgnoreCase(normalizedInput)) {
                return state;
            }
        }

        // Check for similar match
        for (NexusState state : NexusState.values()) {
            if (isSimilar(normalizedInput, state.name().toLowerCase())) {
                return state;
            }
        }

        return NexusState.UNKNOWN;
    }

    private static boolean isSimilar(String input, String known) {
        return known.contains(input) || input.contains(known);
    }

}
