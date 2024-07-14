package large.file.reading.challenge.app.file.watcher;

import large.file.reading.challenge.app.service.CityTemperatureService;
import large.file.reading.challenge.app.service.CsvFileDataLoaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class FileWatcherScheduler {

    private final CsvFileDataLoaderService csvFileDataLoaderService;

    /**
     * Periodically checks if the file being watched has been modified.
     * This method is executed at a fixed rate, specified in milliseconds.
     *
     * <p>If the file watcher scheduler is enabled in the {@link CsvFileDataLoaderService},
     * it will check if the file has changed.</p>
     */
    @Scheduled(fixedRate = 5000)
    public void watchFile() {
        if (csvFileDataLoaderService.isFileWatcherSchedulerEnabled()) {
            csvFileDataLoaderService.checkIsFileChanged();
        }
    }

}
