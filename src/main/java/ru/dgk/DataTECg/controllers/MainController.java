package ru.dgk.DataTECg.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.dgk.DataTECg.repositorys.TMRepository;
import ru.dgk.DataTECg.services.TMDataService;

import java.util.Map;


@Controller
public class MainController {

    final TMRepository tmRepository;
    final TMDataService tmDataService;

    public MainController(TMRepository tmRepository, TMDataService tmDataService) {
        this.tmRepository = tmRepository;
        this.tmDataService = tmDataService;
    }

    @GetMapping(path = "/")
    public String mainPage(Model model, @RequestParam Map<String,String> allParams) {
        System.out.println(allParams);
        if(allParams.size()!=0){
            allParams.keySet().forEach(k -> System.out.println((k + ":" + allParams.get(k))));
            model.addAllAttributes(tmDataService.getDatasets(allParams));
        }

        model.addAttribute("HMs", tmDataService.getHM());

//        model.addAllAttributes(tmDataService.getData());
        return "charts";
    }
}