package pt.ulisboa.ist.sirs.project.securesmarthome.gateway;

/**
 * Created by Alex Anders on 30/11/2016.
 */
public class AuthenticatedSHD {
    public boolean isAuthenticated() {
        return authenticated;
    }

    public AuthenticatedSHD(boolean authenticated) {
        this.authenticated = authenticated;
    }

    private boolean authenticated;
}
