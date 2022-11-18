package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Meeting {
    private Long id;
    private String name;
    private String responsiblePerson;
    private String description;
    private Category category;
    private Type type;
    private Date startDate;
    private Date endDate;
    private List<User> participants;
}
