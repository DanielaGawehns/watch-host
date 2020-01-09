CREATE TABLE IF NOT EXISTS "smartwatch"
(
    ID TEXT not null
        constraint smartwatch_pk
            primary key,
    name TEXT not null
);

CREATE TABLE IF NOT EXISTS "watch_data"
(
    ID TEXT not null
        constraint watch_data_pk
            primary key
        references smartwatch
            on delete cascade,
    battery_level int not null,
    max_storage REAL not null,
    used_storage REAL not null
);
CREATE UNIQUE INDEX IF NOT EXISTS watch_data_ID_uindex
    on watch_data (ID);

CREATE TABLE IF NOT EXISTS comments
(
    ID TEXT not null
        constraint comments_smartwatch_ID_fk
            references smartwatch
                on delete cascade,
    date DATE not null,
    time_start TIME not null,
    time_end TIME not null,
    comment TEXT not null,
    type TEXT not null
);

CREATE TABLE IF NOT EXISTS "measurements"
(
    ID int default -1 not null
        constraint measurements_pk
            primary key
        references smartwatch
            on delete cascade,
    time_start TIME default -1 not null,
    time_end TIME default -1 not null,
    measurement_ID int default -1 not null
);

CREATE TABLE IF NOT EXISTS "datalists"
(
    ID int not null
        references smartwatch
            on delete cascade,
    sensor_name TEXT not null,
    data_ID int
        constraint datalists_pk
            primary key
);
