package gabrielhtg.icstardashboardsalesbackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "business_unit")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinesUnitModel {
    @Id
    private String id;

    @Column(name = "total_revenue")
    private long totalRevenue;

    @Column(name = "total_gross_profit")
    private long totalGrossProfit;
}
