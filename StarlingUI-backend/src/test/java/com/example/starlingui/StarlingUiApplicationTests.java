package com.example.starlingui;

import com.example.starlingui.model.*;
import com.example.starlingui.service.TemplatingServiceImp;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
public class StarlingUiApplicationTests {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
    private Design design;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }
    
    public void setDesign(){
        design = new Design();
        design.setName("kube-system");
        List<Configuration> configList = new ArrayList<>();
        Configuration configuration = new Configuration();
        configuration.setName("deployment1");
        configuration.setId("1000");
        configuration.setKind("master");
        HashMap<String, String> label = new HashMap<>();
        label.put("app","starling");
        configuration.setLabel(label);
        Containers containers = new Containers();
        containers.setName("nanajanashia/k8s-demo-app:v1.0");
        containers.setCommand("");
        containers.setArgs("");
        List<Port> portList = new ArrayList<>();
        Port port = new Port();
        port.setContainerPort(3000);
        portList.add(port);
        containers.setPort(portList);
        List<Containers> containerList = new ArrayList<>();
        containerList.add(containers);
        configuration.setContainers(containerList);
        configList.add(configuration);

        design.setConfig(configList);

        List<Mapping> mappingList = new ArrayList<>();
        Mapping mapping = new Mapping();
        mapping.setNodeID("1000");
        ArrayList<String> mappedDrones = new ArrayList<>();
        mappedDrones.add("minikube");
        mapping.setMappedDrones(mappedDrones);
        mappingList.add(mapping);
        design.setMapping(mappingList);
    }



    @Test
    public void testRightUser() throws Exception {
        User user = new User("charaznablegundam", "362514hao");
        String testUser = new Gson().toJson(user);
        mockMvc.perform(post("/design/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testUser))      // 设置数据格式
                .andDo(print())     // 打印输出发出请求的详细信息
                .andExpect(status().isOk())     // 对返回值进行断言
                .andReturn().getResponse().getContentAsString();

    }

    @Test
    public void testWrongUser() throws Exception {
        User user = new User("charaznablegundam", "362514ao");
        String testUser = new Gson().toJson(user);
        mockMvc.perform(post("/design/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testUser))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

    }

    @Test
    public void testEmptyUser() throws Exception {
        User user = new User("OshiaCHEN", "4&PxDwTMns2YSa7");
        String testUser = new Gson().toJson(user);
        mockMvc.perform(post("/design/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testUser))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

    }
    
    @Test
    public void testTemplating() {
        try {
            TemplatingServiceImp templating = new TemplatingServiceImp();
            String message = templating.doTemplating(design);
            System.out.println(message);
        }catch(Exception e){
            System.out.println("Deploy fail :"+ e.getMessage());
        }
    }   
}
