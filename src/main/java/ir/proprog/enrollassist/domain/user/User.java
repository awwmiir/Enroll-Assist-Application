package ir.proprog.enrollassist.domain.user;

import ir.proprog.enrollassist.domain.student.Student;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) // as required by JPA, don't use it in your code
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @OneToMany(cascade = CascadeType.MERGE)
    private Set<Student> students = new HashSet<>();

    @OneToMany
    private List<User> pending = new ArrayList<>();
    @OneToMany
    private List<User> requested = new ArrayList<>();
    @OneToMany
    private List<User> friends = new ArrayList<>();
    @OneToMany
    private List<User> blocked = new ArrayList<>();


    public User(String _name){
        if(_name == null)
            throw new IllegalArgumentException("Name can not be null.");
        if(_name.equals(""))
            throw new IllegalArgumentException("Name can not be empty.");
        name = _name;
    }

    public void addStudent(Student student) throws Exception {
        //Should this violation be checked?
        //Should all students of a user have the same name?
        //Should it also be equal to user's name?
        for(Student s : students)
            if(!s.getName().equals(student.getName()))
                throw new Exception("Student name can not be different.");
        students.add(student);
    }



    public void sendFriendshipRequest(User other) throws Exception {
        if (this.friends.contains(other))
            throw new Exception("This user is already your friend.");
        else if (this.requested.contains(other))
            throw new Exception("You requested to this user before.");
        else if (this.pending.contains(other))
            throw new Exception("This user requested first.");
        else if(this.blocked.contains(other))
            throw new Exception("You have blocked this user.");
        else if (this.equals(other))
            throw new Exception("You cannot send friendship request to yourself.");

        this.pending.add(other);
    }

    public void receiveFriendshipRequest(User other) throws Exception {
        if(this.blocked.contains(other))
            throw new Exception("You have been blocked by this user.");

        this.requested.add(other);
    }

    public void removeFriend(User other) throws Exception {
        if (this.requested.contains(other))
            this.requested.remove(other);
        else if (this.pending.contains(other))
            this.pending.remove(other);
        else if (this.friends.contains(other))
            this.friends.remove(other);
        else if (this.blocked.contains(other))
            this.blocked.remove(other);
        else
            throw new Exception("There is no relation between these user.");
    }

    public List<User> getAllFriends() {
        List<User> allFriends = new ArrayList<>();
        allFriends.addAll(this.friends);
        allFriends.addAll(this.requested);
        allFriends.addAll(this.blocked);
        allFriends.addAll(this.pending);
        return allFriends;
    }

    public void acceptRequest(User other) throws Exception {
        if (this.requested.contains(other)) {
            this.requested.remove(other);
            this.friends.add(other);
        }
        else
            throw new Exception("This user did not request to be your friend.");

    }

    public void addFriend(User other) {
        this.pending.remove(other);
        this.friends.add(other);
    }

    public User blockFriend(User other) throws Exception {
        if (this.friends.contains(other)) {
            this.friends.remove(other);
            this.blocked.add(other);
            return this;
        }
        else
            throw new Exception("This student is not your friend.");
    }

    public User unblockFriend(User other) throws Exception{
        if (this.blocked.contains(other)) {
            this.blocked.remove(other);
            return this;
        }
        else
            throw new Exception("This user is not blocked.");
    }

    public List<User> getFriendsWhoDoesntBlock() {
        List<User> friendStudents = new ArrayList<>();
        for (User s: this.friends)
            if (s.friends.contains(this))
                friendStudents.add(s);
        return friendStudents;
    }


}
