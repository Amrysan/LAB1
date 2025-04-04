package com.example.bsuirschedule.controller;

import com.example.bsuirschedule.model.Schedule;
import com.example.bsuirschedule.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/schedule")
    public Schedule getSchedule(@RequestParam String group, @RequestParam String date) {
        return scheduleService.getSchedule(group, date);
    }
}
