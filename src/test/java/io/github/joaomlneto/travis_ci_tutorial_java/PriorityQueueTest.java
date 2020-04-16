import java.util.*;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PriorityQueueTest {
    private Integer[] before;
    private Integer[] after;

    public PriorityQueueTest(Integer[] before, Integer[] after){

        this.before = before;
        this.after = after;

    }

    @Parameters
    public static Collection getTestParameters (){
        return Arrays.asList(new Integer[][][] {
                {{1,2,3,4},{1,2,3,4}},
                {{6,-3,0,9},{-3,0,6,9}},
                {{4,3,2,1},{1,2,3,4}},
                {{6,8,-1,2,3},{-1,2,3,6,8}},
                {{19,-10,2,54,7},{-10,2,7,19,54}}
        });
    }

    @Test
    public void testPoll(){

        PriorityQueue que = new PriorityQueue();

        for(int i = 0; i < before.length; i++){
            que.add(before[i]);
        }

        Integer[] afterPoll = {};
        for(int i = 0; i < before.length; i++){
            afterPoll = append(afterPoll, (int)que.poll());
        }

        assertEquals(after,afterPoll);
    }

    private static Integer[] append(Integer[] arr, int element){
        List<Integer> list = new ArrayList<>(Arrays.asList(arr));
        list.add(element);

        return list.toArray(new Integer[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenIllegalArgumentExceptionThrown_thenExpectationSatisfied() {
        PriorityQueue queue = new PriorityQueue(0);
    }

    @Test(expected = NullPointerException.class)
    public void whenNullPointerExceptionThrown_thenExpectationSatisfied() {
        PriorityQueue queue = new PriorityQueue();
        queue.offer(null);
    }

    @Test(expected = NoSuchElementException.class)
    public void whenNoSuchElementExceptionThrown_thenExpectationSatisfied() {
        PriorityQueue queue = new PriorityQueue();
        queue.remove();
    }
}
