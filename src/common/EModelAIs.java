package common;

public enum EModelAIs {
    GEMINI_2_5_FLASH_PREVIEW("gemini-2.5-flash-preview-05-20"),
    GEMINI_2_5_PRO_PREVIEW("gemini-2.5-pro-preview-05-06"),
    GEMINI_2_0_FLASH("gemini-2.0-flash"),
    GEMINI_2_0_FLASH_LITE("gemini-2.0-flash-lite"),
    GEMINI_1_5_PRO("gemini-1.5-pro"),
    GEMINI_1_5_FLASH("gemini-1.5-flash");

    private final String modelName;

    private EModelAIs(String modelName) {
        this.modelName = modelName;
    }

    public String getModelName() {
        return modelName;
    }
}
