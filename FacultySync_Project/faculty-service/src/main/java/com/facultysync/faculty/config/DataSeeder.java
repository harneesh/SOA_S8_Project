package com.facultysync.faculty.config;

import com.facultysync.faculty.entity.Faculty;
import com.facultysync.faculty.entity.LeaveRequest;
import com.facultysync.faculty.entity.TimetableSlot;
import com.facultysync.faculty.repository.FacultyRepository;
import com.facultysync.faculty.repository.LeaveRepository;
import com.facultysync.faculty.repository.TimetableRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static java.time.DayOfWeek.*;

/** Inserts sample data into MySQL the first time the service starts (only when the faculty table is empty). */
@Component
public class DataSeeder implements CommandLineRunner {

    private final FacultyRepository facultyRepo;
    private final TimetableRepository timetableRepo;
    private final LeaveRepository leaveRepo;

    public DataSeeder(FacultyRepository facultyRepo, TimetableRepository timetableRepo, LeaveRepository leaveRepo) {
        this.facultyRepo = facultyRepo;
        this.timetableRepo = timetableRepo;
        this.leaveRepo = leaveRepo;
    }

    @Override
    public void run(String... args) {
        if (facultyRepo.count() > 0) {
            return;
        }
        List<Faculty> f = facultyRepo.saveAll(List.of(
                new Faculty("Dr. Anil Kumar", "CSE", "anil@college.edu", "Professor & HOD",
                        List.of("Data Structures", "Algorithms")),
                new Faculty("Prof. Priya Sharma", "CSE", "priya@college.edu", "Assistant Professor",
                        List.of("Data Structures", "DBMS")),
                new Faculty("Prof. Rahul Verma", "CSE", "rahul@college.edu", "Assistant Professor",
                        List.of("Operating Systems", "Computer Networks")),
                new Faculty("Prof. Sneha Iyer", "CSE", "sneha@college.edu", "Associate Professor",
                        List.of("DBMS", "Web Technologies")),
                new Faculty("Prof. Karthik Rao", "CSE", "karthik@college.edu", "Assistant Professor",
                        List.of("Computer Networks", "Operating Systems", "SOA & Microservices")),
                new Faculty("Prof. Meena Das", "CSE", "meena@college.edu", "Assistant Professor",
                        List.of("SOA & Microservices", "Web Technologies", "Java Programming")),
                new Faculty("Dr. Suresh Nair", "Mathematics", "suresh@college.edu", "Professor",
                        List.of("Discrete Mathematics", "Probability & Statistics")),
                new Faculty("Prof. Fatima Khan", "CSE", "fatima@college.edu", "Assistant Professor",
                        List.of("Algorithms", "Java Programming", "Data Structures"))));
        int anil = f.get(0).getId(), priya = f.get(1).getId(), rahul = f.get(2).getId(), sneha = f.get(3).getId(),
                karthik = f.get(4).getId(), meena = f.get(5).getId(), suresh = f.get(6).getId(), fatima = f.get(7).getId();

        timetableRepo.saveAll(List.of(
                new TimetableSlot(anil, MONDAY, 1, "Algorithms", "CSE-3A", "101"),
                new TimetableSlot(anil, WEDNESDAY, 3, "Data Structures", "CSE-2B", "102"),
                new TimetableSlot(priya, MONDAY, 2, "Data Structures", "CSE-2A", "101"),
                new TimetableSlot(priya, TUESDAY, 1, "DBMS", "CSE-3A", "103"),
                new TimetableSlot(priya, TUESDAY, 4, "Data Structures", "CSE-2A", "101"),
                new TimetableSlot(priya, THURSDAY, 2, "DBMS", "CSE-3B", "103"),
                new TimetableSlot(rahul, MONDAY, 2, "Operating Systems", "CSE-3A", "104"),
                new TimetableSlot(rahul, TUESDAY, 3, "Computer Networks", "CSE-3B", "104"),
                new TimetableSlot(rahul, FRIDAY, 1, "Operating Systems", "CSE-3B", "104"),
                new TimetableSlot(sneha, MONDAY, 3, "DBMS", "CSE-3B", "103"),
                new TimetableSlot(sneha, TUESDAY, 1, "Web Technologies", "CSE-4A", "201"),
                new TimetableSlot(sneha, WEDNESDAY, 2, "DBMS", "CSE-3A", "103"),
                new TimetableSlot(karthik, MONDAY, 4, "SOA & Microservices", "CSE-4A", "202"),
                new TimetableSlot(karthik, TUESDAY, 2, "Computer Networks", "CSE-3A", "104"),
                new TimetableSlot(karthik, THURSDAY, 1, "SOA & Microservices", "CSE-4B", "202"),
                new TimetableSlot(karthik, FRIDAY, 3, "Operating Systems", "CSE-3A", "104"),
                new TimetableSlot(meena, MONDAY, 1, "Java Programming", "CSE-2B", "201"),
                new TimetableSlot(meena, MONDAY, 5, "SOA & Microservices", "CSE-4B", "202"),
                new TimetableSlot(meena, WEDNESDAY, 4, "Web Technologies", "CSE-4A", "201"),
                new TimetableSlot(suresh, MONDAY, 2, "Discrete Mathematics", "CSE-2B", "301"),
                new TimetableSlot(suresh, TUESDAY, 4, "Probability & Statistics", "CSE-3B", "301"),
                new TimetableSlot(fatima, TUESDAY, 2, "Algorithms", "CSE-3B", "102"),
                new TimetableSlot(fatima, THURSDAY, 3, "Java Programming", "CSE-2A", "201")));

        // A sample pending leave for next Monday-Tuesday so the demo works on any date
        LocalDate nextMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        leaveRepo.save(new LeaveRequest(priya, nextMonday, nextMonday.plusDays(1), "Attending a conference"));
    }
}
