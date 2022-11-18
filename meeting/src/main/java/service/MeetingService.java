package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Meeting;
import model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class MeetingService {

    private List<Meeting> meetingList = new ArrayList<>();
    String file = "meetings.json";
    @Autowired
    UserService userService;
    public static String readFileAsString(String file) throws Exception {
        return new String(Files.readAllBytes(Paths.get(file)));
    }

    @PostConstruct
    public void createMeetings() {
        var gson = new Gson();
        Gson gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'hh:mm:ss").create();
        try {
            String json = readFileAsString(file);
            JSONObject root = new JSONObject(json);
            JSONArray meetings = root.getJSONArray("meetings");
            for (int i = 0; i < meetings.length(); i++) {
                JSONObject jsonMeeting = meetings.getJSONObject(i);
                var meeting = gsonBuilder.fromJson(jsonMeeting.toString(), Meeting.class);

                meetingList.add(meeting);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveToJSON() throws IOException {
        //does not work
        Gson gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'hh:mm:ss").create();
        FileWriter fileWriter = new FileWriter("meetings.json");
        fileWriter.flush();

        gsonBuilder.toJson(meetingList, new FileWriter(file));
    }

    public String createMeeting(Meeting meeting) throws IOException {
        meetingList.add(meeting);
        return meeting.getName() + " successfully created";
    }

    public List<Meeting> getMeetingList() throws JSONException {
        return meetingList;
    }

    public String deleteMeeting(String meetingName, String fullName){
        for (int i = 0; i < meetingList.size(); i++) {
            if (meetingList.get(i).getName().equals(meetingName) && meetingList.get(i).getResponsiblePerson().equals(fullName)) {
                meetingList.remove(i);
                return "Meeting " + meetingName + " is deleted by " + fullName;
            }
        }
        return "Incorrect meeting name or responsible person";
    }

    public String addPersonToMeeting(String meetingName, String fullName){
        User person = userService.getUserByFullName(fullName);

        for (int i = 0; i < meetingList.size(); i++) {
            if (meetingList.get(i).getName().equals(meetingName)) {
                if(checkIfPersonIsInMeeting(person, i))
                    return "Person is already in the meeting list";
                else if (checkForPersonAvailability(person, meetingList.get(i)))
                    return person.getFullName() + " is already in a meeting at that time";
                else
                    meetingList.get(i).getParticipants().add(person);
                return person.getFullName() + " is added to the meeting at "+ LocalDateTime.now().toString();
            }
        }
        return null;
    }

    public String deletePersonFromMeeting(String meetingName, User person){
        for (int i = 0; i < meetingList.size(); i++) {
            if (meetingList.get(i).getName().equals(meetingName)) {
                if (checkIfPersonIsInMeeting(person, i) && !meetingList.get(i).getResponsiblePerson().equals(person.getFullName())) {
                    meetingList.get(i).getParticipants().remove(person);
                    return person.getFullName() + " is removed from the meeting";
                }
            }
        }
        return person.getFullName() + " is not in the " + meetingName + " meeting";
    }

    public Boolean checkIfPersonIsInMeeting(User person, int meetingIndex){
        for (int i = 0; i < meetingList.get(meetingIndex).getParticipants().size(); i++){
            if (meetingList.get(meetingIndex).getParticipants().get(i).getFullName() == person.getFullName())
                return true;
        }
        return false;
    }

    public Boolean checkForPersonAvailability(User person, Meeting meeting){
        for (int i = 0; i < meetingList.size(); i++){
            if (checkIfPersonIsInMeeting(person, i))
                if (meetingList.get(i).getEndDate().compareTo(meeting.getStartDate()) < 0 || (meetingList.get(i).getStartDate().compareTo(meeting.getStartDate()) > 0 && meetingList.get(i).getStartDate().compareTo(meeting.getEndDate()) > 0))
                    return true;
        }
        return false;
    }
}
