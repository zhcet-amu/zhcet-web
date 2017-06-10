package in.ac.amu.zhcet.data;

import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseLoader implements ApplicationRunner {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseLoader.class);

    @Autowired
    public DatabaseLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Student user = new Student("14PEB049", "password", "Areeb Jamal", "GF1032");
        userRepository.save(user);

        logger.info("Saved user " + user.toString());
    }
}
