package gabrielhtg.icstardashboardsalesbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gabrielhtg.icstardashboardsalesbackend.entity.ExcelFile;
import gabrielhtg.icstardashboardsalesbackend.entity.User;
import gabrielhtg.icstardashboardsalesbackend.model.WebResponse;
import gabrielhtg.icstardashboardsalesbackend.repository.ExcelFileRepository;
import gabrielhtg.icstardashboardsalesbackend.repository.UserRepository;
import gabrielhtg.icstardashboardsalesbackend.service.AuthService;
import gabrielhtg.icstardashboardsalesbackend.service.ExcelService;
import gabrielhtg.icstardashboardsalesbackend.service.OtherService;
import gabrielhtg.icstardashboardsalesbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
public class ExcelController {
    private final ExcelFileRepository excelFileRepository;

    private final ExcelService excelService;

    private final OtherService otherService;

    private final UserService userService;

    private final UserRepository userRepository;

    private final AuthService authService;

    private final ObjectMapper objectMapper;

//    private StringBuilder stringBuilder;

    public ExcelController(ExcelFileRepository excelFileRepository, ExcelService excelService, OtherService otherService, ObjectMapper objectMapper, UserService userService, UserRepository userRepository, AuthService authService) {
        this.excelFileRepository = excelFileRepository;
        this.excelService = excelService;
        this.otherService = otherService;
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @PostMapping(
            path = "/api/upload-excel",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> uploadFileExcel (@CookieValue(name = "token") String token, @RequestPart(name = "file-excel") MultipartFile fileExcel) throws IOException {

        User user = userRepository.findBySessionToken(token);
        userService.updateSessionTime(user);

        if (userService.isAdmin(user) && userService.isSessionTokenActive(user) && excelService.uploadExcel(fileExcel, user.getEmail(), user)) {
            return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<String>(true, "Upload Success. Data saved!", null)));
        }

        else if (!userService.isSessionTokenActive(user)) {
            authService.logout(user);
            return ResponseEntity.status(401).body(objectMapper.writeValueAsString(new WebResponse<String>(false, "Invalid Token", null)));
        }

        else if (!userService.isAdmin(user)) {
            return ResponseEntity.status(403).body(objectMapper.writeValueAsString(new WebResponse<String>(false, "Failed! You're Not Admin", null)));
        }

        return ResponseEntity.status(400).body(objectMapper.writeValueAsString(new WebResponse<String>(false, "Excel table format unsupported!", null)));
    }

    @GetMapping(
            path = "/api/get-all-sales-data",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> getAllSalesData (@CookieValue(name = "token") String token) throws JsonProcessingException {

        User user = userRepository.findBySessionToken(token);

        userService.updateSessionTime(user);

        if (userService.isSessionTokenActive(user)) {
            List<ExcelFile> excelFileList = excelFileRepository.findAll();
            try {
                return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<>(true, null, excelFileList)));
            } catch (IndexOutOfBoundsException e) {
                return ResponseEntity.status(400).body(objectMapper
                        .writeValueAsString(new WebResponse<String>(false, "Sales Data is Empty", null)));
            }
        }

