# Springboot ELK(logback->redis->logstash->ES)完整记录

##项目安装： Redis、logstash、ES、kibana安装请参照官方文档



##项目Springboot版本：
~~~
<parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.5.RELEASE</version>
 </parent>

<spring-cloud.version>Greenwich.SR1</spring-cloud.version>
~~~
## pom.xml引入
~~~
    <dependency>
            <groupId>com.cwbase</groupId>
            <artifactId>logback-redis-appender</artifactId>
            <version>1.1.5</version>
    </dependency>
~~~


logback.xml，此文件放在sources下面

~~~
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="false">

    <appender name="STDOUT"
        class="ch.qos.logback.core.ConsoleAppender">
        <encoder
            class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%date [%10thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="FILE"
        class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/backend.log</file>
        <rollingPolicy
            class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/backend.%d{yyyy-MM-dd}.log.zip
            </fileNamePattern>
            <maxHistory>1</maxHistory>
        </rollingPolicy>
        <encoder
            class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %C.%M:%L -
                %m%n</pattern>
        </encoder>
    </appender>

    <!-- <appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender"> 
        <destination>127.0.0.1:9250</destination> <encoder charset="UTF-8" class="net.logstash.logback.encoder.LogstashEncoder" 
        /> </appender> -->

    <appender name="LOGSTASH"
        class="com.cwbase.logback.RedisAppender">
        <source>user_common_service</source>
        <sourcePath>node1</sourcePath>
        <type>Service</type>
        <tags>production</tags>
        <host>127.0.0.1</host>
        <port>6379</port>
        <key>logstash</key>
        <additionalField>
            <key>teamName</key>
            <value>mobile_app</value>
        </additionalField>
    </appender>

    <root level="INFO">
        <appender-ref ref="STDOUT" />
        <!--<appender-ref ref="FILE" /> -->
        <appender-ref ref="LOGSTASH" />
    </root>

</configuration>
~~~


### 注意下面几个关键点：

        <source>user_common_service</source>   ####  可以配置应用名称，后面按这个名称创建ES的索引，注意是小写
        <sourcePath>node1</sourcePath>
        <type>Service</type>
        <tags>production</tags>
        <host>127.0.0.1</host>  ###redis的地址
        <port>6379</port>    ###redis的端口
        <key>logstash</key>
        <additionalField>    ###可以自定义属性！！
            <key>teamName</key>
            <value>mobile_app</value>
        </additionalField>



## logstah的配置：

~~~
input {
 redis {
  codec => json
  host => "192.168.1.98"
  port => 6379
  key => "logstash"
  data_type => "list"
 }
}

filter {
} 
output {
    elasticsearch {
    index => "log-%{[source]}-%{+YYYY.MM.dd}"
    hosts => ["192.168.1.98:9200"]
    }
    stdout {codec => rubydebug}
}
~~~
index的创建方式，"log-%{[source]}-%{+YYYY.MM.dd}"   这里按照应用源及日期创建一个索引，按自己需要定义

## kibana中 Create index pattern









修改elasticsearch的shards

curl -XPUT 'http://172.28.161.90:19200/_template/logstash-*' -H 'Content-Type: application/json' -d'{
    "index_patterns" : ["*"],
    "order" : 0,
    "settings" : {
        "number_of_shards" : 5
    }
}'



