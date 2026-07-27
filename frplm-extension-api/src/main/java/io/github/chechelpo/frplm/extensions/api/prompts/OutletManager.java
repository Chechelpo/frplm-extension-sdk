package io.github.chechelpo.frplm.extensions.api.prompts;

import io.github.chechelpo.frplm.extensions.api.standalone.LorebookSnapshot;

public interface OutletManager {
    enum OverrideResult{
        SUCCESS,
        TARGET_OUTLET_DOES_NOT_EXIST,
        TARGET_LOREBOOK_DOES_NOT_EXIST,
        ALREADY_OVERRIDDEN;
    }
    /**
     * @param targetLorebook only entries belonging to this lorebook will be targeted
     * @param targetOutlet only entries with this outlet will be targeted
     * @param newOutlet new outlet of the matching entries
     */
    OverrideResult overrideLorebookOutlet(LorebookSnapshot targetLorebook, String targetOutlet, String newOutlet);
    /**
     * @param targetLorebook only entries with this parent lorebook will be targeted
     * @param newOutletName new outlet for the matching entries
     * @apiNote this will apply lorebook-wide,
     * see {@link #overrideLorebookOutlet(LorebookSnapshot, String, String)} for targeting outlets + lorebooks
     */
    OverrideResult overrideAllLorebookOutlets(LorebookSnapshot targetLorebook, String newOutletName);
    /**
     * @param targetOutlet only entries with this outlet will be targeted
     * @param newOutlet new outlet for the matching entries
     * @apiNote target outlet must exist before you call this function
     */
    OverrideResult overrideOutlet(String targetOutlet, String newOutlet);
}
