package pattern;

// İzin talebinin "Reddedildi" durumunu temsil eder
public class RejectedState implements RequestState {

    @Override
    public String getStateName() {
        return "Reddedildi";
    }
}