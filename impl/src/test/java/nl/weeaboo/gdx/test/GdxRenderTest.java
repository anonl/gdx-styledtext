package nl.weeaboo.gdx.test;

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

public class GdxRenderTest {

    protected boolean generate;
    protected SpriteBatch batch;

    private ShapeRenderer shapeRenderer;

    @Before
    public void beforeRenderTest() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
    }

    @After
    public void afterRenderTest() {
        batch.dispose();
        shapeRenderer.dispose();
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
                    Assert.fail(String.format(Locale.ROOT,
                        "Pixels at (%d,%d) not equal: expected=%08x, actual=%08x",
                        x, y, expectedPixel, actualPixel));
                }
            }
        }
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
