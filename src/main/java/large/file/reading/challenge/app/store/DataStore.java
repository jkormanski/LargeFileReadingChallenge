package large.file.reading.challenge.app.store;

import jakarta.annotation.PreDestroy;
import large.file.reading.challenge.app.dto.AnnualAverageTemperatureDTO;
import large.file.reading.challenge.app.dto.CityTemperatureResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * @author Jarosław Kormański
 * DataStore is a component responsible for caching and retrieving annual average temperature data for cities.
 * It uses a ConcurrentHashMap to store the data, ensuring thread-safe operations.
 */
@Component
@Slf4j
public class DataStore {

    private ConcurrentHashMap<String, List<AnnualAverageTemperatureDTO>> cache = new ConcurrentHashMap();

    public List<AnnualAverageTemperatureDTO> getData(String city) {
        return cache.get(city);
    }

    /**
     * Adds annual average temperature data for a specified city to the cache.
     *
     * @param city the name of the city
     * @param data the annual average temperature data for the city
     */
    public void addData(String city, List<AnnualAverageTemperatureDTO> data) {
        cache.put(city, data);
    }

    /**
     * Removes the annual average temperature data for a specified city from the cache.
     *
     * @param city the name of the city
     */
    public void remove(String city) {
        cache.remove(city);
    }

    /**
     * Retrieves a stream of all cities currently stored in the cache.
     *
     * @return a Stream of city names
     */
    public Stream<String> getAllStoredCities() {
        return cache.keySet().stream();
    }

    public void clearCache() {
        cache.clear();
    }

    /**
     * Clears the cache when the bean is destroyed.
     */
    @PreDestroy
    private void onDestroy() {
        clearCache();
    }
}
