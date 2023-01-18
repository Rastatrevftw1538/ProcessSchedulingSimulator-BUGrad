import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;
import net.datastructures.List;
import net.datastructures.ArrayList;
import net.datastructures.Entry;
import net.datastructures.HeapAdaptablePriorityQueue;

//values and attributes for Process Schedular
public class ProcessScheduling {
    //Priority Queue that is prioritized by lowest Integar. This Priority Queue is
    //structured by means of a Heap and inherets the properties of an Adaptable Priority Queue
    //meaning keys and values can be replaced within the Priority Queue.
    static HeapAdaptablePriorityQueue<Integer,Process> scheduledProcess = new HeapAdaptablePriorityQueue<Integer,Process>();
    /*
    Max amount of time a process can sit idle in queue before increasing in
    priority
    */
    static int maxWaitTime = 30;
    //The current time that has passed
    static int currentTime;
    //value to hold the current running Process
    static Process runningProcess;
    //value to hold the calcualation of average wait time
    static double averageWaitTime;
    //value to hold the size of the queue, for averaging
    static int sizeOfQueue;
    
    public static void main(String[] args) {
        //Creating an ArrayList for the Processes that will be scanned from the input.
        //ArrayList will be the quickest to iterate through and will be used only for placing into
        //the main Priority Queue.
        List<Process> listOfProcesses = new ArrayList<>();
        //Variable that holds all the messages outputed to the console.
        String outputString = "";

        /*
         * This will read the input of "process_scheduling_input.txt" and split the data accordingly:
         * ID
         * Priority
         * Process Duration
         * Time of Arrival
         * 
         * After splitting the data, then it will be added to the ArrayList of Processes.
         */
        try{
            File file = new File("process_scheduling_input.txt");
            Scanner fileReader = new Scanner(file);
            while (fileReader.hasNextLine()){
                String data = fileReader.nextLine();
                String stripedData = data.replace(" ", ",");
                String[] splitData = stripedData.split(",");
                listOfProcesses.add(0, new Process(Integer.parseInt(splitData[0]),Integer.parseInt(splitData[1]),Integer.parseInt(splitData[2]),Integer.parseInt(splitData[3])));
                outputString += "Id = "+listOfProcesses.get(0).id+", priority = "+listOfProcesses.get(0).priority+", duration = "+listOfProcesses.get(0).duration+", arrival time = "+listOfProcesses.get(0).arrivalTime+"\n";
                System.out.println("Id = "+listOfProcesses.get(0).id+", priority = "+listOfProcesses.get(0).priority+", duration = "+listOfProcesses.get(0).duration+", arrival time = "+listOfProcesses.get(0).arrivalTime);
            }
            fileReader.close();
            sizeOfQueue = listOfProcesses.size();
            outputString += "\nMaximum wait time = "+maxWaitTime+"\n";
            System.out.println("\nMaximum wait time = "+maxWaitTime+"\n");
            } 
            catch (FileNotFoundException exception){
                System.out.println("An error occurred.");
                exception.printStackTrace();
            }

            /*
             * The loop for running a process.
             * While either of the Data Structures have values in them,
             * we increase the current time and move a Process from the scanned Processes list
             * to the Priority Queue scheduledProcess when it's arrival time equals that of the current time
             */
            while(!listOfProcesses.isEmpty() || !scheduledProcess.isEmpty()){
                currentTime++;
                for (int i = 0; i<listOfProcesses.size(); ++i) {
                    if(listOfProcesses.get(i).arrivalTime==currentTime){
                        scheduledProcess.insert(listOfProcesses.get(i).priority, listOfProcesses.get(i));
                        listOfProcesses.remove(i);
                    }
                }
                /*
                 * While we still have values in the scheduledProcess Queue,
                 * we assign what the highest priority Process is to the running process
                 * and announce we are running it with all of its values.
                 */
                if(!scheduledProcess.isEmpty()){
                    if(runningProcess != scheduledProcess.min().getValue()){
                        runningProcess = scheduledProcess.min().getValue();
                        outputString += "Now running "+runningProcess.toString()+"at time "+currentTime+"\n";
                        System.out.println("Now running "+runningProcess.toString()+"at time "+currentTime);
                    }
                    /*
                     * If the running Process still has time to run, it will execute and announce
                     * the time it executed and how much time it has left.
                     */
                    if(runningProcess.getTimeRemaining()>0){
                        runningProcess.executeProcess();
                        outputString += "Executed process ID: "+runningProcess.id+", at time "+currentTime+" Remaining: "+runningProcess.getTimeRemaining()+"\n";
                        System.out.println("Executed process ID: "+runningProcess.id+", at time "+currentTime+" Remaining: "+runningProcess.getTimeRemaining());
                    }
                    /*
                     * Once time is up for the running Process, we announce that it is finished
                     * and remove it from the scheduledProcess Priority Queue.
                     */
                    if(runningProcess.getTimeRemaining()==0){
                        outputString +="Finished running "+runningProcess.toString()+"\n";
                        System.out.println("Finished running "+runningProcess.toString());
                        scheduledProcess.removeMin();
                    }
                }
                /*
                * Iterating through the Priority Queue in order of priority to determine which
                * Processes have been waiting too long. We use and iterator since we only want to go
                * in one direction and do not care for the index of the Process.
                */
                Iterator<Entry<Integer,Process>> waitQueue = scheduledProcess.iterator();
                if (waitQueue.hasNext()){
                    //Skip the first Entry since this will be the one that is being processed.
                    Entry<Integer,Process> waitingProcess = waitQueue.next();
                    while (waitQueue.hasNext()){
                        waitingProcess = waitQueue.next();
                        /*
                        * If this Entries wait time exceeds the allowed wait time per process, 
                        * we decrement the Key(priority) by one to move it up in priority, then
                        * reset it's waiting value and announce that the process has been moved up
                        * in the Priority Queue.
                        */
                        if(waitingProcess.getValue().getWaitTime() > maxWaitTime){
                            waitingProcess.getValue().setPriority(--waitingProcess.getValue().priority);
                            scheduledProcess.replaceKey(waitingProcess, waitingProcess.getValue().priority);

                            /* 
                             * At this time we also add the time it has been waiting to the average time
                             * holding variable to then calculate at the end.
                            */
                            averageWaitTime+=waitingProcess.getValue().getTimeRemaining();

                            waitingProcess.getValue().restWaitTime();
                            outputString +="Process "+waitingProcess.getValue().id+" reached maximum wait time... decreasing priority to "+waitingProcess.getValue().priority +"\n";
                            System.out.println("Process "+waitingProcess.getValue().id+" reached maximum wait time... decreasing priority to "+waitingProcess.getValue().priority);
                        }
                        //if it is still under the wait time, just increase it's wait time.
                        else{
                            waitingProcess.getValue().increaseWaitTime();
                        }
                    }
                }
        }
        /*
         * Once the Processes are complete, announce that it is finished and the calculation
         * of the average wait times. Then save all of the information printed to outputString
         * and write it to "process_scheduling_output.txt". If this file does not exist, then create that
         * file.
         */
        outputString +="Finished running all processes at time "+currentTime+"\n"+"Average wait time: "+ averageWaitTime/sizeOfQueue;
        System.out.println("Finished running all processes at time "+currentTime);
        System.out.println("Average wait time: "+ averageWaitTime/sizeOfQueue);
        try{
            File file = new File("process_scheduling_output.txt");
            FileWriter myWriter = new FileWriter(file.getName());
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
                } 
            else {
                System.out.println("File already exists.");
            }
            myWriter.write(outputString);
            myWriter.close();
        }
        catch (IOException  exception) {
            System.out.println("An error occurred.");
            exception.printStackTrace();
        }
    }
}
