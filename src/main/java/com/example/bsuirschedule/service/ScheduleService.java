package com.example.bsuirschedule.service;

import com.example.bsuirschedule.model.Schedule;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ScheduleService {

    public Schedule getSchedule(String group, String date) {
        String apiUrl = String.format("https://iis.bsuir.by/api/v1/schedule?studentGroup=%s&date=%s", group, date);
        RestTemplate restTemplate = new RestTemplate();

        try {
            String response = restTemplate.getForObject(apiUrl, String.class);

            System.out.println("Response from API: " + response);  // Логируем весь JSON-ответ

            return parseSchedule(response, group, date);
        } catch (Exception e) {
            return new Schedule(group, date, List.of("Ошибка при получении расписания: " + e.getMessage()));
        }
    }

    private Schedule parseSchedule(String response, String group, String date) {
        List<String> subjects = new ArrayList<>();

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date requestedDate = dateFormat.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(requestedDate);

            String[] daysOfWeek = {"Воскресенье", "Понедельник", "Вторник", "Среда",
                    "Четверг", "Пятница", "Суббота"};
            String russianDayOfWeek = daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1];

            int weekNumber = ((calendar.get(Calendar.WEEK_OF_YEAR) - getSemesterStartWeek()) % 2) + 1;

            JSONObject jsonResponse = new JSONObject(response);
            JSONObject schedules = jsonResponse.getJSONObject("schedules");

            if (schedules.has(russianDayOfWeek)) {
                JSONArray lessons = schedules.getJSONArray(russianDayOfWeek);
                SimpleDateFormat apiDateFormat = new SimpleDateFormat("dd.MM.yyyy");

                for (int i = 0; i < lessons.length(); i++) {
                    JSONObject lesson = lessons.getJSONObject(i);

                    Date startDate = apiDateFormat.parse(lesson.getString("startLessonDate"));
                    Date endDate = apiDateFormat.parse(lesson.getString("endLessonDate"));

                    if (!requestedDate.before(startDate) && !requestedDate.after(endDate)) {
                        JSONArray weekNumbers = lesson.optJSONArray("weekNumber");
                        if (weekNumbers == null || containsWeek(weekNumbers, weekNumber)) {
                            String subjectInfo = String.format("%s - %s | %s | %s | %s",
                                    lesson.getString("startLessonTime"),
                                    lesson.getString("endLessonTime"),
                                    lesson.getString("subjectFullName"),
                                    lesson.getString("lessonTypeAbbrev"),
                                    lesson.getJSONArray("auditories").getString(0));

                            subjects.add(subjectInfo);
                        }
                    }
                }
            }

            if (subjects.isEmpty()) {
                subjects.add("Занятий на " + date + " (" + russianDayOfWeek + ") не найдено");
            }

        } catch (Exception e) {
            subjects.add("Ошибка обработки расписания: " + e.getMessage());
            e.printStackTrace();
        }

        return new Schedule(group, date, subjects);
    }

    private boolean containsWeek(JSONArray weekNumbers, int currentWeek) {
        for (int i = 0; i < weekNumbers.length(); i++) {
            if (weekNumbers.getInt(i) == currentWeek) {
                return true;
            }
        }
        return false;
    }

    private int getSemesterStartWeek() {
        return 5;
    }
}
