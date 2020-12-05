package ar.com.magneto.performance;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

@Slf4j
public class DurationLog {

    public LocalDateTime initEvent(){
        return LocalDateTime.now();
    }

    public void logDurationInMillisecs(LocalDateTime initialTime, String evento){
        log.info("Duración del evento {}: {} milisegundos", evento, ChronoUnit.MILLIS.between(initialTime,LocalDateTime.now()));
    }

}
