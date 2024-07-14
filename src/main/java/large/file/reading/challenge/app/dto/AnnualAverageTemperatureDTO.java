package large.file.reading.challenge.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnnualAverageTemperatureDTO {

    private String year;

    private double averageTemperature;

}
