package nl.weeaboo.styledtext.gdx;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Logger;
import com.google.common.testing.GcFinalization;
import com.google.common.testing.GcFinalization.FinalizationPredicate;

public class GdxCleanerTest {

    private static final Logger LOG = new Logger(GdxCleanerTest.class.getName());

    private final GdxCleaner cleaner = GdxCleaner.get();

    @Before
    public void before() {
        garbageCollect(0);
    }

    @Test
    public void invalidateGC() {
        createResource(1);
        garbageCollect(0); // Unreferenced resources get collected

        invalidateGCSub();

        garbageCollect(0); // alpha can now be garbage collected
    }

    private void invalidateGCSub() {
        Resource alpha = createResource(2);
        createResource(3);

        garbageCollect(1); // We're still holding a reference to alpha

        LOG.debug("This log statements prevents alpha from being garbage-collected: " + alpha);
    }

    private Resource createResource(int id) {
        Resource resource = new Resource(id);
        GdxCleaner.get().register(resource, new Cleanup(id));
        return resource;
    }

    private void garbageCollect(final int expectedSize) {
        GcFinalization.awaitDone(new FinalizationPredicate() {
            @Override
            public boolean isDone() {
                cleaner.cleanUp();
                return cleaner.size() == expectedSize;
            }
        });
    }

    private static final class Resource {

        final int id;

        Resource(int id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return String.valueOf(id);
        }

    }

    private static final class Cleanup implements Disposable {

        final int id;

        Cleanup(int id) {
            this.id = id;
        }

        @Override
        public void dispose() {
            LOG.info("Cleanup: " + id);
        }

    }

}
