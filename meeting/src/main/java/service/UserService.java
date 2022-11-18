package service;

import com.google.gson.Gson;
import model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private List<User> userList = new ArrayList<User>();

    public static String readFileAsString(String file) throws Exception {
        return new String(Files.readAllBytes(Paths.get(file)));
    }

    @PostConstruct
    public void createUsers() {
        var gson = new Gson();

        String file = "users_json.json";
        try {
            String json = readFileAsString(file);
            JSONObject root = new JSONObject(json);
            JSONArray users = root.getJSONArray("users");
            for (int i = 0; i < users.length(); i++) {
                JSONObject jsonUser = users.getJSONObject(i);
                var user = gson.fromJson(jsonUser.toString(), User.class);
                userList.add(user);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> getUsers() throws JSONException {
        return userList;
    }

    public User getUserByFullName(String fullName){
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getFullName().equals(fullName)) {
                return userList.get(i);
            }
        }
        return null;
    }
}
