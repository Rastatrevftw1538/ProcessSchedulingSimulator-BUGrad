
public class Process {
        public int id;
        protected int priority;
        public int arrivalTime;
        public int duration;
        private int timeRemaining = duration;
        private int timeWaiting = 0;

        //Constructor for creating a new process
        public Process(int id, int priority, int duration ,int arrivalTime){
            this.id = id; //ID of Process
            this.priority = priority; //Assigined priority
            this.arrivalTime = arrivalTime; //Time it arrived in the Queue
            this.duration = duration; // How long it takes to run the whole Process
            this.timeRemaining = duration; // How much longer it has to run
            this.timeWaiting = 0; //A value to track how long it has been sitting idle
        }
        //Override of toString to allow for correct formmating of Process class
        @Override
        public String toString(){
            return "Process id = "+ this.id+"\n Arrival = "+this.arrivalTime+"\n Duration = "+this.duration+"\n Run time left = "+this.timeRemaining+"\n";
        }
        /*
        Increases the time waited in the Queue for a process
        Inputs: None
        Outputs: None
        */
        protected void increaseWaitTime(){
            ++timeWaiting;
        }
        /*
        Resets the wait time of a process, usually when it has reached the maximum
        allowed to wait before being moved up in priority.
        Inputs: None
        Outputs: None
        */
        protected void restWaitTime(){
            timeWaiting=0;
        }
        /*
        Gets the time that the Process has been waiting
        Inputs: None
        Outputs: Int | timeWaiting
        */
        protected int getWaitTime(){
            return timeWaiting;
        }
        /*
        Simulates executing a Process per second by reducing the time remaining
        in a process by one.
        Inputs: None
        Outputs: Int | timeRemaining
        */
        protected void executeProcess(){
            --timeRemaining;
        }
        /*
        returns the time remaining on a Process before it is complete
        Inputs: None
        Outputs: Int | timeRemaining
        */
        protected int getTimeRemaining(){
            return timeRemaining;
        }
        /*
        Sets the priority of the Process to a new value
        Inputs: Int | newPriority
        Outputs: None
        */
        protected void setPriority(int newPriority){
            priority = newPriority;
        }
    }