package pt.ulisboa.ist.sirs.project.securesmarthome.gateway;

/**
 * Created by Alex Anders on 30/11/2016.
 */
public class authenticatedSHD {
    public boolean isAuthenticated() {
        return authenticated;
    }

    public authenticatedSHD(boolean authenticated) {
        this.authenticated = authenticated;
    }

    private boolean authenticated;
}