        // akan return kode unauthorized
        authService.logout(user);
        return ResponseEntity.status(401).body(objectMapper.writeValueAsString(new WebResponse<String>(false, "Invalid Token", null)));
    }


    @GetMapping(
            path = "/api/get-all-by-uploader",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> getAllByUploader (@CookieValue(name = "token") String token, @RequestHeader(name = "email") String email) throws JsonProcessingException {

        User user = userRepository.findBySessionToken(token);

        try {
            userService.updateSessionTime(user);
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(objectMapper.writeValueAsString(new WebResponse<>(false, "Unauthorized", null)));
        }

        if (userService.isSessionTokenActive(user)) {
            List<ExcelFile> excelFiles = excelService.getAllByUploaderEmail(email);

           if (!excelFiles.isEmpty()) {
               return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<>(true, null, excelFiles)));
           }

           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectMapper.writeValueAsString(new WebResponse<String>(false, "Data Not Found", null)));

        }

        // akan return kode unauthorized
        authService.logout(user);
        return ResponseEntity.status(401).body(objectMapper.writeValueAsString(new WebResponse<Void>(false, "Invalid Token", null)));
    }

    @GetMapping(
            path = "/api/get-all-from",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> getAllFrom (@CookieValue(name = "token") String token, @RequestParam(name = "from") String from) throws JsonProcessingException {

        User user = userRepository.findBySessionToken(token);
        userService.updateSessionTime(user);

        if (userService.isSessionTokenActive(user)) {
            List<ExcelFile> excelFiles = excelService.getAllDataFrom(user, otherService.convertDateToMillis(from));

            if (!excelFiles.isEmpty()) {

                return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<>(true, null, excelFiles)));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectMapper.writeValueAsString(new WebResponse<Void>(false, "Data Not Found", null)));

        }

        // akan return kode unauthorized
        authService.logout(user);
        return ResponseEntity.status(401).body(objectMapper.writeValueAsString(new WebResponse<Void>(false, "Invalid Token", null)));
    }

    @GetMapping(
            path = "/api/get-all-from-until",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> getAllFromUntil (@CookieValue(name = "token") String token, @RequestParam(name = "from") String from, @RequestParam(name = "until") String until) throws JsonProcessingException {

        User user = userRepository.findBySessionToken(token);
        userService.updateSessionTime(user);

        if (userService.isSessionTokenActive(user)) {
            List<ExcelFile> excelFiles = excelService.getAllDataFromUntil(user, otherService.convertDateToMillis(from), otherService.convertDateToMillis(until));

            if (!excelFiles.isEmpty()) {
                return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<>(true, null, excelFiles)));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectMapper.writeValueAsString(new WebResponse<Void>(false, "Data Not Found", null)));
        }

        // akan return kode unauthorized
        authService.logout(user);
        return ResponseEntity.status(401).body(objectMapper.writeValueAsString(new WebResponse<Void>(false, "Invalid Token", null)));
    }

    @GetMapping(
            path = "/api/get-all-until",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> getAllUntil (@CookieValue(name = "token") String token, @RequestParam(name = "until") String until) throws JsonProcessingException {

        User user = userRepository.findBySessionToken(token);
        userService.updateSessionTime(user);

        if (userService.isSessionTokenActive(user)) {
            List<ExcelFile> excelFiles = excelService.getAllDataUntil(user, otherService.convertDateToMillis(until));

            if (!excelFiles.isEmpty()) {
                return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<>(true, null, excelFiles)));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectMapper.writeValueAsString(new WebResponse<Void>(false, "Data Not Found", null)));

        }

        // akan return kode unauthorized
        authService.logout(user);
        return ResponseEntity.status(401).body(objectMapper.writeValueAsString(new WebResponse<Void>(false, "Invalid Token", null)));
    }

    @GetMapping(
            path = "/api/get-all-by-pipelineStatus",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> getAllByPipelineStatus (@CookieValue(name = "token") String token, @RequestParam(name = "pipelineStatus") String pipelineStatus) throws JsonProcessingException {

        User user = userRepository.findBySessionToken(token);
        userService.updateSessionTime(user);

        if (userService.isSessionTokenActive(user)) {
            List<ExcelFile> excelFiles = excelService.getAllByPipelineStatus(pipelineStatus);

            if (!excelFiles.isEmpty()) {
                return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<>(true, null, excelFiles)));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectMapper.writeValueAsString(new WebResponse<Void>(false, "Data Not Found", null)));

        }

        // akan return kode unauthorized
        authService.logout(user);
        return ResponseEntity.status(401).body("Invalid Token");
    }

    @GetMapping(
            path = "/api/get-all-by-business-unit",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> getAllByBusinessUnit (@CookieValue(name = "token") String token, @RequestParam(name = "businessUnit") String businessUnit) throws JsonProcessingException {

        User user = userRepository.findBySessionToken(token);
        userService.updateSessionTime(user);

        if (userService.isSessionTokenActive(user)) {
            List<ExcelFile> excelFiles = excelService.getAllByBusinessUnit(businessUnit);

            if (!excelFiles.isEmpty()) {
                return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<>(true, "success", excelFiles)));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectMapper.writeValueAsString(new WebResponse<Void>(false, "Data Not Found", null)));

        }

        // akan return kode unauthorized
        authService.logout(user);
        return ResponseEntity.status(401).body("Invalid Token");
    }

    @DeleteMapping(
            path = "/api/delete-all-sales-data",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> deleteAllSalesData (@CookieValue(name = "token") String token) throws JsonProcessingException {
        User user = userRepository.findBySessionToken(token);

        if (user != null && userService.isAdmin(user) && userService.isSessionTokenActive(user)) {
            excelService.deleteAllData();
            return ResponseEntity.status(200).body(objectMapper.writeValueAsString(new WebResponse<Void>(true, "Success Remove All Data", null)));
        }

        else if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(objectMapper.writeValueAsString(new WebResponse<Void>(false, "Invalid Token", null)));
        }

        userService.updateSessionTime(user);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectMapper.writeValueAsString(new WebResponse<Void>(false, "Delete All Sales Data Failed", null)));
    }

    @GetMapping (
            path = "/api/get-hot-revenue",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> getHotRevenue (@CookieValue(name = "token") String token) throws JsonProcessingException {
        return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<>(true, "success", excelService.getHotRevenue(token))));
    }

    @GetMapping (
            path = "/api/get-hot-gross",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> getHotGrossProfit (@CookieValue(name = "token") String token) throws JsonProcessingException {
        return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<>(true, "success", excelService.getHotGrossProfit(token))));
    }

    @GetMapping (
            path = "/api/get-warm-revenue",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> getWarmRevenue (@CookieValue(name = "token") String token) throws JsonProcessingException {
        return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<>(true, "success", excelService.getWarmRevenue(token))));
    }

    @GetMapping (
            path = "/api/get-warm-gross",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> getWarmGrossProfit (@CookieValue(name = "token") String token) throws JsonProcessingException {
        return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<>(true, "success", excelService.getWarmGrossProfit(token))));
    }

    @GetMapping (
            path = "/api/get-cold-revenue",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> getColdRevenue (@CookieValue(name = "token") String token) throws JsonProcessingException {
        return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<>(true, "success", excelService.getColdRevenue(token))));
    }

    @GetMapping (
            path = "/api/get-cold-gross",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> getColdGrossProfit (@CookieValue(name = "token") String token) throws JsonProcessingException {
        return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<>(true, "success", excelService.getColdGrossProfit(token))));
    }

    @GetMapping(
            path = "/api/get-total-by-busines-units",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> getTotalRevenueByBusinesUnits (@CookieValue (name = "token") String token) throws JsonProcessingException {
        return ResponseEntity.ok(objectMapper.writeValueAsString(new WebResponse<>(true, "success", excelService.getTotalRevenueByBusinesUnits(token))));
    }
}
