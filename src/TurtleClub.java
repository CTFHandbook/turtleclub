
import java.util.ArrayList;

public class TurtleClub implements Robot {

    Battery battery = new Battery();
    Controller controller = new Controller();

    public TurtleClub(){

    }
    public int Init(int x){
        return 1;
    }

    public void Shutdown(){
        System.out.println("Robot Shutdown");

    }

    public boolean[] Status(){
        boolean[] temp = {true, false};
        return temp;
    }


}

