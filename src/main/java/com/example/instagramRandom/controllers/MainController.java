package com.example.instagramRandom.controllers;

import com.example.instagramRandom.domains.ClientQuery;
import com.example.instagramRandom.domains.InstaPhoto;
import com.example.instagramRandom.repos.InstaPhotoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    private HashSet<Object> usernames = new HashSet<>();
    private ClientQuery clientQuery = new ClientQuery();
    private String winner = "";
    private String errorMessage = "";
    private boolean isApplied = false;

    @Autowired
    private InstaPhotoRepo instaPhotoRepo;


    @GetMapping("/")
    public String main(Map<String, Object> model) {
        model.put("usernames",usernames);
        model.put("winner",winner);
        model.put("errorMessage",errorMessage);
        model.put("isApplied",isApplied);

        return "index";
    }

    @PostMapping("/")
    public String add(@RequestParam String shortcode){
        errorMessage = "";
        isApplied = false;
        usernames.clear();
        int errorChange = 0;

        try {
            try {
                List<InstaPhoto> instaPhoto = instaPhotoRepo.findByShortcode(shortcode);

                if(instaPhoto.size() > 0 ){
                    throw new Exception();
                }

            }catch (Exception exception){
                errorMessage = "Уже было проведено!";
                throw new Exception();
            }

            InstaPhoto instaPhoto = new InstaPhoto();

            clientQuery.setShortcode(shortcode);
            clientQuery.setUsernames(usernames);
            String query = clientQuery.doRequestToInst(clientQuery.linkBuilderByShortCode());


            try {
                clientQuery.getUsernamesFromComments(query);
                if(usernames.size() == 1){
                    usernames.clear();
                    errorChange = 1;
                    throw new Exception();


                }else if (usernames.size() == 0){
                    errorChange = 2;
                    throw  new Exception();
                }
            }catch (Exception exception){
                if (errorChange == 1){
                    errorMessage = "Один участник не может участвовать в конкурсе!";
                } else if (errorChange == 2){
                    errorMessage = "Конкурс не может проводиться без участников!";
                } else {
                    errorMessage = "Ошибка ввода. Попробуйте ввести shortcode фото еще раз!";
                }
                throw new Exception();

            }
            winner = clientQuery.getLuckyFollower();
            instaPhoto.setShortcode(shortcode);
            instaPhoto.setUsername(winner);
            instaPhotoRepo.save(instaPhoto);

            isApplied = true;
            return "redirect:/";


        }catch (Exception exception) {
            return "redirect:/";
        }



    }

}
