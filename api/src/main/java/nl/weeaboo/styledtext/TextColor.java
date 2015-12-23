package nl.weeaboo.styledtext;

final class TextColor {

    private TextColor() {
    }

    public static int packColorFloat(float r, float g, float b, float a) {
        int ri = Math.max(0, Math.min(255, Math.round(255 * r)));
        int gi = Math.max(0, Math.min(255, Math.round(255 * g)));
        int bi = Math.max(0, Math.min(255, Math.round(255 * b)));
        int ai = Math.max(0, Math.min(255, Math.round(255 * a)));
        return (ai << 24) | (ri << 16) | (gi << 8) | (bi);
    }

    public static int packColorInt(int r, int g, int b, int a) {
        int ri = Math.max(0, Math.min(255, r));
        int gi = Math.max(0, Math.min(255, g));
        int bi = Math.max(0, Math.min(255, b));
        int ai = Math.max(0, Math.min(255, a));
        return (ai << 24) | (ri << 16) | (gi << 8) | (bi);
    }

}
