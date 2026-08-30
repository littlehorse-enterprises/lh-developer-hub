package io.littlehorse.examples;

import com.google.protobuf.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

public class Book {

    public String name;
    public Date publishDate;
    public Instant publishInstant;
    public Timestamp publishTimestamp;
    public java.sql.Timestamp publishSqlTimestamp;
    public LocalDateTime publishLocalDateTime;
}
