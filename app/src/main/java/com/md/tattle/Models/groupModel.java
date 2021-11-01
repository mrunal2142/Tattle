package com.md.tattle.Models;

public class groupModel {

    String gId, gIcon, gNmae, gDes, gAdmin;

    public groupModel() {
    }

    public groupModel(String gId, String gIcon, String gNmae, String gDes, String gAdmin) {
        this.gId = gId;
        this.gIcon = gIcon;
        this.gNmae = gNmae;
        this.gDes = gDes;
        this.gAdmin = gAdmin;
    }

    public String getgId() {
        return gId;
    }

    public void setgId(String gId) {
        this.gId = gId;
    }

    public String getgIcon() {
        return gIcon;
    }

    public void setgIcon(String gIcon) {
        this.gIcon = gIcon;
    }

    public String getgNmae() {
        return gNmae;
    }

    public void setgNmae(String gNmae) {
        this.gNmae = gNmae;
    }

    public String getgDes() {
        return gDes;
    }

    public void setgDes(String gDes) {
        this.gDes = gDes;
    }

    public String getgAdmin() {
        return gAdmin;
    }

    public void setgAdmin(String gAdmin) {
        this.gAdmin = gAdmin;
    }
}
