package nl.weeaboo.gdx.test;

import java.awt.GraphicsEnvironment;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.lwjgl.glfw.GLFW;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;

public class GdxIntegrationTestRunner extends BlockJUnit4ClassRunner {

    private static final int MAX_STARTUP_TIME_SEC = 30;
    private static final int MAX_RUN_TIME_SEC = 900; // 15 minutes

    public GdxIntegrationTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    public void run(final RunNotifier notifier) {
        EachTestNotifier testNotifier = new EachTestNotifier(notifier, getDescription());
        if (GraphicsEnvironment.isHeadless()) {
            // Integration tests require a GL context, so they don't work in a headless env
            testNotifier.fireTestIgnored();
        } else {
            try {
                runInRenderThread(new Runnable() {
                    @Override
                    public void run() {
                        GdxIntegrationTestRunner.super.run(notifier);
                    }
                });
            } catch (InterruptedException e) {
                testNotifier.addFailure(e);
            }
        }
    }

    private void runInRenderThread(final Runnable runner) throws InterruptedException {
        final Semaphore initLock = new Semaphore(0);
        final Semaphore runLock = new Semaphore(0);

        /*
         * Workaround for libGDX issue; GLFW context is terminated upon shutdown, but not reinitialized when
         * creating a new application
         */
        GLFW.glfwInit();

        /*
         * Workaround for libGDX issue; Lwjgl3Application constructor contains an infinite loop (lolwut), so
         * we have to create it in a background thread.
         */
        final Thread initThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

                @SuppressWarnings("unused")
                Lwjgl3Application app = new Lwjgl3Application(new ApplicationAdapter() {
                    @Override
                    public void create() {
                        initLock.release();
                    }

                    @Override
                    public void render() {
                        // Clear backbuffer between tests
                        Gdx.gl.glClearColor(0, 0, 0, 0);
                        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);

                        try {
                            runner.run();
                        } finally {
                            runLock.release();
                        }
                    }
                }, config);
            }
        });
        initThread.start();

        try {
            // Wait for init
            Assert.assertTrue(initLock.tryAcquire(1, MAX_STARTUP_TIME_SEC, TimeUnit.SECONDS));

            // Wait for tests to run
            Assert.assertTrue(runLock.tryAcquire(1, MAX_RUN_TIME_SEC, TimeUnit.SECONDS));
        } finally {
            final Application app = Gdx.app;
            if (app != null) {
                app.exit();
            }

            initThread.join(30000);
        }
    }

}
