package ca.nrc.cadc.doi.search;

import ca.nrc.cadc.doi.status.Status;

import java.util.ArrayList;
import java.util.List;

public class DoiSearchFilter {
    private Role role;
    private List<Status> statusList = new ArrayList<>();

    public DoiSearchFilter() {
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Status> getStatusList() {
        return this.statusList;
    }

    public void setStatusList(List<Status> statusList) {
        this.statusList = statusList;
    }

    public void prepareStatusList(List<String> statusList) {
        for (String status : statusList) {
            this.statusList.add(Status.toValue(status));
        }
    }

}
