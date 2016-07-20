package nl.weeaboo.gdx.test;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import org.junit.After;
import org.junit.Before;
import org.junit.experimental.categories.Category;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import nl.weeaboo.gdx.test.junit.GdxUiTest;
import nl.weeaboo.gdx.test.pixmap.PixmapEquality;
import nl.weeaboo.gdx.test.pixmap.ScreenshotHelper;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.gdx.GdxFontUtil;
import nl.weeaboo.styledtext.gdx.TestFreeTypeFontStore;
import nl.weeaboo.styledtext.layout.ITextLayout;
import nl.weeaboo.styledtext.layout.LayoutParameters;
import nl.weeaboo.styledtext.layout.LayoutUtil;

@Category(GdxUiTest.class)
public class GdxRenderTest {

    // Allow a small difference in color to account for rounding errors
    private static final int MAX_COLOR_DIFF = 2;

    protected boolean generate = false;
    protected SpriteBatch batch;
    protected TestFreeTypeFontStore fontStore;

    private final int pad = 4;

    private ShapeRenderer shapeRenderer;
    private PixmapEquality pixmapEquals;

    @Before
    public final void beforeRenderTest() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        fontStore = new TestFreeTypeFontStore();

        pixmapEquals = new PixmapEquality();
        pixmapEquals.setMaxColorDiff(MAX_COLOR_DIFF);
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
        Pixmap actual = ScreenshotHelper.screenshot(x, y, w, h);

        String outputPath = "src/test/resources/render/" + testName + ".png";
        FileHandle fileHandle = Gdx.files.local(outputPath);
        if (generate) {
            PixmapIO.writePNG(fileHandle, actual);
        } else {
            Pixmap expected = new Pixmap(fileHandle);
            pixmapEquals.assertEquals(expected, actual);
        }
    }

}
