package gabrielhtg.icstardashboardsalesbackend.service;

import gabrielhtg.icstardashboardsalesbackend.entity.ExcelFile;
import gabrielhtg.icstardashboardsalesbackend.entity.User;
import gabrielhtg.icstardashboardsalesbackend.model.BusinesUnitModel;
import gabrielhtg.icstardashboardsalesbackend.repository.ExcelFileRepository;
import gabrielhtg.icstardashboardsalesbackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class ExcelService {
    final
    ExcelFileRepository excelFileRepository;

    final
    UserRepository userRepository;

    final OtherService otherService;

    final UserService userService;

    public ExcelService(ExcelFileRepository excelFileRepository, UserRepository userRepository, OtherService otherService, UserService userService) {
        this.excelFileRepository = excelFileRepository;
        this.userRepository = userRepository;
        this.otherService = otherService;
        this.userService = userService;
    }

    @Transactional
    public boolean uploadExcel(MultipartFile fileExcel, String idUploader, User user) throws IOException {
        if (userService.isAdmin(user) && userService.isSessionTokenActive(user)) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileExcel.getBytes());

            Workbook workbook = new XSSFWorkbook(inputStream);

            Sheet sheet = workbook.getSheetAt(0);

            boolean firstRow = true;
            for (Row row : sheet) {
                if (firstRow) {
                    firstRow = false;

                    if (row.getCell(3).getStringCellValue().equals("Business Unit")) {
                        continue;

                    }

                    return false;
                }

                try {
                    System.out.println(row.getCell(3).getStringCellValue());
                    if (!row.getCell(3).getStringCellValue().isEmpty()) {
                        ExcelFile excelFile = new ExcelFile();
                        excelFile.setBusinessUnit(row.getCell(3).getStringCellValue());
                        excelFile.setAccount(row.getCell(4).getStringCellValue());
                        excelFile.setOpportunity(row.getCell(5).getStringCellValue());
                        excelFile.setTotalRevenue((int)row.getCell(13).getNumericCellValue());
                        excelFile.setTotalGrossProfit((int)row.getCell(14).getNumericCellValue());
                        excelFile.setPipelineStatus(row.getCell(16).getStringCellValue());
                        excelFile.setClosedDate(row.getCell(17).getStringCellValue());
                        excelFile.setUserUploader(userRepository.findById(idUploader).orElse(null));
                        excelFile.setUploadTime(new OtherService().convertMilisToStringWithHour(System.currentTimeMillis()));

                        excelFileRepository.save(excelFile);
                    }
                } catch (IllegalStateException e) {
                    return false;
                }
            }
            return true;
        }

        return false;

    }

    @Transactional
    public List<ExcelFile> gelAllData (User user) {

        if (userService.isSessionTokenActive(user)) {
            return excelFileRepository.findAll();
        }

        return null;
    }

    @Transactional
    public List<ExcelFile> getAllByUploaderEmail (String uploaderEmail) {
        return excelFileRepository.findAllByUserUploaderEmail(uploaderEmail);
    }

    @Transactional
    public List<ExcelFile> getAllByPipelineStatus (String pipelineStatus) {
        return excelFileRepository.findAllByPipelineStatus(pipelineStatus);
    }

    @Transactional
    public List<ExcelFile> getAllByBusinessUnit (String businessUnit) {
        return excelFileRepository.findAllByBusinessUnit(businessUnit);
    }

    @Transactional
    public List<ExcelFile> getAllDataFrom (User user, long timeFrom) {


        if (userService.isSessionTokenActive(user)) {
            List<ExcelFile> temp = excelFileRepository.findAll();

            temp.removeIf(excelFile -> otherService.convertDateToMillisWithHour(excelFile.getUploadTime()) < timeFrom);

            return temp;
        }

        return null;
    }

    @Transactional
    public List<ExcelFile> getAllDataUntil (User user, long timeUntil) {

        if (userService.isSessionTokenActive(user)) {
            List<ExcelFile> temp = excelFileRepository.findAll();

            temp.removeIf(excelFile -> (otherService.convertDateToMillisWithHour(excelFile.getUploadTime()) > timeUntil));

            return temp;
        }

        return null;
    }

    @Transactional
    public List<ExcelFile> getAllDataFromUntil (User user, long timeFrom, long timeUntil) {

        if (userService.isSessionTokenActive(user)) {
            List<ExcelFile> temp = excelFileRepository.findAll();

            List<ExcelFile> filteredList = new ArrayList<>();

            for (ExcelFile excelFile : temp) {
                long uploadTime = otherService.convertDateToMillisWithHour(excelFile.getUploadTime());
                if (uploadTime >= timeFrom && uploadTime <= timeUntil) {
                    filteredList.add(excelFile);
                }
            }

            return filteredList;
        }

        return null;
    }

    @Transactional
    public void deleteAllData () {
        excelFileRepository.deleteAll();
    }

    @Transactional
    public long getHotRevenue (String token) {
        User user = userRepository.findBySessionToken(token);

        if (userService.isSessionTokenActive(user)) {
            List<ExcelFile> excelFiles = getAllByPipelineStatus("hot");

            long result = 0;

            for (ExcelFile e: excelFiles) {
                result = result + e.getTotalRevenue();
            }

            return result;
        }

        return 0;
    }

    @Transactional
    public long getHotGrossProfit (String token) {
        User user = userRepository.findBySessionToken(token);

        if (userService.isSessionTokenActive(user)) {
            List<ExcelFile> excelFiles = getAllByPipelineStatus("hot");

            long result = 0;

            for (ExcelFile e: excelFiles) {
                result = result + e.getTotalGrossProfit();
            }

            return result;
        }

        return 0;
    }

    @Transactional
    public long getWarmRevenue (String token) {
        User user = userRepository.findBySessionToken(token);

        if (userService.isSessionTokenActive(user)) {
            List<ExcelFile> excelFiles = getAllByPipelineStatus("warm");

            long result = 0;

            for (ExcelFile e: excelFiles) {
                result = result + e.getTotalRevenue();
            }

            return result;
        }

        return 0;
    }

    @Transactional
    public long getWarmGrossProfit (String token) {
        User user = userRepository.findBySessionToken(token);

        if (userService.isSessionTokenActive(user)) {
            List<ExcelFile> excelFiles = getAllByPipelineStatus("warm");

            long result = 0;

            for (ExcelFile e: excelFiles) {
                result = result + e.getTotalGrossProfit();
            }

            return result;
        }

        return 0;
    }

    @Transactional
    public long getColdRevenue (String token) {
        User user = userRepository.findBySessionToken(token);

        if (userService.isSessionTokenActive(user)) {
            List<ExcelFile> excelFiles = getAllByPipelineStatus("cold");

            long result = 0;

            for (ExcelFile e: excelFiles) {
                result = result + e.getTotalRevenue();
            }

            return result;
        }

        return 0;
    }

    @Transactional
    public long getColdGrossProfit (String token) {
        User user = userRepository.findBySessionToken(token);

        if (userService.isSessionTokenActive(user)) {
            List<ExcelFile> excelFiles = getAllByPipelineStatus("cold");

            long result = 0;

            for (ExcelFile e: excelFiles) {
                result = result + e.getTotalGrossProfit();
            }

            return result;
        }

        return 0;
    }

