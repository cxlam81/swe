package com.nphc.rest;

import com.google.gson.Gson;
import com.nphc.data.User;
import com.nphc.data.UserJson;
import com.nphc.data.repository.UserRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.ws.ResponseWrapper;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
public class SweRestController {
    private final Path fileStorageLocation = Paths.get("");

    //prepare Date patterns
    String[] patterns = {"yy-MMM-dd", "yyyy-MM-dd"};

    @Autowired
    UserRepository userRepository;

    @PostMapping("users/upload")
    @ResponseWrapper(targetNamespace = "message")
    public ResponseEntity<String> uploadUser(@RequestParam(value = "file") MultipartFile file)
    {
        // Normalize file name
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileName = "";

        try {
            //Validate file name for invalid characters
            if(originalFileName.contains("..")){
                return new ResponseEntity("Invalid filename"+originalFileName, HttpStatus.BAD_REQUEST);
            }

            //Accept only CSV
            String fileExtension = "";
            try{
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".")+1);
            }catch (Exception e){
                return new ResponseEntity("Unkown error "+e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            if (!fileExtension.equalsIgnoreCase("csv")){
                //("Invalid file type "+originalFileName);
                return new ResponseEntity("Invalid file type "+originalFileName, HttpStatus.BAD_REQUEST);
            }
            try{
                //parse CSV
                Reader reader = new InputStreamReader(file.getInputStream());
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                        .withFirstRecordAsHeader());
                List<User> userList = new ArrayList<>();
                // id,login,name,salary,startDate
                csvParser.getRecords().stream().forEach( r ->
                        {
                            try {
                                userList.add(new User(r.get("id"), r.get("login"), r.get("name"),
                                        Float.parseFloat(r.get("salary")), DateUtils.parseDate(r.get("startDate"), patterns)));
                            } catch (ParseException parseException) {
                                parseException.printStackTrace();
                            }
                        }
                );
                Map<String, User> currentUsers = userRepository.findAll().stream()
                        .collect(Collectors.toMap(User::getId, Function.identity()));
                Map<String, User> updatedUsers = userRepository.saveAll(userList).stream()
                        .collect(Collectors.toMap(User::getId, Function.identity()));
                if (updatedUsers.equals(currentUsers)){
                    return new ResponseEntity(HttpStatus.OK);
                }
                else{
                    return new ResponseEntity("Records created", HttpStatus.CREATED);
                }
            }catch(Exception e){
                return new ResponseEntity("message"+e.getMessage(), HttpStatus.BAD_REQUEST);
            }

        }catch (Exception ex) {
            return new ResponseEntity("Exception encountered", HttpStatus.BAD_REQUEST);
        }

        //return new ResponseEntity("message", HttpStatus.CREATED);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUserList(){
        return new ResponseEntity<List<User>>(userRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserWithID(@PathVariable(name = "id") String employeeID){
        Optional<User> opt = userRepository.findById(employeeID);
        if (opt.isPresent()) {
            return new ResponseEntity<User>(opt.get(), HttpStatus.OK);
        }
        else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/users")
    @ResponseBody
    public ResponseEntity<String> createUser(@RequestBody UserJson user){
        Optional<User> opt = userRepository.findById(user.getId());
        if (opt.isPresent()){
            return new ResponseEntity("Employee ID already exists", HttpStatus.BAD_REQUEST);
        }
        else{
            //check login
            opt = userRepository.findUserByLogin(user.getLogin());
            if (opt.isPresent()){
                return new ResponseEntity("Employee login not unique", HttpStatus.BAD_REQUEST);
            }
        }
        try {
            Float salary = Float.parseFloat(user.getSalary());
            if (salary < 0){
                return new ResponseEntity("Invalid salary", HttpStatus.BAD_REQUEST);
            }
            Date startDate = DateUtils.parseDate(user.getStartDate(), patterns);
            userRepository.save(
                    new User(user.getId(), user.getLogin(), user.getEmployeeName(), salary, startDate)
            );
            return new ResponseEntity("Successfully created", HttpStatus.CREATED);
        }catch (NumberFormatException numFormatException){
            return new ResponseEntity("Invalid salary", HttpStatus.BAD_REQUEST);
        }catch (ParseException dateException){
            return new ResponseEntity("Invalid date", HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/users/{id}")
    @PatchMapping("/users/{id}")
    @ResponseBody
    public ResponseEntity<String> updateUser(@PathVariable(name = "id") String employeeID, @RequestBody UserJson user){
        Optional<User> opt = userRepository.findById(employeeID);
        if (opt.isPresent()){
            //check login if updated
            boolean loginUpdated = true;
            if (opt.get().getLogin().equalsIgnoreCase(user.getLogin())){
                loginUpdated = false;
            }
            Optional<User> findUserWithNewLogin = userRepository.findUserByLogin(user.getLogin());
            if (loginUpdated && findUserWithNewLogin.isPresent()){
                return new ResponseEntity("Employee new login is not unique", HttpStatus.BAD_REQUEST);
            }
            try {
                Float salary = Float.parseFloat(user.getSalary());
                if (salary < 0){
                    return new ResponseEntity("Invalid salary", HttpStatus.BAD_REQUEST);
                }
                Date startDate = DateUtils.parseDate(user.getStartDate(), patterns);
                User dbUser = opt.get();
                dbUser.setLogin(user.getLogin());
                dbUser.setEmployeeName(user.getEmployeeName());
                dbUser.setSalary(salary);
                dbUser.setStartDate(startDate);
                userRepository.save(dbUser);
                return new ResponseEntity("Successfully updated", HttpStatus.OK);
            }catch (NumberFormatException numFormatException){
                return new ResponseEntity("Invalid salary", HttpStatus.BAD_REQUEST);
            }catch (ParseException dateException){
                return new ResponseEntity("Invalid date", HttpStatus.BAD_REQUEST);
            }catch (Exception e){
                return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        else{
            return new ResponseEntity("No such employee", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable(name = "id") String employeeID){
        Optional<User> opt = userRepository.findById(employeeID);
        if (opt.isPresent()){
            userRepository.delete(opt.get());
            return new ResponseEntity("Successfully deleted", HttpStatus.OK);
        }
        else{
            return new ResponseEntity("No such employee", HttpStatus.BAD_REQUEST);
        }
    }
}
