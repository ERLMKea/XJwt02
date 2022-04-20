package com.example.xjwt02.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MyHomeController {

    @RequestMapping("/")
    public String getIndex(Model model) {
        return "index1.html";
    }

}
