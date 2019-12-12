package org.openjfx;

import nl.liacs.watch.protocol.server.WrappedConnection;
import nl.liacs.watch.protocol.types.Message;
import nl.liacs.watch.protocol.types.ParameterType;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
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
        while (this.connection.isOpen()) {
            Message item = this.connection.receive();

            switch (item.type) {
                case INCREMENT:
                    item.parameters[0].setType(ParameterType.STRING);
                    var sensor = item.parameters[0].getString();
                    System.out.println(sensor);
                    if (sensor.equals("BATTERY")) {
                        item.parameters[2].setType(ParameterType.DOUBLE);
                        var percentage = item.parameters[2].getDouble();
                        watch.getWatchData().setBatteryPercentage(percentage.intValue());
                        break;
                    }

                    var values = Arrays.stream(item.parameters).skip(2).map((param) -> {
                        param.setType(ParameterType.DOUBLE);
                        return param.getDouble();
                    }).collect(Collectors.toList());
                    var dataPoint = new DataPoint(sensor, LocalDate.now(), LocalTime.now(), values);

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
