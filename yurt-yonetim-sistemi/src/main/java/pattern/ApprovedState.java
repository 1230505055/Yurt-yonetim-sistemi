package pattern;

// İzin talebinin "Onaylandı" durumunu temsil eder
public class ApprovedState implements RequestState {

    @Override
    public String getStateName() {
        return "Onaylandı";
    }
}