package com.example;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.catalina.filters.RequestDumperFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableOAuth2Sso
public class UiApplication {

    @Controller
    static class HomeController {
        @Autowired
        OAuth2RestTemplate restTemplate;
        @Value("${messages.url:http://localhost:9999}/secure/")
        String messagesUrl;

        @RequestMapping("/")
        @ResponseBody
        Object home(Model model) {
//            List<Message> messages = Arrays.asList(restTemplate.getForObject(messagesUrl + "/messages", Message[].class));
//            model.addAttribute("messages", messages);
//            return "index";
         return    restTemplate.getForObject("http://localhost:8080/api/users", String.class);
        }

        @RequestMapping(path = "messages", method = RequestMethod.POST)
        String postMessages(@RequestParam String text) {
            Message message = new Message();
            message.text = text;
            restTemplate.exchange(RequestEntity
                    .post(UriComponentsBuilder.fromHttpUrl(messagesUrl).pathSegment("messages").build().toUri())
                    .body(message), Message.class);
            return "redirect:/";
        }
    }

    public static class Message {
        public String text;
        public String username;
        public LocalDateTime createdAt;
    }


    public static void main(String[] args) {
        SpringApplication.run(UiApplication.class, args);
    }


    @Bean
    OAuth2RestTemplate oauth2RestTemplate(OAuth2ClientContext oauth2ClientContext, OAuth2ProtectedResourceDetails details) {
        return new OAuth2RestTemplate(details, oauth2ClientContext);
    }
}
