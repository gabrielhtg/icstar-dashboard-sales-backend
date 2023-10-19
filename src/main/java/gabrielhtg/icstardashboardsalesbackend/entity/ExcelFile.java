package gabrielhtg.icstardashboardsalesbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcelFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "business_unit")
    private String businessUnit;

    private String account;

    private String opportunity;

    @Column(name = "total_revenue")
    private int totalRevenue;

    @Column(name = "pipeline_status")
    private String pipelineStatus;

    @Column(name = "closed_date")
    private String closedDate;

    @Column(name = "total_gross_profit")
    private int totalGrossProfit;

    @ManyToOne
    @JoinColumn(name = "user_uploader", referencedColumnName = "email")
    private User userUploader;

    @Column(name = "upload_time")
    private String uploadTime;
}
