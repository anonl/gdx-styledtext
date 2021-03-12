package nl.weeaboo.styledtext.gdx;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Iterator;

import javax.annotation.concurrent.ThreadSafe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Logger;
import com.google.errorprone.annotations.concurrent.GuardedBy;

/**
 * Calls the {@link Disposable#dispose()} on resources when they become eligible for garbage collection.
 */
@ThreadSafe
final class GdxCleaner {

    private static final Logger LOG = new Logger(GdxCleaner.class.getName());

    private static final GdxCleaner INSTANCE = new GdxCleaner();

    private final Object stateLock = new Object();

    @GuardedBy("stateLock")
    private final ReferenceQueue<Object> garbage = new ReferenceQueue<Object>();

    @GuardedBy("stateLock")
    private final Array<Cleanable> registered = new Array<Cleanable>(false, 8);

    private GdxCleaner() {
    }

    public static GdxCleaner get() {
        return INSTANCE;
    }

    /**
     * Registers a disposable resource with the cleaner. The {@link Disposable#dispose()} method will be
     * called when the object is garbage collected.
     */
    public <T> void register(T referent, Disposable cleanup) {
        cleanUp();

        synchronized (stateLock) {
            registered.add(new Cleanable(referent, garbage, cleanup));
        }
    }

    /**
     * Returns the number of registered resources. When a resource is disposed, it's also automatically
     * unregistered.
     */
    public int size() {
        synchronized (stateLock) {
            return registered.size;
        }
    }

    /** Garbage collect resources that are no longer referenced. */
    public void cleanUp() {
        // Remove dead references
        synchronized (stateLock) {
            for (Iterator<Cleanable> itr = registered.iterator(); itr.hasNext(); ) {
                Cleanable ref = itr.next();
                if (ref.get() == null) {
                    itr.remove();
                }
            }

            // Clean garbage
            Reference<?> rawReference;
            while ((rawReference = garbage.poll()) != null) {
                CleanupRunnable cleanupRunnable = new CleanupRunnable((Cleanable)rawReference);
                if (Gdx.app != null) {
                    Gdx.app.postRunnable(cleanupRunnable);
                } else {
                    cleanupRunnable.run();
                }
            }
        }
    }

    private static final class CleanupRunnable implements Runnable {

        private final Cleanable cleanable;

        CleanupRunnable(Cleanable cleanable) {
            this.cleanable = cleanable;
        }

        @Override
        public void run() {
            LOG.debug("Disposing resource: " + cleanable);
            try {
                cleanable.cleanup.dispose();
            } catch (RuntimeException e) {
                LOG.error("Cleanup task threw an exception: " + cleanable.cleanup, e);
            }
        }
    }

    private static final class Cleanable extends WeakReference<Object> {

        private final Disposable cleanup;
        private final String stringRepresentation;

        public Cleanable(Object referent, ReferenceQueue<? super Object> q, Disposable cleanup) {
            super(referent, q);

            if (cleanup == null) {
                throw new NullPointerException("Cleanup function may not be null");
            } else if (referent == cleanup) {
                throw new IllegalArgumentException("Cleanup  function shouldn't reference (or be equal to) the referent");
            }

            this.cleanup = cleanup;

            stringRepresentation = String.valueOf(referent);
        }

        @Override
        public String toString() {
            return stringRepresentation;
        }

    }
}
