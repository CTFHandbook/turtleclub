public class Motor {
    public double speed;
    public boolean running;
    public boolean status;

    public boolean GetStatus() { return status; }
    public void SetStatus(boolean _status) { this.status = _status; }

    public void setSpeed(double _speed) {
        if (_speed > 1 || _speed < 0) {
            System.out.println("Invalid speed");
        } else {
            this.speed = _speed;
        }
    }
    public double getSpeed(){ return this.speed; }

    public void SetRunning(boolean _running){ this.running = _running; }
    public boolean GetRunning(){ return this.running; }

}
