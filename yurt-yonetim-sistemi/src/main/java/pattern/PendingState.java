package pattern;

// İzin talebinin "Beklemede" durumunu temsil eder (Varsayılan durum)
public class PendingState implements RequestState {

    @Override
    public String getStateName() {
        return "Beklemede";
    }
}