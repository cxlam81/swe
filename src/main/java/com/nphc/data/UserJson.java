package com.nphc.data;

import java.util.Objects;

public class UserJson {
    private String id;
    private String login;
    private String employeeName;
    private String salary;
    private String startDate;

    public UserJson() {
    }

    public UserJson(String id, String login, String employeeName, String salary, String startDate) {
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

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
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
        UserJson user = (UserJson) o;
        return id.equals(user.id) && login.equals(user.login) && employeeName.equals(user.employeeName) && salary.equals(user.salary) && startDate.equals(user.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, employeeName, salary, startDate);
    }
}
