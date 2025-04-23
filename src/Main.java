public class Main {

    public static void main(String[] args) {

        Object[][] Process_Info = {
                {"P1", 0, 19, new int[]{}},
                {"P2", 5, 16, new int[]{}},
                {"P3", 15, 27, new int[]{}},
                {"P4", 35, 13, new int[]{}},
                {"P5", 50, 10, new int[]{}},
                {"P6", 65, 26, new int[]{}},
                {"P7", 94, 19, new int[]{}}
        };
        int[][] IOs = {
                {18, 18}, // (start time, time to complete)
                {43, 22},
                {72, 25},
                {104, 14}
        };
        Object[][] IO_ID = {};
        Object[][] Ready_Queue = {};
        Object[][] Blocked_Queue = {};

        int Time_Quantum = 4; // This is the hard coded Time quantum value
        int time = 0; // Initial time starts at 0
        int Ready_Queue_row_index = -1; // Initial Ready_Queue_row_index starts at -1
        String bottom_numbers = "0";
        String process_order = "";
        int blocked = 0;
        for (Object[] process : Process_Info) {
            if ((int) process[1] == time) {
                Ready_Queue = addToQueue(Ready_Queue, process);
            }
        }
        Object[] current = {};
        String id = "";
        int timee = 0;
        while (!allProcessesComplete(Process_Info)) {

            current = Ready_Queue[0];
            id = (String) current[0];
            int remaining = (int) current[2];
            if (Time_Quantum == 0) {

                Time_Quantum = 4;
                Object[] firstProcess = Ready_Queue[0];
                bottom_numbers += (time > 15 ? " " : "  ") + time;

                for (Object[] process : Process_Info) {
                    if (process[0].equals(String.valueOf(id))) {
                        process[3] = append((int[]) process[3], time - timee);
                        process[3] = append((int[]) process[3], time);
                    }
                }
                timee = 0;
                Ready_Queue = removeFromQueue(Ready_Queue, 0);
                if (blocked == 0 && remaining > 0) {
                    Ready_Queue = addToQueue(Ready_Queue, firstProcess);
                }
                blocked = 0;
                process_order = process_order + "|" + id;
            } else {

                remaining = remaining - 1;
                Time_Quantum--;

                if (remaining < 0) {
                    Time_Quantum = 0;
                    blocked = 1;
                } else{
                    time++;
                    timee++;
                    for (Object[] process : Process_Info) {
                        if ((int) process[1] == time) {
                            Ready_Queue = addToQueue(Ready_Queue, process);
                        }
                    }
                    for (int i = 0; i < Blocked_Queue.length; i++) {
                        IOs[i][1] = (int) IOs[i][1] - 1;
                        if ((int) IOs[i][1] == 0) {
                            Ready_Queue = addToQueue(Ready_Queue, Blocked_Queue[i]);
                            Blocked_Queue = removeFromQueue(Blocked_Queue, 0);
                            IOs = removeIO(IOs, i);
                        }
                    }
                }
                Ready_Queue[0][2] = remaining;
                for (int i = 0; i < IOs.length; i++) {
                    if (IOs[i][0] == time) {
                        Blocked_Queue = addToQueue(Blocked_Queue, Ready_Queue[0]);
                        IO_ID = addToQueue2(IO_ID, Ready_Queue[0][0],IOs[i][1]);
                        blocked = 1;
                        Time_Quantum = 0;
                    }
                }
            }

        }
        bottom_numbers += (time > 15 ? " " : "  ") + time;
        process_order = process_order + "|" + id + "|";
        System.out.print("\n1.)\nGantt Chart:\n");
        System.out.println(process_order);
        System.out.println(bottom_numbers);

        String[] parts = process_order.replaceAll("^\\|+|\\|+$", "").split("\\|");
        String[] parts2 = bottom_numbers.trim().split("\\s+");
        int mid = parts.length / 2;
        int mid2 = parts2.length / 2;

        StringBuilder part1 = new StringBuilder(), part2 = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            (i < mid ? part1 : part2).append("||").append(parts[i]);
        }

        part1.append("|");
        part2.append("|");

        StringBuilder part11 = new StringBuilder(), part21 = new StringBuilder();

        for (int i = 0; i < parts2.length; i++) {
            int n = Integer.parseInt(parts2[i]);
            String s = parts2[i] + (n < 10 ? "   " : n < 100 ? "  " : " ");
            (i < mid2 ? part11 : part21).append(s);
        }

        String[] part11Values = part11.toString().trim().split("\\s+");
        String lastValue = part11Values[part11Values.length - 1];

        part21.insert(0, lastValue + "  ");

        System.out.println("\nI separated to two parts to make it easy to read\n");
        System.out.println("Part 1: " + part1 + "\n        " + part11.toString().trim());
        System.out.println("Part 2: " + part2 + "\n        " + part21.toString().trim());

        System.out.println("\n2.)\nWaiting Time:");
        for (int i = 0; i < Process_Info.length; i++) {
            int[] times = (int[]) Process_Info[i][3];
            System.out.print(Process_Info[i][0] + " = ");

            StringBuilder mathh = new StringBuilder();
            int total = 0;
            boolean firstTermAdded = false;

            for (int j = times.length - 2; j >= 1; j -= 2) {
                int diff = times[j] - times[j - 1];
                if (times[j] != times[j - 1]) {
                    if (firstTermAdded) mathh.append("+");
                    mathh.append("(").append(times[j]).append("-").append(times[j - 1]).append(")");
                    total += diff;
                    firstTermAdded = true;
                }
            }

            int arrivalDiff = times[0] - (int) Process_Info[i][1];
            if (firstTermAdded) mathh.append("+");
            mathh.append("(").append(times[0]).append("-").append(Process_Info[i][1]).append(")");
            total += arrivalDiff;
            firstTermAdded = true;

            for (int k = 0; k < IO_ID.length; k++) {
                if (IO_ID[k][0] == Process_Info[i][0]) {
                    total -= (int) IO_ID[k][1];
                    mathh.append("-").append(IO_ID[k][1]);
                }
            }
            System.out.println(mathh.toString() + " = " + total);
        }

        System.out.println("\n3.)\nResponse Time:");
        for (int i = 0; i < Process_Info.length; i++) {
            int[] times = (int[]) Process_Info[i][3];
            System.out.println(Process_Info[i][0] + " = " + times[0] + " - " + Process_Info[i][1] + " = " + (times[0] - (int) Process_Info[i][1]));
        }
        System.out.println("\n4.)\nTurn-Around Time:");
        for (int i = 0; i < Process_Info.length; i++) {
            int[] times = (int[]) Process_Info[i][3];
            int lastTime = times[times.length - 1]; // Get the last time value
            System.out.println(Process_Info[i][0] + " = " + lastTime + " - " + Process_Info[i][1] + " = " + (lastTime - (int) Process_Info[i][1]));
        }
    }

    // Method to append a value to an int[] array
    public static int[] append(int[] array, int value) {
        int[] newArray = new int[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = value;
        return newArray;
    }

    public static Object[][] addToQueue(Object[][] queue, Object[] process) {
        int newLength = queue.length + 1;
        Object[][] newQueue = new Object[newLength][];
        for (int i = 0; i < queue.length; i++) {
            newQueue[i] = queue[i];
        }
        newQueue[newLength - 1] = process;
        return newQueue;
    }

    public static Object[][] removeFromQueue(Object[][] queue, int index) {
        if (index < 0 || index >= queue.length) return queue;
        Object[][] newQueue = new Object[queue.length - 1][];
        int j = 0;
        for (int i = 0; i < queue.length; i++) {
            if (i != index) newQueue[j++] = queue[i];
        }
        return newQueue;
    }

    public static int[][] removeIO(int[][] arr, int index) {
        int[][] newArr = new int[arr.length - 1][];
        int j = 0;
        for (int i = 0; i < arr.length; i++) {
            if (i != index) newArr[j++] = arr[i];
        }
        return newArr;
    }
    public static boolean allProcessesComplete(Object[][] processes) {
        for (Object[] process : processes) {
            if ((int) process[2] > 0) return false;
        }
        return true;
    }
    public static Object[][] addToQueue2(Object[][] queue, Object val1, Object val2) {
        int newLength = queue.length + 1;
        Object[][] newQueue = new Object[newLength][];
        for (int i = 0; i < queue.length; i++) {
            newQueue[i] = queue[i];
        }
        newQueue[newLength - 1] = new Object[]{val1, val2};
        return newQueue;
    }
}