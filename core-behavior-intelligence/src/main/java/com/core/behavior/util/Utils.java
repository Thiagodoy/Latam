package com.core.behavior.util;

import com.core.activiti.model.UserActiviti;
import com.core.activiti.model.UserInfo;
import com.core.behavior.annotations.PositionParameter;
import com.core.behavior.model.Log;
import com.core.behavior.model.Ticket;
import com.core.behavior.reader.BeanIoReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.Instant;
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
    private static SimpleDateFormat formmatDate2;
    private static Tika tika;
    private static final Map<String, String> entitiesHtml = new HashMap<String, String>();
    private static final Map<String, String> positionColumnByField = new HashMap<String, String>();
    
    public static final List<String> layoutMin = Arrays.asList("dataEmissao","dataEmbarque","horaEmbarque","ciaBilhete","trecho","origem","destino","cupom","bilhete","tipo","cabine","ciaVoo","valorBrl","empresa","cnpj","iataAgencia","baseVenda","qtdPax","numVoo","consolidada");
    public static String headerMinLayoutFile = "LINHA;DATA_EMISSAO;DATA_EMBARQUE;HORA_EMBARQUE;CIA_BILHETE;TRECHO;ORIGEM;DESTINO;CUPOM;BILHETE;TIPO;CABINE;CIA_VOO;VALOR_BRL;EMPRESA;CNPJ;IATA_AGENCIA;BASE_VENDA;QTD_PAX;NUM_VOO;CONSOLIDADA";
    public static enum TypeField {
        TICKET, LOG
    };

    static {

        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        formmatDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        formmatDate2 = new SimpleDateFormat("dd/MM/yyyy");

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
        
        
        positionColumnByField.put("dataEmissao","B");
        positionColumnByField.put("dataEmbarque","C");
        positionColumnByField.put("horaEmbarque","D");
        positionColumnByField.put("ciaBilhete","E");
        positionColumnByField.put("trecho","F");
        positionColumnByField.put("origem","G");
        positionColumnByField.put("destino","H");
        positionColumnByField.put("cupom","I");
        positionColumnByField.put("bilhete","J");
        positionColumnByField.put("tipo","L");
        positionColumnByField.put("cabine","K");
        positionColumnByField.put("ciaVoo","M");
        positionColumnByField.put("valorBrl","N");
        positionColumnByField.put("empresa","O");
        positionColumnByField.put("cnpj","P");
        positionColumnByField.put("iataAgencia","Q");
        positionColumnByField.put("baseVenda","R");
        positionColumnByField.put("qtdPax","S");
        positionColumnByField.put("numVoo","T");
        positionColumnByField.put("consolidada","U");

    }

    public static  String getPositionExcelColumn(String field){
        return positionColumnByField.get(field);
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
    
    public static boolean isEmpty(File file){
        
        long count = 0;
        FileReader reader = null;
        LineNumberReader readerLine = null;
        boolean isEmpty = true;
        try {
             reader = new FileReader(file);
             readerLine = new LineNumberReader(reader);

            while (readerLine.readLine() != null) {
                count++;
            }           

            --count;
            return count <= 0 ? isEmpty : !isEmpty;
            

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            return isEmpty;
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            return isEmpty;
        }finally{
            try {
                readerLine.close();
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(BeanIoReader.class.getName()).log(Level.SEVERE, null, ex);
            }
           
        }
    }
    
    public static boolean isLayoutMin(String atribute, long layout){       
        
        if(layout == 1){
            return layoutMin.contains(atribute);            
        }
         return true;
        
    }
    
    
    public static String mapToString(Map ma){
        try {
            return  new ObjectMapper().writeValueAsString(ma);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    
    public static Map<String,String>toMap(String value) throws IOException{
        return  new ObjectMapper().readValue(value, HashMap.class);
    }
    
   public static LocalDateTime dateToLocalDateTime(Date date){
       return Instant.ofEpochMilli(date.getTime())
      .atZone(ZoneId.systemDefault())
      .toLocalDateTime();    
   }
   
   public static String formatDate(Date date){
       return formmatDate2.format(date);
   }
    
    
}
