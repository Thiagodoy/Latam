package com.core.behavior.util;

import com.core.behavior.annotations.PositionParameter;
import com.core.behavior.model.Log;
import com.core.behavior.model.Ticket;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.beanio.StreamFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
public class Utils {

    private static List<Field> fieldsTicket;
    private static List<Field> fieldsLog;
    private static List<Field> fields;
    private static DateTimeFormatter dateTimeFormatter;
    private static SimpleDateFormat formmatDate;

    public static enum TypeField {
        TICKET, LOG
    };

    static {

        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        formmatDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        fieldsTicket = Arrays.asList(Ticket.class.getDeclaredFields())
                .stream()
                .filter(f -> f.isAnnotationPresent(PositionParameter.class))
                .collect(Collectors.toList());

        fieldsLog = Arrays.asList(Log.class.getDeclaredFields())
                .stream()
                .filter(f -> f.isAnnotationPresent(PositionParameter.class))
                .collect(Collectors.toList());

    }

    public static File convertToFile(MultipartFile file) throws FileNotFoundException, IOException {

        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();

        return convFile;
    }

    public static LocalDateTime convertDateToLOcalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static String mountBatchInsert(Object t, TypeField type) {

        List<String> parameters = new ArrayList<String>();
        TreeMap<Integer, String> values = new TreeMap<>();

        switch (type) {
            case TICKET:
                fields = fieldsTicket;
                break;
            case LOG:
                fields = fieldsLog;
                break;
        }

        fields.forEach(f -> {

            try {
                Object value = f.get(t);
                Integer index = f.getAnnotation(PositionParameter.class).value();
                putParameter(values, index, value);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }

        });

        for (Integer key : values.keySet()) {
            parameters.add(values.get(key));
        }

        String retono = "(" + parameters.stream().collect(Collectors.joining(",")) + ")";
        // Logger.getLogger(Utils.class.getName()).log(Level.INFO,retono);
        return retono;
    }

    private static void putParameter(TreeMap<Integer, String> parameters, Integer index, Object value) {

        if (value == null) {
            parameters.put(index, "NULL");
        } else if (value instanceof String) {
            parameters.put(index, "'" + value.toString().replace("'", " ") + "'");
        } else if ((value instanceof Long) || (value instanceof Integer) || (value instanceof Double)) {
            parameters.put(index, value.toString());
        } else if (value instanceof LocalDateTime) {
            parameters.put(index, "'" + ((LocalDateTime) value).format(dateTimeFormatter) + "'");
        } else if (value instanceof Date) {
            parameters.put(index, "'" + formmatDate.format(value) + "'");
        } else {
            parameters.put(index, "'" + value.toString() + "'");
        }

    }

    public static String getContenFromLayout(EmailLayoutEnum layout) throws IOException {

        StreamFactory factory = StreamFactory.newInstance();
        InputStream stream = null;

        switch (layout) {
            case CONGRATS:
                stream = factory.getClass().getClassLoader().getResourceAsStream("static/CONGRAT-EMAIL.html");

                break;
        }

        String theString = IOUtils.toString(stream, "UTF-8");

        return theString;

    }

    public static  File loadLogo()
            throws IOException {
        StreamFactory factory = StreamFactory.newInstance();
        InputStream initialStream = factory.getClass().getClassLoader().getResourceAsStream("static/logo.png");;

        File targetFile = File.createTempFile("logo", ".png");

        FileUtils.copyInputStreamToFile(initialStream, targetFile);
        return targetFile;
    }
    
    public static String generatePasswordRandom(){
    
        Random rd = new Random(5);
          
        int value = rd.nextInt();
        int value2 = rd.nextInt();
        return  "%$" + value + "##!" + value2 + "+";     
        
       
    }

}