//    public List<BusinesUnitModel> getTotalRevenueByBusinesUnits (String token) {
//        User user = userRepository.findBySessionToken(token);
//
//        if (userService.isSessionTokenActive(user)) {
//            List<ExcelFile> excelFiles = excelFileRepository.findAll();
//            List<String> businessUnitsString = new ArrayList<>();
//
//            for (ExcelFile excelFile : excelFiles) {
//                businessUnitsString.add(excelFile.getBusinessUnit());
//            }
//
//            HashSet<String> uniqueStrings = new HashSet<>(businessUnitsString);
//
//            // Clear the original ArrayList
//            businessUnitsString.clear();
//
//            // Add the unique strings back to the ArrayList
//            businessUnitsString.addAll(uniqueStrings);
//
//            List<BusinesUnitModel> businesUnits = new ArrayList<>();
//            long totalRevenue = 0;
//            long totalGross = 0;
//
//            for (String excelFileString : businessUnitsString) {
//
//                for (ExcelFile excelFile: excelFiles) {
//                    if (excelFile.getBusinessUnit().equals(excelFileString)) {
//                        totalRevenue = totalRevenue + excelFile.getTotalRevenue();
//                        totalGross = totalGross + excelFile.getTotalGrossProfit();
//                    }
//                }
//
//                businesUnits.add(new BusinesUnitModel(excelFileString,totalRevenue, totalGross));
//            }
//
//            return businesUnits;
//        }
//
//        return null;
//    }

    public List<BusinesUnitModel> getTotalRevenueByBusinesUnits (String token) {
        User user = userRepository.findBySessionToken(token);

        if (userService.isSessionTokenActive(user)) {
            List<ExcelFile> excelFiles = excelFileRepository.findAll();
            List<String> businessUnitsString = new ArrayList<>();

            for (ExcelFile excelFile : excelFiles) {
                businessUnitsString.add(excelFile.getBusinessUnit());
            }

            HashSet<String> uniqueStrings = new HashSet<>(businessUnitsString);

            // Clear the original ArrayList
            businessUnitsString.clear();

            // Add the unique strings back to the ArrayList
            businessUnitsString.addAll(uniqueStrings);

            List<BusinesUnitModel> businesUnits = new ArrayList<>();


            for (String excelFileString : businessUnitsString) {
                long totalRevenue = 0;
                long totalGross = 0;

                for (ExcelFile excelFile: excelFiles) {
                    if (excelFile.getBusinessUnit().equals(excelFileString)) {
                        totalRevenue = totalRevenue + excelFile.getTotalRevenue();
                        totalGross = totalGross + excelFile.getTotalGrossProfit();
                    }
                }

                businesUnits.add(new BusinesUnitModel(excelFileString,totalRevenue, totalGross));
            }

            return businesUnits;
        }

        return null;
    }
}
