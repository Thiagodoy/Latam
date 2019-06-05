package com.core.behavior.util;

import com.core.activiti.model.UserActiviti;
import com.core.activiti.model.UserInfo;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
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
    private static Tika tika;
    private static final Map<String, String> entitiesHtml = new HashMap<String, String>();

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

        tika = new Tika();

        entitiesHtml.put("à", "&#224;");
        entitiesHtml.put("è", "&#232;");
        entitiesHtml.put("ì", "&#236;");
        entitiesHtml.put("ò", "&#242;");
        entitiesHtml.put("ù", "&#249;");
        entitiesHtml.put("À", "&#192;");
        entitiesHtml.put("È", "&#200;");
        entitiesHtml.put("Ì", "&#204;");
        entitiesHtml.put("Ò", "&#210;");
        entitiesHtml.put("Ù", "&#217;");
        entitiesHtml.put("á", "&#225;");
        entitiesHtml.put("é", "&#233;");
        entitiesHtml.put("í", "&#237;");
        entitiesHtml.put("ó", "&#243;");
        entitiesHtml.put("ú", "&#250;");
        entitiesHtml.put("ý", "&#253;");
        entitiesHtml.put("Á", "&#193;");
        entitiesHtml.put("É", "&#201;");
        entitiesHtml.put("Í", "&#205;");
        entitiesHtml.put("Ó", "&#211;");
        entitiesHtml.put("Ú", "&#218;");
        entitiesHtml.put("Ý", "&#221;");
        entitiesHtml.put("â", "&#226;");
        entitiesHtml.put("ê", "&#234;");
        entitiesHtml.put("î", "&#238;");
        entitiesHtml.put("ô", "&#244;");
        entitiesHtml.put("û", "&#251;");
        entitiesHtml.put("Â", "&#194;");
        entitiesHtml.put("Ê", "&#202;");
        entitiesHtml.put("Î", "&#206;");
        entitiesHtml.put("Ô", "&#212;");
        entitiesHtml.put("Û", "&#219;");
        entitiesHtml.put("ã", "&#227;");
        entitiesHtml.put("ñ", "&#241;");
        entitiesHtml.put("õ", "&#245;");
        entitiesHtml.put("Ã", "&#195;");
        entitiesHtml.put("Ñ", "&#209;");
        entitiesHtml.put("Õ", "&#213;");
        entitiesHtml.put("ä", "&#228;");
        entitiesHtml.put("ë", "&#235;");
        entitiesHtml.put("ï", "&#239;");
        entitiesHtml.put("ö", "&#246;");
        entitiesHtml.put("ü", "&#252;");
        entitiesHtml.put("ÿ", "&#255;");
        entitiesHtml.put("Ä", "&#196;");
        entitiesHtml.put("Ë", "&#203;");
        entitiesHtml.put("Ï", "&#207;");
        entitiesHtml.put("Ö", "&#214;");
        entitiesHtml.put("Ü", "&#220;");        
        entitiesHtml.put("ç", "&#231;");        

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
            case FORGOT:
                stream = factory.getClass().getClassLoader().getResourceAsStream("static/FORGOT-ACESS.html");
                break;
        }

        String theString = IOUtils.toString(stream, "UTF-8");

        return theString;

    }

    public static File loadLogo(String path)
            throws IOException {
        StreamFactory factory = StreamFactory.newInstance();
        InputStream initialStream = factory.getClass().getClassLoader().getResourceAsStream(path);

        File targetFile = File.createTempFile("logo", ".png");

        FileUtils.copyInputStreamToFile(initialStream, targetFile);
        return targetFile;
    }

    public static String generatePasswordRandom() {

        Random rd = new Random();

        int value = rd.nextInt(10);
        int value2 = rd.nextInt(10);
        return "%$" + value + "##!" + value2 + "+";

    }

    public static String getMimeType(File file) throws IOException {
        return tika.detect(file);
    }

    public static Optional<UserInfo> valueFromUserInfo(UserActiviti user, String key) {
        return user.getInfo()
                .stream()
                .filter(t -> t.getKey().equals(key))
                .findFirst();
    }

    public static boolean isMaster(UserActiviti user) {

        return user.getGroups()
                .stream()
                .filter(g -> g.getGroupId().equals(Constantes.PROFILE_MASTER) || g.getGroupId().equals(Constantes.PROFILE_BEHAVIOR_MASTER))
                .findFirst().isPresent();
    }

    public static String replaceAccentToEntityHtml(String value) {

        for (int i = 0; i < value.length(); i++) {
            String letter = Character.toString(value.charAt(i));
            
            if(entitiesHtml.containsKey(letter)){
                value = value.replace(letter, entitiesHtml.get(letter));
            }
        } 
        return value;
    }

}
