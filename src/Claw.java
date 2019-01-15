public class Claw extends Robot.Subsystem {
    public int clawPosition;
    public GamePiece item;

    public GamePiece GetGamePiece(){ return item; }
    public void SetGamePiece(GamePiece _piece){ this.item = _piece; }
    public void SetClawPosition(int _position){ this.clawPosition = _position; }
    public int GetClawPosition(){ return this.clawPosition; }

    public boolean CommandCheck(String[] _command){
        boolean temp=false;

        switch (_command[1]){
            case "grabPanel":
                if(GetGamePiece()==null && GetClawPosition()==2){
                    SetGamePiece(GamePiece.Panel);
                    temp = true;
                }
                else {
                    temp = false;
                }
                break;

            case "grabBall":
                if(GetGamePiece()==null && GetClawPosition()==1){
                    SetGamePiece(GamePiece.Ball);
                    temp = true;
                }
                else {
                    temp = false;
                }
                break;

            case "scoreBall":
                if(GetGamePiece()==GamePiece.Ball && GetClawPosition()==3){
                    SetGamePiece(null);
                    temp = true;
                }
                else {
                    temp = false;
                }

                break;
            case "scorePanel":
                if(GetGamePiece()==null && GetClawPosition()==2){
                    SetGamePiece(null);
                    temp = true;
                }
                else {
                    temp = false;
                }
                break;
            case "position1":
                SetClawPosition(1);
                break;
            case "position2":
                SetClawPosition(2);
                break;
            case "position3":
                SetClawPosition(3);
                break;
            case "position4":
                SetClawPosition(4);
                break;
        }
        return temp;
    }
}
