package nl.weeaboo.gdx.test;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.nio.ByteBuffer;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.ScreenUtils;

import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.gdx.GdxFontUtil;
import nl.weeaboo.styledtext.gdx.TestFreeTypeFontStore;
import nl.weeaboo.styledtext.layout.ITextLayout;
import nl.weeaboo.styledtext.layout.LayoutParameters;
import nl.weeaboo.styledtext.layout.LayoutUtil;

public class GdxRenderTest {

    // Allow a small difference in color to account for rounding errors
    private static final int MAX_COLOR_DIFF = 2;

    protected boolean generate = false;
    protected SpriteBatch batch;
    protected TestFreeTypeFontStore fontStore;

    private final int pad = 4;

    private ShapeRenderer shapeRenderer;

    @Before
    public final void beforeRenderTest() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        fontStore = new TestFreeTypeFontStore();
    }

    @After
    public final void afterRenderTest() {
        batch.dispose();
        shapeRenderer.dispose();
    }

    protected void checkRenderResult(String filename, Rectangle r) {
        checkRenderResult(filename, r.x, r.y, r.width, r.height);
    }

    protected Rectangle renderText(StyledText text) {
        return renderText(text, -1f);
    }

    protected Rectangle renderText(StyledText text, float visibleChars) {
        LayoutParameters params = new LayoutParameters();
        return renderText(text, visibleChars, params);
    }

    protected Rectangle renderText(StyledText text, float visibleChars, LayoutParameters params) {
        ITextLayout layout = LayoutUtil.layout(fontStore, text, params);

        float th = layout.getTextHeight();
        float tw = layout.getTextWidth();

        Rectangle bounds = new Rectangle2D.Float(0, 0, tw + pad * 2, th + pad * 2).getBounds();

        clearRect(bounds.x, bounds.y, bounds.width, bounds.height);

        batch.begin();
        GdxFontUtil.draw(batch, layout, pad, pad + th, visibleChars);
        batch.end();

        return bounds;
    }

    protected void clearRect(int x, int y, int w, int h) {
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.rect(x, y, w, h);
        shapeRenderer.end();
    }

    public void checkRenderResult(String testName, int x, int y, int w, int h) {
        Pixmap actual = screenshot(x, y, w, h);

        String outputPath = "src/test/resources/render/" + testName + ".png";
        FileHandle fileHandle = Gdx.files.local(outputPath);
        if (generate) {
            PixmapIO.writePNG(fileHandle, actual);
        } else {
            Pixmap expected = new Pixmap(fileHandle);
            assertPixmapEquals(expected, actual);
        }
    }

    private static void assertPixmapEquals(Pixmap expected, Pixmap actual) {
        for (int y = 0; y < expected.getHeight(); y++) {
            for (int x = 0; x < expected.getWidth(); x++) {
                int expectedPixel = expected.getPixel(x, y);

                int actualPixel = actual.getPixel(x, y);
                if (expectedPixel != actualPixel) {
                    int er = (expectedPixel>>24) & 0xFF;
                    int eg = (expectedPixel>>16) & 0xFF;
                    int eb = (expectedPixel>> 8) & 0xFF;
                    int ea = (expectedPixel    ) & 0xFF;

                    int ar = (actualPixel>>24) & 0xFF;
                    int ag = (actualPixel>>16) & 0xFF;
                    int ab = (actualPixel>> 8) & 0xFF;
                    int aa = (actualPixel    ) & 0xFF;

                    if (!cequal(ea, aa) || !cequal(er, ar) || !cequal(eg, ag) || !cequal(eb, ab)) {
                        Assert.fail(String.format(Locale.ROOT,
                            "Pixels at (%d,%d) not equal (maxDiff=%d): expected=%08x, actual=%08x",
                            x, y, MAX_COLOR_DIFF, expectedPixel, actualPixel));
                    }
                }
            }
        }
    }

    private static boolean cequal(int expectedChannelVal, int actualChannelVal) {
        return Math.abs(expectedChannelVal - actualChannelVal) <= MAX_COLOR_DIFF;
    }

    private static Pixmap screenshot(int x, int y, int w, int h) {
        byte[] pixelData = ScreenUtils.getFrameBufferPixels(x, y, w, h, true);

        Pixmap pixmap = new Pixmap(w, h, Format.RGBA8888);
        ByteBuffer buf = pixmap.getPixels();
        buf.put(pixelData);
        buf.rewind();
        return pixmap;
    }

}
