package com.core.behavior;

import com.core.behavior.dto.LogDTO;
import com.core.behavior.properties.BehaviorProperties;
import com.core.behavior.services.IntegrationService;
import com.core.behavior.services.TicketService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SparkTest {
    
    @Autowired
    private TicketService service;
    
    
    
    @Autowired
    private IntegrationService integrationService;

//    private SparkSession sparkSession;
//
//    private JavaSparkContext javaSparkContext;
    
    @Autowired
    private BehaviorProperties behaviorProperties;
    
    
   // @Test
    public void verifyCupom() throws SchedulerException, InterruptedException, Exception {
        
//        javaSparkContext = new JavaSparkContext(new SparkConf()
//                .setAppName("SparkJdbcDs")
//                .setMaster("local[*]")
//        .set("spark.memory.fraction", "0.25")
//        //.set("spark.memory.storageFraction", "0.20")
//        );            
//            
//            sparkSession = SparkSession
//                    .builder()
//                    .sparkContext(javaSparkContext.sc())                    
//                    .appName("Behavior Sql Log")
//                    .getOrCreate();
//            
//            
//            
//
//            Dataset<Row> jdbcDF = sparkSession.sqlContext().read()
//                    .format("jdbc")
//                    .option("url", behaviorProperties.getDatasource().getSqlserver().getDataUrl())
//                    .option("dbtable", "behavior.log")
//                    //.option("numPartitions", "10")
//                    .option("user", behaviorProperties.getDatasource().getSqlserver().getDataSourceUser())
//                    .option("password", behaviorProperties.getDatasource().getSqlserver().getDataSourcePassword())
//                    .load();
//            
//            Dataset<LogDTO> logs = jdbcDF.as(Encoders.bean(LogDTO.class));
//            logs.count();
//            sparkSession.close();
//            javaSparkContext.close();
         
         
    }
}
