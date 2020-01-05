package org.openjfx;

import nl.liacs.watch.protocol.server.WrappedConnection;
import nl.liacs.watch.protocol.types.Message;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Connects a {@link Smartwatch} with a {@link WrappedConnection}.
 */
public class WatchConnector implements Closeable {
    private final Smartwatch watch;
    private final WrappedConnection connection;
    private final Thread thread;

    /**
     * Create a new {@link WatchConnector} and start the update loop.
     *
     * @param watch The watch to bind
     * @param connection The connection to bind
     */
    public WatchConnector(@NotNull Smartwatch watch, @NotNull WrappedConnection connection) {
        this.watch = watch;
        this.connection = connection;

        this.thread = new Thread(() -> {
            try {
                this.updateLoop();
            } catch (InterruptedException e) {
                return;
            }
        });
        this.thread.start();
    }

    /**
     * The main update loop, should be run in a thread.
     *
     * @throws InterruptedException Interrupted error when the thread is interrupted.
     */
    private void updateLoop() throws InterruptedException {
        while (!this.connection.isClosed()) {
            Message item = this.connection.receive();

            switch (item.type) {
                case INCREMENT:
                    var sensor = item.parameters[0].asString().getValue();
                    System.out.println(sensor);
                    if (sensor.equals("BATTERY")) {
                        var percentage = item.parameters[2].asDouble().getValue();
                        watch.getWatchData().setBatteryPercentage(percentage.intValue());
                        break;
                    }

                    var values = Arrays.stream(item.parameters).skip(2).map((param) -> param.asDouble().getValue()).collect(Collectors.toList());
                    var dataPoint = new DataPoint(sensor, LocalDateTime.now(), values);

                    var list = Collections.singletonList(dataPoint);
                    watch.addData(list);

                    break;
            }
        }
    }

    /**
     * Stop the update loop.
     *
     * @throws IOException IO error when failing to close the connection.
     */
    @Override
    public void close() throws IOException {
        this.connection.close();
        this.thread.interrupt();
    }
}
