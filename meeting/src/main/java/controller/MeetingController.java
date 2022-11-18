package controller;

import model.Meeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.MeetingService;

import java.io.IOException;
import java.util.List;

@RestController
public class MeetingController {

    @Autowired
    private MeetingService service;

    @GetMapping("/meetings")
    public List<Meeting> findAllMeetings() {
        return service.getMeetingList();
    }

    @DeleteMapping("/deleteMeeting/{meetingName}/{fullName}")
    public String deleteMeeting(@PathVariable String meetingName, @PathVariable String fullName) {
        return service.deleteMeeting(meetingName, fullName);
    }

    @PostMapping("/addperson/{name}/{fullName}")
    public String addPersonToMeeting(@PathVariable String name, @PathVariable String fullName){
        return service.addPersonToMeeting(name, fullName);
    }

    @PostMapping("/meeting")
    public String createMeeting(@RequestBody Meeting meeting) throws IOException {
        return service.createMeeting(meeting);
    }
}
