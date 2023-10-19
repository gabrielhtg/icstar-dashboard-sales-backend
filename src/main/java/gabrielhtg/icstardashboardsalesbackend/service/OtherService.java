package gabrielhtg.icstardashboardsalesbackend.service;

import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class OtherService {
    public long convertDateToMillisWithHour(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

            Date date = sdf.parse(dateString);

            return date.getTime();
        } catch (ParseException e) {
            return -1;
        }
    }

    public long convertDateToMillis(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            Date date = sdf.parse(dateString);

            return date.getTime();
        } catch (ParseException e) {
            return -1;
        }
    }

    public String convertMilisToStringWithHour(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        return sdf.format(new Date(time));
    }

}
