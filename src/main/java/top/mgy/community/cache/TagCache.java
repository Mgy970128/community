package top.mgy.community.cache;

import org.apache.commons.lang3.StringUtils;
import top.mgy.community.dto.TagDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TagCache {
    public static List<TagDTO> get() {
        ArrayList<TagDTO> list = new ArrayList<>();
        TagDTO program = new TagDTO();
        program.setCategoryName("开发语言");
        program.setTags(Arrays.asList("Java", "JavaScript", "PHP", "html5", "node.js", "python", "c++", "c", "go", "objective-c",
                "TypeScript", "shell", "swift", "c#", "sass", "ruby", "bash", "less", "ASP.NET", "lua", "scala", "perl"));
        list.add(program);
        TagDTO framework = new TagDTO();
        framework.setCategoryName("平台框架");
        framework.setTags(Arrays.asList("Spring","Vue","React","Angular"));
        list.add(framework);
        TagDTO bigData = new TagDTO();
        bigData.setCategoryName("大数据");
        bigData.setTags(Arrays.asList("Hadoop","MapReduce","Yarn","HDFS","Hive","Spark","Pig","Storm","Flink",
                "ZooKeeper","Hbase","Flume","Sqoop","kettle","Ozzie","Azkaban","CDH"));
        list.add(bigData);
        TagDTO server = new TagDTO();
        server.setCategoryName("服务器");
        server.setTags(Arrays.asList("Liunx","Nginx","Docker","Apache","Ubuntu","CentosOS","Cache","Tomcat",
                "负载均衡","Unix","windows-sever"));
        list.add(server);
        TagDTO dataBase = new TagDTO();
        dataBase.setCategoryName("数据库");
        dataBase.setTags(Arrays.asList("Mysql","Redis","SQL","Oracle","Mongodb","NoSQL","Memcached","SQLServer",
                "Postgresql","SQLite","DB2","H2"));
        list.add(dataBase);
        TagDTO tool = new TagDTO();
        tool.setCategoryName("开发工具");
        tool.setTags(Arrays.asList("git","github","VS-code","vim","sublime-text","xcode","IDEA","eclipse",
                "maven","ide","VS","atom","textmate"));
        list.add(tool);

        return list;
    }

    public static String FilterInvalid(String tags){
        String[] split = StringUtils.split(tags, ",");
        List<TagDTO> tagDTOS = get();

        List<String> tagList = tagDTOS.stream().flatMap(tag -> tag.getTags().stream()).collect(Collectors.toList());
        String invalid = Arrays.stream(split).filter(t -> !tagList.contains(t)).collect(Collectors.joining(","));
        return invalid;
    }
}
