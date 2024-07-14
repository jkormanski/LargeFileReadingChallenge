package large.file.reading.challenge.app.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import large.file.reading.challenge.app.dto.AnnualAverageTemperatureDTO;
import large.file.reading.challenge.app.dto.CityTemperatureResponseDTO;
import large.file.reading.challenge.app.dto.CsvRowData;
import large.file.reading.challenge.app.exception.CityNotFoundException;
import large.file.reading.challenge.app.exception.InvalidCityException;
import large.file.reading.challenge.app.store.DataStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
 * The {@code CityTemperatureService} class provides methods for retrieving city temperature data.
 * It interacts with a data store to fetch the annual average temperatures for a specified city.
 *
 * <p>This class is annotated with {@link Service} to indicate that it is a Spring service component,
 * and with {@link Slf4j} to enable logging. The {@link RequiredArgsConstructor} annotation generates
 * a constructor with required arguments, which in this case is the {@link DataStore} dependency.</p>
 *
 * @author Jarosław Kormański
 * @version 1.0
 * @since 2024-07-14
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CityTemperatureService {

    private final DataStore dataStore;

    /**
     * Retrieves the annual average temperatures for a given city.
     *
     * @param city the name of the city
     * @return a {@link CityTemperatureResponseDTO} containing the city's annual average temperatures and the HTTP status
     * @throws InvalidCityException if the city parameter is null or empty
     * @throws CityNotFoundException if no data is found for the given city
     */
    public CityTemperatureResponseDTO getAnnualAverageTemperatures(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new InvalidCityException("City cannot be null or empty");
        }

        List<AnnualAverageTemperatureDTO> annualAverageTemperatureDTOList = dataStore.getData(city);

        if (null == annualAverageTemperatureDTOList) {
            throw new CityNotFoundException("Data for city " + city +  " was not found");
        }

        return new CityTemperatureResponseDTO(city, annualAverageTemperatureDTOList, HttpStatus.OK);
    }
}
