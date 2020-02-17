package org.openjfx;

import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import nl.liacs.watch.protocol.server.WrappedConnection;
import nl.liacs.watch.protocol.types.Message;
import nl.liacs.watch.protocol.types.MessageParameter;
import nl.liacs.watch.protocol.types.MessageParameterString;

/**
 * Connects a {@link Smartwatch} with a {@link WrappedConnection}.
 */
public class WatchConnector implements Closeable {
    private final Smartwatch watch;
    private final WrappedConnection connection;
    private final List<Runnable> dataObservers = new ArrayList<>();
    private final Thread thread;
    private final Thread batteryThread;

    /**
     * Create a new {@link WatchConnector} and start the update loop.
     *
     * @param watch The watch to bind
     * @param connection The connection to bind
     * @param controller The controlle to bind
     */
    WatchConnector(@NotNull Smartwatch watch, @NotNull WrappedConnection connection) {
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

        this.batteryThread = new Thread(() -> {
            try {
                this.batteryLoop();
            } catch (IOException e) {
                System.out.println(e);
            } catch (InterruptedException e) {
                return;
            }
        });
        this.batteryThread.start();
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
                case INCREMENT: {
                    var sensor = item.parameters[0].asString().getValue();
                    System.out.println(sensor);
                    if (sensor.equals("BATTERY")) {
                        var percentage = item.parameters[2].asDouble().getValue();
                        watch.getWatchData().setBatteryPercentage(percentage.intValue());
                        break;
                    }

                    var values = Arrays.stream(item.parameters)
                        .skip(2)
                        .map((param) -> param.asDouble().getValue())
                        .collect(Collectors.toList());
                    var dataPoint = new DataPoint(sensor, LocalDateTime.now(), values);
                    System.out.println("Data point: "+Arrays.toString(values.toArray()));
                    var list = Collections.singletonList(dataPoint);
                    watch.addData(list);

                    for (var observer : this.dataObservers) {
                        observer.run();
                    }

                    break;
                }

                case PLAYBACK: {
                    var sensor = item.parameters[0].asString().getValue();
                    var values = Arrays.stream(item.parameters)
                        .skip(2)
                        .map((param) -> param.asDouble().getValue())
                        .collect(Collectors.toList());
                    var date = LocalDateTime.ofEpochSecond(item.parameters[1].asLong().getValue() / 1000, 0, ZoneOffset.UTC);
                    var dataPoint = new DataPoint(sensor, date, values);

                    var list = Collections.singletonList(dataPoint);
                    watch.addData(list);

                    for (var observer : this.dataObservers) {
                        observer.run();
                    }

                    break;
                }

                case GET_VALUES:
                    var key = item.parameters[0].asString().getValue();
                    var res = getKeyValue(key);
                    if (res != null) {
                        item.makeReply(0, null, res);
                    } else { // key not available
                        item.makeReply(404, "key not found");
                    }
                    break;

                case SET_VALUES:
                    throw new IllegalArgumentException("TODO");

                case PING:
                case REPLY:
                    throw new IllegalStateException("PING or REPLY leaked out of WrappedConnection");
            }
        }
    }

    /**
     * Temp loop for battery fetching, later this should be a sensor and code is
     * already written for that.
     */
    private void batteryLoop() throws InterruptedException, IOException {
        while (!this.connection.isClosed()) {
            this.connection.getValues("system.battery").thenAccept(params -> {
                var percentage = params[0].asInteger().getValue();
                System.out.printf("got battery value of %d\n", percentage);
                watch.getWatchData().setBatteryPercentage(percentage);

                for (var observer : this.dataObservers) {
                    observer.run();
                }
            });

            Thread.sleep(10 * 1000);
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

    /**
     * Gets the value associated with {@code key}.
     * Returns {@code null} when the key couldn't be found.
     *
     * @param key The key to get the value of.
     * @return The value, or {@code null} if the key wasn't found.
     */
    @Nullable
    private MessageParameter[] getKeyValue(@NotNull String key) {
        if (key.equals("system.name")) { // just to test
            return new MessageParameter[]{
                new MessageParameterString("nl.liacs.watch-host"),
            };
        }

        return null;
    }

    public void AddDataObserver(Runnable runnable) {
        this.dataObservers.add(runnable);
    }

    /**
     * Get all sensors from the watch.
     * TODO: THIS FUNCTION IS FOR DEMO PUPOSES AND SHOULD BE MOVED AND REFACTORED
     *
     * @return A future that completes to a list of {@link Sensor} instances.
     * @throws IOException IO error when failing to retrieve the sensor list.
     */
    public CompletableFuture<List<Sensor>> getSensors() throws IOException {
        var sensorsFut = connection.getValues("sensor.list");

        return sensorsFut.thenApply((parameters) -> {
            var sensorFutures = new ArrayList<CompletableFuture<Sensor>>();
            for (var param : parameters) {
                var sensorName = param.asString().getValue();

                try {
                    var sensorFut = Sensor.getByName(sensorName, connection);
                    sensorFutures.add(sensorFut);
                } catch (IOException e) {
                    // HACK
                    System.out.println(e);
                }
            }

            return CompletableFuture.allOf(sensorFutures.toArray(new CompletableFuture[sensorFutures.size()])).thenApply(x -> sensorFutures);
        }).thenCompose(waitAll -> {
            return waitAll.thenApply(sensorFutures -> {
                var sensors = new ArrayList<Sensor>();
                for (var fut : sensorFutures) {
                    try {
                        sensors.add(fut.get());
                    } catch (Exception e) {
                        // HACK
                        System.out.println(e);
                    }
                }
                return sensors;
            });
        });
    }
}
