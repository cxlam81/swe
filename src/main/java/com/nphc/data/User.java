package com.nphc.data;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "employee")
public class User {
    @Id
    private String id;

    private String login;

    @Column(name = "employee_name")
    private String employeeName;

    private Float salary;

    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    public User() {
    }

    public User(String id, String login, String employeeName, Float salary, Date startDate) {
        this.id = id;
        this.login = login;
        this.employeeName = employeeName;
        this.salary = salary;
        this.startDate = startDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Float getSalary() {
        return salary;
    }

    public void setSalary(Float salary) {
        this.salary = salary;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return "User{" +
                "employeeID='" + id + '\'' +
                ", login='" + login + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", salary=" + salary +
                ", startDate=" + startDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) && login.equals(user.login) && employeeName.equals(user.employeeName) && salary.equals(user.salary) && startDate.equals(user.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, employeeName, salary, startDate);
    }
}
