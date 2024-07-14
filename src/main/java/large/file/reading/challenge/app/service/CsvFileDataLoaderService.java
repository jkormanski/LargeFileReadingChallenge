package large.file.reading.challenge.app.service;

import jakarta.annotation.PostConstruct;
import large.file.reading.challenge.app.dto.AnnualAverageTemperatureDTO;
import large.file.reading.challenge.app.dto.CsvRowData;
import large.file.reading.challenge.app.store.DataStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class for handling loading data from csv file
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CsvFileDataLoaderService {

    private final DataStore dataStore;

    @Value("${path.to.example.csv.file}")
    private String csvFilePath;

    private long csvFileLastModified;

    private boolean fileWatcherEnabled;

    @PostConstruct
    public void init() {
        loadCsvData();
    }

    /**
     * Synchronized method to load and process the CSV data from the file specified
     * by {@code csvFilePath}. This method performs the following steps:
     * <ol>
     *   <li>Clears the data cache.</li>
     *   <li>Disables the file watcher during data loading.</li>
     *   <li>Checks if the CSV file exists.</li>
     *   <li>If the file exists, logs the start of data loading.</li>
     *   <li>Reads the CSV file line by line and converts each line to a {@code CsvRowData} object.</li>
     *   <li>Groups the temperatures by year and city.</li>
     *   <li>Calculates the annual average temperature for each city.</li>
     *   <li>Logs the completion of data loading.</li>
     *   <li>Handles any {@link IOException} that occurs during file reading.</li>
     *   <li>Updates the {@code csvFileLastModified} timestamp to the last modified date of the CSV file.</li>
     * </ol>
     * Finally, the file watcher is re-enabled after data loading.
     */
    synchronized void loadCsvData() {
        dataStore.clearCache();
        fileWatcherEnabled = false;

        if (csvFileExists()) {
            log.info("loadCsvData started");

            try (Stream<String> lines = Files.lines(Paths.get(csvFilePath))) {
                Stream<CsvRowData> csvRowDataStream = lines.map(this::getCsvRowData);
                Map<String, Map<String, List<Double>>> temperaturesGroupedByYearAndCity = groupTemperaturesByYearAndCity(csvRowDataStream);
                calculateAnnualAverageTemperature(temperaturesGroupedByYearAndCity);
                log.info("loadCsvData finished");
            } catch (IOException e) {
                log.error("Error reading data from file {}", csvFilePath, e);
            }

            csvFileLastModified = getCsvFileLastModifedDate();
        }

        fileWatcherEnabled = true;
    }

    /**
     * Groups temperatures by year and city from a stream of CsvRowData.
     *
     * @param csvRowDataStream a stream of CsvRowData objects
     * @return a map where the key is the city name and the value is another map.
     *         This inner map's key is the year and the value is a list of temperatures for that year.
     */
    private Map<String, Map<String, List<Double>>> groupTemperaturesByYearAndCity(Stream<CsvRowData> csvRowDataStream) {
        return csvRowDataStream
                .collect(Collectors.groupingBy(
                        CsvRowData::getCity,
                        Collectors.groupingBy(
                                CsvRowData::getYear,
                                Collectors.mapping(CsvRowData::getTemperature, Collectors.toList())
                        )
                ));
    }

    /**
     * Calculates the annual average temperature for each city and stores the results in the data store.
     *
     * @param temperaturesGroupedByYearAndCity a map where the key is the city name and the value is another map.
     *                                         This inner map's key is the year and the value is a list of temperatures for that year.
     */
    private void calculateAnnualAverageTemperature(Map<String, Map<String, List<Double>>> temperaturesGroupedByYearAndCity) {
        temperaturesGroupedByYearAndCity.forEach((city, annualData) -> {
            List<AnnualAverageTemperatureDTO> cityAnnualTemperatureAverages = annualData.entrySet().stream()
                    .map(entry -> {
                        double averageTemperature = entry.getValue().stream()
                                .mapToDouble(val -> val)
                                .average()
                                .orElse(0.0);
                        // Round to 2 decimal places
                        double roundedAverageTemperature = BigDecimal.valueOf(averageTemperature)
                                .setScale(2, RoundingMode.HALF_UP)
                                .doubleValue();
                        return new AnnualAverageTemperatureDTO(entry.getKey(), roundedAverageTemperature);
                    })
                    .collect(Collectors.toList());
            dataStore.addData(city, cityAnnualTemperatureAverages);
        });
    }

    private CsvRowData getCsvRowData(String line) {
        CsvRowData csvRowData = new CsvRowData();
        String[] lineParts = line.split(";");
        csvRowData.setCity(lineParts[0]);
        csvRowData.setYear(lineParts[1].split("-")[0]);
        csvRowData.setTemperature(Double.valueOf(lineParts[2]));
        return csvRowData;
    }

    /**
     * Checks if the file watcher is enabled.
     *
     * @return {@code true} if the file watcher is enabled, {@code false} otherwise.
     */
    public boolean isFileWatcherSchedulerEnabled() {
        return fileWatcherEnabled;
    }

    /**
     * Retrieves the last modified date of the CSV file specified by {@code csvFilePath}.
     *
     * @return the last modified date of the CSV file in milliseconds since epoch.
     */
    private long getCsvFileLastModifedDate() {
        return new File(csvFilePath).lastModified();
    }

    /**
     * Checks if the CSV file specified by {@code csvFilePath} exists.
     *
     * @return {@code true} if the CSV file exists, {@code false} otherwise.
     */
    private boolean csvFileExists() {
        return new File(csvFilePath).exists();
    }

    /**
     * Checks if the CSV file has been changed since the last check. If the file has been modified,
     * it logs the modification and loads the CSV data. If the file has been deleted, it logs the deletion.
     */
    public void checkIsFileChanged() {

        if (csvFileExists()) {
            if (getCsvFileLastModifedDate() > csvFileLastModified) {
                log.info("File {} was modified", csvFilePath);
                loadCsvData();
            }
        } else {
            if (csvFileLastModified > 0) {
                log.info("File {} was deleted", csvFilePath);
                csvFileLastModified = 0;
            }
        }
    }

}
