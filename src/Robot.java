import java.util.*;

public interface Robot {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Description: A class to represent a subsystem For example, wheels, claw ect... A robot can have any amount of
    subsystems. They will just need to create a new class for each subsystem since the subsystem class is abstract. The
    only function that needs to be implemented in the new class is CommandCheck(). A subsystem also has an arbitrary
    amount of motors associated with them.

    Member Variables: ArrayList<Motor> - contains all the motors for each subsystem.

                      String name - self explanatory

                      boolean status - self explanatory

    Member functions: void deplete() - each command takes away some charge.

                      double getCharge() - self explanatory

                      void setCharge(double) - self explanatory

                      boolean GetStatus() - self explanatory

                      void SetStatus(boolean) - self explanatory
     */
    abstract class Subsystem {

        //Member Variables
        public ArrayList<Motor> motors = new ArrayList<>();
        public String name;
        public boolean status;

        //Getters and Setters
        public boolean GetStatus(){ return MotorStatus() && this.status; }
        public void SetStatus(boolean _status){ this.status = _status; }
        public int GetMotorCount(){ return motors.size(); }

        //Constructors
        public Subsystem(){
            //Always starts out as false.
            this.status=false;
        }
        public Subsystem(String _name, int _motors){
            this.name=_name;
            for(int i=0; i<_motors; i++){
                Motor motor = new Motor();
                motors.add(motor);
            }
            //Always starts out as false
            this.status=false;
        }

        /*
        Description: Used to check if a command sent from controller.SendCommand is valid. Different per subsystem
        Parameters: 1. _command = The command pre-seperated on spaces.
         */
        public abstract boolean CommandCheck(String[] _command);

        /*
        Description: Used to tie into the overall status on the subsystem. THe whole subsystem status is false if one
                     motor status is false.
        Parameters: None
         */
        public boolean MotorStatus(){
            boolean temp=false;
            for(int i=0; i<motors.size();i++) {
                if(motors.get(i).GetStatus()){
                    temp = true;
                }
            }
            return temp;
        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Description: As the robot completes commands the battery will drain, thus putting a limit on the amount of
    actions you can perform. This class works entirely behind the scenes with no user interaction. This class could
    really be removed entirely, but it's kinda fun.

    Member Variables: double charge - The current charge the battery has.

                      boolean status - Battery working?

    Member functions: void deplete() - each command takes away some charge.

                      double getCharge() - self explanatory

                      void setCharge(double) - self explanatory

                      boolean GetStatus() - self explanatory

                      void SetStatus(boolean) - self explanatory
     */
    class Battery {
        public double charge;
        public boolean status;

        public boolean GetStatus(){ return this.status; }
        public void SetStatus(boolean _status){ this.status = _status; }

        public double GetCharge(){ return this.charge; }
        public void SetCharge(double _charge) {
            if (_charge > 12 || _charge < 0) {
                System.out.println("Invalid Charge");
            }

        }

        /*
        Description: Uses java Random class to take away some charge between 0.0 and 1.0
        Parameters: none
         */
        public void Deplete() {
            Random rand = new Random();
            SetCharge(GetCharge()- rand.nextDouble());
        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Description: Class responsible for reading in commands and sending to appropriate subsystem. This class in practice
    would be the bridge between software and hardware. Here we just send commands to virtual I/O channels that point
    at nothing.

    Member Variables:
                        int[] channels - The hardware abstraction channels. Each channel controls a specific motor in a
                                         subsystem.

                        boolean status - Controller working?

                        boolean manual - TRUE = manual mode / FALSE = auto mode.

                        LinkedList commandBuffer - In auto mode the user passes all the commands at one time to this
                                                   FIFO (first in - first out) buffer. THe controller will read from
                                                   here and execute commands until empty.

                        ArrayList<Subsystem> subArray - An array of all the subsystems.

    Member Functions:
                        void receiveCommand() - Reads the next command from terminal or command buffer depending on mode.
                                                Then sends the command forward to sendCommand().

                        void sendCommand(String) - Parses the command and sends to appropriate subsystem for logic checks.
                                                   If the subsystem sends back OK then the command is passed to the I/O
                                                   abstraction channels to control the motors.
     */
    class Controller{
        public int[] channels = new int[20];
        public boolean status;
        public boolean manual;
        Queue<String> commandBuffer = new LinkedList<>();
        ArrayList<Subsystem> subArray = new ArrayList<>();

        public boolean GetStatus(){ return this.status; }
        public void SetStatus(boolean _status){ this.status = _status; }
        public boolean GetManual(){ return this.manual; }
        public void SetManual(boolean _manual){ this.manual = _manual; }

        /*
        Description:This function can work two ways - manual or automatic. In the future I may implement position tracking to
                    mimic driving and scoring in the real competition arena.

        Manual:     The player enters in commands one by one into the terminal window as this program runs. The robot
                    will attempt to preform each command.

        Automatic:  Students enter in commands in the main function and send those commands directly to this function.
                    The robot will switch into manual mode after all autonomous commands have been executed.

        Parameters: None
         */
        public void ReceiveCommand(){
            System.out.println("Command received from user.");
            String command;

            //Manual mode
            if(GetManual()){
                Scanner scan = new Scanner(System.in).useDelimiter("\\n");
                while(GetStatus()) {
                    command = scan.next();
                    System.out.println("Attempting Command: " + command);
                    SendCommand(command);
                }
            }

            //Autonomous mode
            else {
                while(commandBuffer.size()>0){
                    command = commandBuffer.remove();
                    System.out.println("Attempting command: " + command);
                    SendCommand(command);
                }
                SetManual(true);
                ReceiveCommand();
            }

        }
        /*
        Description: Sends the command from user to the correct Subsystem for further processing. If the command passes
                     the subsystem check the proper motor channels will be sent a '1'. This represents an 'ON' state for the motor.
                     This is where the abstraction takes place.

        Parameters: 1. Command String
                       formatted as "<Subsystem name> <command>"
                       examples:
                        Claw grabPanel
                        Claw grabBall
                        Claw scoreBall
                        Claw scorePanel
                        Claw position1
                        Claw position2
                        Claw position3
                        Claw position4

                        Wheels left <feet> 0 means just turn 90 deg
                        Wheels right <feet>
                        Wheels forward <feet>

                        exeptions:
                        shutdown
                        gogo
                        switchMode
         */
        public void SendCommand(String _command) {
            String[] split = _command.split(" ");
            boolean check;
            int motorCount=0;
            int baseMotor;
            for(int i=0; i<subArray.size(); i++){
                baseMotor = motorCount;
                motorCount = subArray.get(i).GetMotorCount() + motorCount;
                if(subArray.get(i).name==split[0]){
                    check = subArray.get(i).CommandCheck(split);
                    if(check) {
                        for (int j = baseMotor; j <= motorCount; j++) {
                            channels[j] = 1;
                        }
                    }
                    break;
                }
            }

        }

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /*
    See the implementation of these functions in TurtleClub.java for more information.
     */
    int Init(int x);
    void Shutdown();
    boolean[] Status();


}
