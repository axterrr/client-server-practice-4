package ua.edu.ukma.clientserver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchParams {
    private String name;
    private String category;
    private Integer amountFrom;
    private Integer amountTo;
    private Double priceFrom;
    private Double priceTo;
}
